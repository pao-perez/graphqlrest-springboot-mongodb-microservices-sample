package com.paoperez.imageservice;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ImageRepository extends MongoRepository<Image, String> {
}
