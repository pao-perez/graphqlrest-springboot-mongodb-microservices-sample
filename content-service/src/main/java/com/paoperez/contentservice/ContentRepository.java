package com.paoperez.contentservice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, String> {
}
