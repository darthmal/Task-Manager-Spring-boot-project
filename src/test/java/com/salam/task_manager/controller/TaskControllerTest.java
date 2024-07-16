package com.salam.task_manager.controller;

import com.salam.task_manager.dto.TaskResponseDto;
import com.salam.task_manager.dto.TaskRequestDto;
import com.salam.task_manager.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskRequestDto taskRequestDto;
    private TaskResponseDto taskResponseDto;

    @BeforeEach
    void setUp() {
        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");
        taskRequestDto.setDescription("Test Description");
        taskRequestDto.setDueDate(ZonedDateTime.now());

        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(1L);
        taskResponseDto.setTitle("Test Task");
        taskResponseDto.setDescription("Test Description");
        taskResponseDto.setUserId(1L);
        taskResponseDto.setDueDate(ZonedDateTime.now());

        // Set up mock authentication
        UserDetails userDetails = new User("testuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testCreateTask() {
        when(taskService.createTask(any(TaskRequestDto.class), anyString())).thenReturn(taskResponseDto);

        ResponseEntity<TaskResponseDto> response = taskController.createTask(taskRequestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(taskResponseDto);
        verify(taskService, times(1)).createTask(any(TaskRequestDto.class), anyString());
    }

    @Test
    void testGetMyTasks() {
        when(taskService.getTasksForUser(anyString())).thenReturn(List.of(taskResponseDto));

        ResponseEntity<List<TaskResponseDto>> response = taskController.getMyTasks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(taskResponseDto);
        verify(taskService, times(1)).getTasksForUser(anyString());
    }
}