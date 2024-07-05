package com.salam.task_manager.controller;

import com.salam.task_manager.dto.TaskDto;
import com.salam.task_manager.services.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Add new task
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        System.out.println(username);
        return new ResponseEntity<>(taskService.createTask(taskDto, username), HttpStatus.CREATED);
    }

    // List of all logged in user task's
    @GetMapping
    public ResponseEntity<List<TaskDto>> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksForUser(username);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // update user task
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable("id") Long taskId,
                                              @Valid @RequestBody TaskDto updatedTaskDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TaskDto updatedTask = taskService.updateTask(taskId, updatedTaskDto, username);
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    // Delete task by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") Long taskId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.deleteTask(taskId, username);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.NO_CONTENT);
    }

    // Delete multiple logged in user task's
    @DeleteMapping
    public ResponseEntity<String> deleteTasks(@RequestBody List<Long> taskIds) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        taskService.deleteTasks(taskIds, username);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.NO_CONTENT);
    }
}