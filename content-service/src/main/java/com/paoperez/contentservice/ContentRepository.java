package com.paoperez.contentservice;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ContentRepository extends MongoRepository<Content, String> {}
