package com.salam.task_manager.services;

import com.salam.task_manager.dto.TaskRequestUpdatdeDto;
import com.salam.task_manager.dto.TaskResponseDto;
import com.salam.task_manager.dto.TaskRequestDto;
import com.salam.task_manager.exception.ResourceNotFoundException;
import com.salam.task_manager.mapper.TaskMapper;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.TaskStatus;
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

    // Create task
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));
        System.out.println(taskRequestDto);
        TaskModel task = taskMapper.requestDtoToEntity(taskRequestDto, user);
        System.out.println(task);
        task.setUser(user);
        task.setStatus(TaskStatus.PENDING);
        return taskMapper.toDto(taskRepository.save(task));
    }

    // get user's tasks
    public List<TaskResponseDto> getTasksForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        List<TaskModel> tasks = taskRepository.findByUser(user);

        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    // Update user's task
    @Transactional
    public TaskResponseDto updateTask(Long taskId, TaskRequestUpdatdeDto updatedTaskDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel existingTask = taskRepository.findByIdAndUser(taskId, user) // Fetch task by ID and user
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        // Update only the provided fields
        taskMapper.updateTaskFromDto(updatedTaskDto, existingTask);
        TaskModel updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel taskToDelete = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        taskRepository.delete(taskToDelete);
    }

}
