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
import com.salam.task_manager.repository.mongodb.MongoDbTaskRepository;
import com.salam.task_manager.repository.mongodb.MongoDbUserRepository;
import com.salam.task_manager.repository.postgresql.PostgresTaskRepository;
import com.salam.task_manager.repository.postgresql.PostgresqlUserRepository;
import com.salam.task_manager.utils.ProfileNameProvider;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private final TaskMapper taskMapper;

    private final ProfileNameProvider profileNameProvider;

    @Autowired
    public TaskService(UserRepository userRepository,
                       TaskMapper taskMapper,
                       ProfileNameProvider profileNameProvider) {
        this.taskMapper = taskMapper;
        this.profileNameProvider = profileNameProvider;
    }

    @Autowired
    public void setTaskRepository(List<TaskRepository> repositories) {
        String activeProfile = profileNameProvider.getActiveProfileName();

        this.taskRepository = repositories.stream()
                .filter(repository ->
                        (activeProfile.equals("mongodb") && repository instanceof MongoDbTaskRepository) ||
                                (activeProfile.equals("postgresql") && repository instanceof PostgresTaskRepository)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No repository found for active profile: " + activeProfile));
    }

    @Autowired
    public void setUserRepository(List<UserRepository> repositories) {
        String activeProfile = profileNameProvider.getActiveProfileName();

        this.userRepository = repositories.stream()
                .filter(repository ->
                        (activeProfile.equals("mongodb") && repository instanceof MongoDbUserRepository) ||
                                (activeProfile.equals("postgresql") && repository instanceof PostgresqlUserRepository)
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No user repository found for active profile: " + activeProfile));
    }

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
    public TaskResponseDto updateTask(String taskId, TaskRequestUpdatdeDto updatedTaskDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel existingTask = taskRepository.findByIdAndUser(taskId, user) // Fetch task by ID and user
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        // Update only the provided fields
        taskMapper.updateTaskFromDto(updatedTaskDto, existingTask);
        TaskModel updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    // Delete task by id
    @Transactional
    public void deleteTask(String taskId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        TaskModel taskToDelete = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: ", taskId.toString(), ""));

        taskRepository.delete(taskToDelete);
    }

    // Bulk delete
    @Transactional
    public void deleteTasks(List<String> taskIds, String username) {
        com.salam.task_manager.models.user.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));

        List<TaskModel> tasksToDelete = taskRepository.findAllByIdInAndUser(taskIds, user);

        if (tasksToDelete.size() != taskIds.size()) {
            throw new ResourceNotFoundException("One or more tasks not found or do not belong to the user.", "", "");
        }

        taskRepository.deleteAll(tasksToDelete);
    }

}
