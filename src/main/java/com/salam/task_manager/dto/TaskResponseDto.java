package com.salam.task_manager.dto;

import com.salam.task_manager.models.TaskStatus;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class TaskResponseDto {
    private String id;
    private String title;
    private String description;
    private String userId;
    private Date dueDate;
    private TaskStatus status;
    private Date createdAt;
    private Date updatedAt;
}