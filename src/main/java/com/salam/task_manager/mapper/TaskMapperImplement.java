package com.salam.task_manager.mapper;

import com.salam.task_manager.dto.TaskDto;
import com.salam.task_manager.exception.ResourceNotFoundException;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImplement implements TaskMapper{

    private final UserRepository userRepository;

    public TaskMapperImplement(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public TaskDto toDto(TaskModel task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setStatus(task.getStatus());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setUserId(task.getUser().getId());
        taskDto.setDueDate(task.getDueDate());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setUpdatedAt(task.getUpdatedAt());
        return taskDto;
    }

    @Override
    public TaskModel toEntity(TaskDto taskDto, User user) {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(taskDto.getId());
        taskModel.setUser(user);
        taskModel.setTitle(taskDto.getTitle());
        taskModel.setDescription(taskDto.getDescription());
        taskModel.setStatus(taskDto.getStatus());
        taskModel.setCreatedAt(taskDto.getCreatedAt());
        taskModel.setUpdatedAt(taskDto.getUpdatedAt());
        taskModel.setDueDate(taskDto.getDueDate());
        return taskModel;
    }
}
