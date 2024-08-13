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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository TaskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private TaskModel task;
    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;
    private TaskRequestUpdatdeDto taskRequestUpdatdeDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1L");
        user.setUsername("testuser");

        task = new TaskModel();
        task.setId("1L");
        task.setTitle("Test Task");
        task.setUser(user);

        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");

        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId("1L");
        taskResponseDto.setTitle("Test Task");
        taskResponseDto.setUserId("1L");

        taskRequestUpdatdeDto = new TaskRequestUpdatdeDto();
        taskRequestUpdatdeDto.setTitle("Updated Task Title");
        taskRequestUpdatdeDto.setDescription("Updated Task Description");
        taskRequestUpdatdeDto.setDueDate(Date.from(ZonedDateTime.now().plusDays(1).toInstant()));
        taskRequestUpdatdeDto.setStatus(TaskStatus.DONE);
    }

    @Test
    void testCreateTask_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskMapper.requestDtoToEntity(taskRequestDto, user)).thenReturn(task);
        when(TaskRepository.save(any(TaskModel.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto createdTask = taskService.createTask(taskRequestDto, user.getUsername());

        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(createdTask.getUserId()).isEqualTo(user.getId());
        verify(TaskRepository, times(1)).save(any(TaskModel.class));
    }

    @Test
    void testCreateTask_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(taskRequestDto, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username:  not found with testuser: ");

        verify(TaskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void testGetTasksForUser_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findByUser(user)).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        List<TaskResponseDto> tasks = taskService.getTasksForUser(user.getUsername());

        assertThat(tasks).isNotNull();
        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo(task.getTitle());
    }

    @Test
    void testGetTasksForUser_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTasksForUser(user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username:  not found with testuser: ");
    }

    @Test
    void testUpdateTask_Success() {
        String taskId = "1L";

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));
        when(TaskRepository.save(any(TaskModel.class))).thenAnswer(invocation -> {
            TaskModel taskToUpdate = invocation.getArgument(0);
            taskToUpdate.setTitle(taskRequestUpdatdeDto.getTitle());
            taskToUpdate.setDescription(taskRequestUpdatdeDto.getDescription());
            taskToUpdate.setDueDate(taskRequestUpdatdeDto.getDueDate());
            taskToUpdate.setStatus(taskRequestUpdatdeDto.getStatus());
            return taskToUpdate;
        });

        // Update taskResponseDto to reflect updated values
        taskResponseDto.setTitle(taskRequestUpdatdeDto.getTitle());
        taskResponseDto.setDescription(taskRequestUpdatdeDto.getDescription());
        taskResponseDto.setDueDate(taskRequestUpdatdeDto.getDueDate());
        taskResponseDto.setStatus(taskRequestUpdatdeDto.getStatus());

        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto updatedTaskResponseDto = taskService.updateTask(taskId, taskRequestUpdatdeDto, user.getUsername());

        assertThat(updatedTaskResponseDto.getTitle()).isEqualTo(taskRequestUpdatdeDto.getTitle());
        assertThat(updatedTaskResponseDto.getDescription()).isEqualTo(taskRequestUpdatdeDto.getDescription());
        assertThat(updatedTaskResponseDto.getDueDate()).isEqualTo(taskRequestUpdatdeDto.getDueDate());
        assertThat(updatedTaskResponseDto.getStatus()).isEqualTo(taskRequestUpdatdeDto.getStatus());

        verify(TaskRepository, times(1)).save(any(TaskModel.class));
    }

    @Test
    void testUpdateTask_UserNotFound() {
        String taskId = "1L";
        TaskRequestUpdatdeDto updatedTaskDto = new TaskRequestUpdatdeDto();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskId, updatedTaskDto, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username:  not found with testuser: ");

        verify(TaskRepository, never()).findByIdAndUser(anyString(), any(User.class));
        verify(TaskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void testUpdateTask_TaskNotFound() {
        String taskId = "1L";
        TaskRequestUpdatdeDto updatedTaskDto = new TaskRequestUpdatdeDto();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskId, updatedTaskDto, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with ID:  not found with " + taskId.toString());

        verify(TaskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void testDeleteTask_Success() {
        String taskId = "1L";
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");
        TaskModel taskToDelete = new TaskModel();
        taskToDelete.setId(taskId);
        taskToDelete.setUser(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(taskToDelete));
        doNothing().when(TaskRepository).delete(taskToDelete);

        taskService.deleteTask(taskId, user.getUsername());

        verify(TaskRepository, times(1)).delete(taskToDelete);
    }

    @Test
    void testDeleteTask_UserNotFound_ThrowsException() {
        String taskId = "1L";
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty()); // Simulate user not found

        assertThatThrownBy(() -> taskService.deleteTask(taskId, username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(TaskRepository, never()).delete(any(TaskModel.class)); // No deletion should occur
    }

    @Test
    void testDeleteTask_TaskNotFound_ThrowsException() {
        String taskId = "999L";
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty()); // Task not found

        assertThatThrownBy(() -> taskService.deleteTask(taskId, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(TaskRepository, never()).delete(any(TaskModel.class));
    }

    @Test
    void testDeleteTasks_Success() {
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");

        List<String> taskIds = List.of("1L", "2L");
        List<TaskModel> tasksToDelete = taskIds.stream()
                .map(id -> {
                    TaskModel task = new TaskModel();
                    task.setId(id);
                    task.setUser(user);
                    return task;
                })
                .collect(Collectors.toList());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findAllByIdInAndUser(taskIds, user)).thenReturn(tasksToDelete);
        doNothing().when(TaskRepository).deleteAll(tasksToDelete);

        taskService.deleteTasks(taskIds, user.getUsername());

        verify(TaskRepository, times(1)).deleteAll(tasksToDelete);
    }

    @Test
    void testDeleteTasks_UserNotFound_ThrowsException() {
        List<String> taskIds = List.of("1L", "2L");
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTasks(taskIds, username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(TaskRepository, never()).deleteAll(any());
    }

    // Helper method to create TaskModel instances
    private TaskModel createTaskModel(String id, User user) {
        TaskModel task = new TaskModel();
        task.setId(id);
        task.setUser(user);
        return task;
    }

    @Test
    void testDeleteTasks_SomeTasksNotFound_ThrowsException() {
        User user = new User();
        user.setId("1L");
        user.setUsername("testuser");

        List<String> taskIds = List.of("1L", "2L", "3L"); // Requesting to delete 3 tasks
        List<TaskModel> existingTasks = List.of(
                createTaskModel("1L", user),
                createTaskModel("2L", user)  // Only 2 tasks exist
        );

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(TaskRepository.findAllByIdInAndUser(taskIds, user)).thenReturn(existingTasks);

        assertThatThrownBy(() -> taskService.deleteTasks(taskIds, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("One or more tasks not found");

        verify(TaskRepository, never()).deleteAll(any());
    }
}