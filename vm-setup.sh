#!/bin/bash

# Exit as soon as an error is encountered
set -e
# Enable debug mode/printing of all executed commands
set -x

DEPLOYMENT_ENV=$1
PROJECT_ID=$2
SOURCE_IP_ADDRESS=$3 # the static ip attached to the external https load balancer (non-development env setup)
DOMAIN=$4
REGION=asia-southeast1
ZONE=$REGION-a
PROJECT_NUMBER=$(gcloud projects describe ${PROJECT_ID} --format="value(projectNumber)")
COMPUTE_SERVICEACCOUNT=$PROJECT_NUMBER-compute@developer.gserviceaccount.com
RESOURCE_TAG=$DEPLOYMENT_ENV-contentually

echo "Start $DEPLOYMENT_ENV Setup"

if [[ $DEPLOYMENT_ENV == "" ]]; then
    echo "DEPLOYMENT_ENV is invalid. Exiting setup script."
    exit 1;
fi

if [[ $PROJECT_ID == "" ]]; then
    echo "PROJECT_ID is invalid. Exiting setup script."
    exit 1;
fi

if [[ $SOURCE_IP_ADDRESS == "" ]]; then
    echo "SOURCE_IP_ADDRESS is invalid. Exiting setup script."
    exit 1;
fi

if [[ $DEPLOYMENT_ENV != "development" ]] && [[ $DOMAIN == "" ]]; then
    echo "DOMAIN is invalid. Exiting setup script."
    exit 1;
fi

# Enable Secret Manager api
gcloud services enable secretmanager.googleapis.com --project=$PROJECT_ID

# Create mongo secrets
gcloud secrets --project=$PROJECT_ID create mongo-username --data-file=./secrets/mongo_username --locations=$REGION --replication-policy=user-managed
gcloud secrets --project=$PROJECT_ID create mongo-password --data-file=./secrets/mongo_password --locations=$REGION --replication-policy=user-managed

# Grant Secret Accessor role to Compute Engine service account
gcloud secrets --project=$PROJECT_ID add-iam-policy-binding mongo-username \
    --member="serviceAccount:${COMPUTE_SERVICEACCOUNT}" \
    --role="roles/secretmanager.secretAccessor"

# Create bucket and upload root docker-compose file
BUCKET=$RESOURCE_TAG-bucket
gsutil mb -p $PROJECT_ID -l $REGION gs://$BUCKET/
gsutil cp ./docker-compose.yaml gs://$BUCKET

# Build and upload container images
gcloud services enable cloudbuild.googleapis.com --project=$PROJECT_ID
cd ./avatar-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/avatar-service:0.0.1 --project=$PROJECT_ID
cd ./db && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/avatar-db:0.0.1 --project=$PROJECT_ID
cd ../../image-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/image-service:0.0.1 --project=$PROJECT_ID
cd ./db && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/image-db:0.0.1 --project=$PROJECT_ID
cd ../../category-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/category-service:0.0.1 --project=$PROJECT_ID
cd ./db && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/category-db:0.0.1 --project=$PROJECT_ID
cd ../../content-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/content-service:0.0.1 --project=$PROJECT_ID
cd ./db && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/content-db:0.0.1 --project=$PROJECT_ID
cd ../../graphql-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/graphql-service:0.0.1 --project=$PROJECT_ID
cd ../discovery-service && gcloud builds submit --tag asia.gcr.io/$PROJECT_ID/discovery-service:0.0.1 --project=$PROJECT_ID
cd ..

# Create data disk
DATA_DISK=$RESOURCE_TAG-disk-data
gcloud compute --project=$PROJECT_ID disks create $DATA_DISK \
    --zone=$ZONE \
    --size=10 \
    --type=pd-standard

# Create vm instance
VM_INSTANCE=$RESOURCE_TAG-instance
SUBNET=default
HTTP_PORT=8080
EUREKA_PORT=8761
HTTP_TARGET_TAG=http-server
gcloud compute --project=$PROJECT_ID instances create $VM_INSTANCE \
    --zone=$ZONE \
    --machine-type=n1-standard-1 \
    --subnet=$SUBNET \
    --network-tier=PREMIUM \
    --metadata-from-file=startup-script=vm-setup-startup-script.sh \
    --metadata=deployment-env=$DEPLOYMENT_ENV \
    --maintenance-policy=MIGRATE \
    --service-account=$COMPUTE_SERVICEACCOUNT \
    --scopes=cloud-platform \
    --tags=$HTTP_TARGET_TAG \
    --image=ubuntu-minimal-1804-bionic-v20201014 \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=10GB \
    --boot-disk-type=pd-standard \
    --boot-disk-device-name=$VM_INSTANCE-disk-boot \
    --disk=name=$DATA_DISK,device-name=$DATA_DISK,mode=rw,boot=no,auto-delete=no \
    --shielded-secure-boot \
    --shielded-vtpm \
    --shielded-integrity-monitoring \
    --reservation-affinity=any

if [[ $DEPLOYMENT_ENV != "development" ]]; then
    # Create network endpoint group
    NEG=$RESOURCE_TAG-neg
    gcloud compute --project=$PROJECT_ID network-endpoint-groups create $NEG \
        --zone=$ZONE \
        --subnet=$SUBNET \
        --network-endpoint-type=GCE_VM_IP_PORT \
        --default-port=$HTTP_PORT

    # Get vm instance internal/primary IP
    VM_INSTANCE_PRIMARY_IP=$(gcloud compute --project=$PROJECT_ID instances describe $VM_INSTANCE --zone=$ZONE --format='get(networkInterfaces[0].networkIP)')

    # Attach vm instance endpoints as network endpoint to network endpoint group
    gcloud compute --project=$PROJECT_ID network-endpoint-groups update $NEG \
        --zone=$ZONE \
        --add-endpoint=instance=$VM_INSTANCE,ip=$VM_INSTANCE_PRIMARY_IP,port=$HTTP_PORT \
        --add-endpoint=instance=$VM_INSTANCE,ip=$VM_INSTANCE_PRIMARY_IP,port=$EUREKA_PORT

    # Create http health check
    HTTP_HEALTH_CHECK=$RESOURCE_TAG-health-check-http
    gcloud beta compute --project=$PROJECT_ID health-checks create http $HTTP_HEALTH_CHECK \
        --use-serving-port \
        --request-path=/ \
        --proxy-header=NONE \
        --check-interval=10 \
        --timeout=10 \
        --unhealthy-threshold=3 \
        --healthy-threshold=2 \
        --no-enable-logging # avail in beta only at time of writing

    # Create firewall to allow health check from Google Cloud health checking systems to vm instance on port 8080
    gcloud compute --project=$PROJECT_ID firewall-rules create $RESOURCE_TAG-fw-allow-lb-health-check-http-traffic \
        --direction=INGRESS \
        --priority=1000 \
        --network=default \
        --action=ALLOW \
        --rules=tcp:$HTTP_PORT,tcp:$EUREKA_PORT \
        --source-ranges=130.211.0.0/22,35.191.0.0/16 \
        --target-tags=$HTTP_TARGET_TAG

    # Create load balancer backend service
    LB_BE=$RESOURCE_TAG-lb-be
    gcloud compute --project=$PROJECT_ID backend-services create $LB_BE \
        --protocol=HTTP \
        --port-name=http \
        --health-checks=$HTTP_HEALTH_CHECK \
        --global-health-checks \
        --global

    # Add network endpoint group as backend to load balancer backend service
    gcloud compute --project=$PROJECT_ID backend-services add-backend $LB_BE \
        --network-endpoint-group=$NEG \
        --network-endpoint-group-zone=$ZONE \
        --balancing-mode=RATE \
        --max-rate-per-endpoint=100 \
        --global

    # Create load balancer URL map that will route requests to load balancer backend service
    LB_URL_MAP=$RESOURCE_TAG-lb
    gcloud compute --project=$PROJECT_ID url-maps create $LB_URL_MAP \
        --default-service=$LB_BE

    # Create SSL certificate
    SSL_CERTIFICATE=$RESOURCE_TAG-ssl-certificate
    gcloud compute --project=$PROJECT_ID ssl-certificates create $SSL_CERTIFICATE \
        --domains=$DOMAIN \
        --global

    # Create load balancer https target proxy that will route requests to load balancer url map and attach ssl certificate
    LB_HTTPS_TARGET_PROXY=$RESOURCE_TAG-lb-target-proxy-https
    gcloud compute --project=$PROJECT_ID target-https-proxies create $LB_HTTPS_TARGET_PROXY \
        --url-map=$LB_URL_MAP \
        --ssl-certificates=$SSL_CERTIFICATE

    # Create load balancer forwarding rule to route incoming requests to the load balancer https target proxy
    gcloud compute --project=$PROJECT_ID forwarding-rules create $RESOURCE_TAG-lb-forwarding-rule-https \
        --address=$SOURCE_IP_ADDRESS \
        --target-https-proxy=$LB_HTTPS_TARGET_PROXY \
        --ports=443 \
        --global
fi

echo "$DEPLOYMENT_ENV Setup Complete"