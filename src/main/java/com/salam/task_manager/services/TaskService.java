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



    @Transactional
    public TaskDto updateTask(Long taskId, TaskDto updatedTaskDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel existingTask = taskRepository.findByIdAndUser(taskId, user) // Fetch task by ID and user
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        // Update only the provided fields
        if (updatedTaskDto.getTitle() != null) {
            existingTask.setTitle(updatedTaskDto.getTitle());
        }
        if (updatedTaskDto.getDescription() != null) {
            existingTask.setDescription(updatedTaskDto.getDescription());
        }
        if (updatedTaskDto.getDueDate() != null) {
            existingTask.setDueDate(updatedTaskDto.getDueDate());
        }
        existingTask.setCompleted(updatedTaskDto.isCompleted());

        return taskMapper.toDto(taskRepository.save(existingTask));
    }


    @Transactional
    public void deleteTask(Long taskId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel taskToDelete = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        taskRepository.delete(taskToDelete);
    }

    @Transactional
    public void deleteTasks(List<Long> taskIds, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        List<TaskModel> tasksToDelete = taskRepository.findAllByIdInAndUser(taskIds, user);

        if (tasksToDelete.size() != taskIds.size()) {
            throw new ResourceNotFoundException("One or more tasks not found or do not belong to the user.", "", "");
        }

        taskRepository.deleteAll(tasksToDelete);
    }
}
