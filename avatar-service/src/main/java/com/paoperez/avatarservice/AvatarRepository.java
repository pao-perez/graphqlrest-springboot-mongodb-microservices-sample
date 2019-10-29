package com.paoperez.avatarservice;

import org.springframework.data.mongodb.repository.MongoRepository;

interface AvatarRepository extends MongoRepository<Avatar, String> {
    Avatar findByUserName(String userName);
}
