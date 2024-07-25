package com.salam.task_manager.controller;

import com.salam.task_manager.dto.TaskRequestUpdatdeDto;
import com.salam.task_manager.dto.TaskResponseDto;
import com.salam.task_manager.dto.TaskRequestDto;
import com.salam.task_manager.exception.ResourceNotFoundException;
import com.salam.task_manager.models.TaskStatus;
import com.salam.task_manager.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskRequestDto taskRequestDto;
    private TaskRequestUpdatdeDto taskRequestUpdatdeDto;
    private TaskResponseDto taskResponseDto;

    @BeforeEach
    void setUp() {
        taskRequestDto = new TaskRequestDto();
        taskRequestDto.setTitle("Test Task");
        taskRequestDto.setDescription("Test Description");
        taskRequestDto.setDueDate(Date.from(ZonedDateTime.now().toInstant()));

        taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId("1L");
        taskResponseDto.setTitle("Test Task");
        taskResponseDto.setDescription("Test Description");
        taskResponseDto.setUserId("1L");
        taskResponseDto.setDueDate(Date.from(ZonedDateTime.now().toInstant()));

        taskRequestUpdatdeDto = new TaskRequestUpdatdeDto();
        taskRequestUpdatdeDto.setTitle("Updated Task Title");
        taskRequestUpdatdeDto.setDescription("Updated Task Description");
        taskRequestUpdatdeDto.setDueDate(Date.from(ZonedDateTime.now().plusDays(1).toInstant()));
        taskRequestUpdatdeDto.setStatus(TaskStatus.DONE);

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

    @Test
    void testUpdateTask_Success() {
        String taskId = "1L";

        when(taskService.updateTask(anyString(), any(TaskRequestUpdatdeDto.class), anyString()))
                .thenReturn(taskResponseDto);

        ResponseEntity<TaskResponseDto> response = taskController.updateTask(taskId, taskRequestUpdatdeDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(taskResponseDto);
        verify(taskService, times(1)).updateTask(taskId, taskRequestUpdatdeDto, "testuser");
    }

    @Test
    void testDeleteTask_Success() {
        String taskId = "1L";
        doNothing().when(taskService).deleteTask(taskId, "testuser"); // Mock deleteTask to do nothing

        ResponseEntity<String> response = taskController.deleteTask(taskId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isEqualTo("Deleted successfully");
        verify(taskService, times(1)).deleteTask(taskId, "testuser");
    }

    @Test
    void testDeleteTask_TaskNotFound_ReturnsNotFound() {
        String taskId = "999L"; // With an fictional Id
        String username = "testuser";

        doThrow(new ResourceNotFoundException("Task not found", "", ""))
                .when(taskService).deleteTask(taskId, username);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskController.deleteTask(taskId));

        assertThat(exception.getRessourceName()).isEqualTo("Task not found");
        assertThat(exception.getFieldName()).isEqualTo("");
        assertThat(exception.getFieldValue()).isEqualTo("");

        verify(taskService, times(1)).deleteTask(taskId, username);
    }

    @Test
    void testDeleteTasks_Success() {
        List<String> taskIds = List.of("1L", "2L", "3L");
        doNothing().when(taskService).deleteTasks(taskIds, "testuser");

        ResponseEntity<String> response = taskController.deleteTasks(taskIds);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isEqualTo("Successfully deleted");
        verify(taskService, times(1)).deleteTasks(taskIds, "testuser");
    }

    @Test
    void testDeleteTasks_SomeTasksNotFound_ReturnsNotFound() {
        List<String> taskIds = List.of("1L", "2L", "999L"); // Include a non-existent ID

        doThrow(new ResourceNotFoundException("One or more tasks not found", "", ""))
                .when(taskService).deleteTasks(taskIds, "testuser");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> taskController.deleteTasks(taskIds));

        assertThat(exception.getRessourceName()).isEqualTo("One or more tasks not found");
        assertThat(exception.getFieldValue()).isEqualTo("");
        assertThat(exception.getFieldName()).isEqualTo("");

        verify(taskService, times(1)).deleteTasks(taskIds, "testuser");
    }

}