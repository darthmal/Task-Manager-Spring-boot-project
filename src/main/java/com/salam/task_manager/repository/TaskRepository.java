package com.salam.task_manager.repository;


import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<TaskModel, String> {
    List<TaskModel> findByUser(User user);
    Optional<TaskModel> findByIdAndUser(String taskId, User user);
    List<TaskModel> findAllByIdInAndUser(List<String> taskIds, User user);
}