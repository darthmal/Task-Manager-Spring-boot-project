package com.salam.task_manager.services;

import com.salam.task_manager.dto.TaskDto;
import com.salam.task_manager.exception.ResourceNotFoundException;
import com.salam.task_manager.mapper.TaskMapper;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.TaskRepository;
import com.salam.task_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskDto createTask(TaskDto taskDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel task = taskMapper.toEntity(taskDto, user);
        task.setUser(user);
        task.setCompleted(false);
        return taskMapper.toDto(taskRepository.save(task));
    }

    public List<TaskDto> getTasksForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        List<TaskModel> tasks = taskRepository.findByUser(user);

        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

}
