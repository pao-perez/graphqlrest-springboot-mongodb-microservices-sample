# Note: You can use any Debian/Ubuntu based image you want. 
FROM mcr.microsoft.com/vscode/devcontainers/base:focal

# [Option] Install zsh
ARG INSTALL_ZSH="false"
# [Option] Upgrade OS packages to their latest versions
ARG UPGRADE_PACKAGES="false"
# [Option] Enable non-root Docker access in container
ARG ENABLE_NONROOT_DOCKER="true"

# Install needed packages and setup non-root user. Use a separate RUN statement to add your own dependencies.
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG SOURCE_SOCKET=/var/run/docker-host.sock
ARG TARGET_SOCKET=/var/run/docker.sock
COPY library-scripts/*.sh /tmp/library-scripts/
RUN apt-get update \
    && /bin/bash /tmp/library-scripts/common-debian.sh "${INSTALL_ZSH}" "${USERNAME}" "${USER_UID}" "${USER_GID}" "${UPGRADE_PACKAGES}" \
    # Use Docker script from script library to set things up
    && /bin/bash /tmp/library-scripts/docker-debian.sh "${ENABLE_NONROOT_DOCKER}" "${SOURCE_SOCKET}" "${TARGET_SOCKET}" "${USERNAME}" \
    # Clean up
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/* /tmp/library-scripts/

# Setting the ENTRYPOINT to docker-init.sh will configure non-root access to 
# the Docker socket if "overrideCommand": false is set in devcontainer.json. 
# The script will also execute CMD if you need to alter startup behaviors.
ENTRYPOINT [ "/usr/local/share/docker-init.sh" ]
CMD [ "sleep", "infinity" ]

# Install additional OS packages
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends openjdk-11-jdk maven

# Install gcloud
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | \
    tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | \
    apt-key --keyring /usr/share/keyrings/cloud.google.gpg add - && apt-get update -y && apt-get install google-cloud-sdk -y
