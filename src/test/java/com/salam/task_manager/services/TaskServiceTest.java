package com.salam.task_manager.services;

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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

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

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        task = new TaskModel();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setUser(user);

        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");

        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(1L);
        taskResponseDto.setTitle("Test Task");
        taskResponseDto.setUserId(1L);
    }

    @Test
    void testCreateTask_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskMapper.requestDtoToEntity(taskRequestDto, user)).thenReturn(task);
        when(taskRepository.save(any(TaskModel.class))).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponseDto);

        TaskResponseDto createdTask = taskService.createTask(taskRequestDto, user.getUsername());

        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo(task.getTitle());
        assertThat(createdTask.getUserId()).isEqualTo(user.getId());
        verify(taskRepository, times(1)).save(any(TaskModel.class));
    }

    @Test
    void testCreateTask_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(taskRequestDto, user.getUsername()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with username:  not found with testuser: ");

        verify(taskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    void testGetTasksForUser_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(List.of(task));
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
}