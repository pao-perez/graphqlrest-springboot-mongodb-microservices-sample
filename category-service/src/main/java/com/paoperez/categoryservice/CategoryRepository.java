package com.paoperez.categoryservice;

import org.springframework.data.mongodb.repository.MongoRepository;

interface CategoryRepository extends MongoRepository<Category, String> {
}
