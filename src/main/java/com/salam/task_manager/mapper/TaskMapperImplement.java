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
        System.out.println(task);
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setCompleted(task.isCompleted());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setUserId(task.getUser().getId());
        taskDto.setDueDate(task.getDueDate());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setUpdatedAt(task.getUpdatedAt());
        System.out.println(taskDto);
        System.out.println("Id");
        System.out.println(task.getUser().getId());
        return taskDto;
    }

    @Override
    public TaskModel toEntity(TaskDto taskDto, User user) {
        System.out.println(taskDto);
//        User user = userRepository.findById(Math.toIntExact(taskDto.getUserId()))
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id", "", ""));
        TaskModel taskModel = new TaskModel();
        taskModel.setId(taskDto.getId());
        taskModel.setUser(user);
        taskModel.setTitle(taskDto.getTitle());
        taskModel.setDescription(taskDto.getDescription());
        taskModel.setCompleted(taskDto.isCompleted());
        taskModel.setCreatedAt(taskDto.getCreatedAt());
        taskModel.setUpdatedAt(taskDto.getUpdatedAt());
        taskModel.setDueDate(taskDto.getDueDate());
        System.out.println(taskModel);
        return taskModel;
    }
}
