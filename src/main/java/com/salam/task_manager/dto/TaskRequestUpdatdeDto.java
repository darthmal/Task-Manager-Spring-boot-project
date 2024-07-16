package com.salam.task_manager.dto;

import com.salam.task_manager.models.TaskStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TaskRequestUpdatdeDto {
    private String title;
    private String description;
    private ZonedDateTime dueDate;
    private TaskStatus status;
}
