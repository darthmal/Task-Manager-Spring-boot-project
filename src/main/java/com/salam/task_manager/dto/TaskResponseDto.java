package com.salam.task_manager.dto;

import com.salam.task_manager.models.TaskStatus;
import lombok.*;

import java.time.ZonedDateTime;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private ZonedDateTime dueDate;
    private TaskStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}