package com.salam.task_manager.repository.postgresql;


import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.TaskRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("postgresqlTaskRepository")
@Profile("postgresql")
public interface PostgresTaskRepository extends TaskRepository, JpaRepository<TaskModel, String> {
}