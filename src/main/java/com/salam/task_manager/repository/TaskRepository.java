package com.salam.task_manager.repository;


import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long> {

    List<TaskModel> findByUser(User user);

    Optional<TaskModel> findByIdAndUser(Long taskId, User user);

    List<TaskModel> findAllByIdInAndUser(List<Long> taskIds, User user);
}