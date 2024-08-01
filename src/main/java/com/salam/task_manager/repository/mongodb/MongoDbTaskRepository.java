package com.salam.task_manager.repository.mongodb;

import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("mongodbTaskRepository")
@Profile("mongodb")
public interface MongoDbTaskRepository extends TaskRepository, MongoRepository<TaskModel, String> {
}
