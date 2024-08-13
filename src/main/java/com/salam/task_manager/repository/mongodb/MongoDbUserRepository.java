package com.salam.task_manager.repository.mongodb;

import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("mongodbUserRepository")
@Profile("mongodb")
public interface MongoDbUserRepository extends UserRepository, MongoRepository<User, String> {
}
