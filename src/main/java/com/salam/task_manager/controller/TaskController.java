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

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        System.out.println(username);
        return new ResponseEntity<>(taskService.createTask(taskDto, username), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskDto> tasks = taskService.getTasksForUser(username);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}