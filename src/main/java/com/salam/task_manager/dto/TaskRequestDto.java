package com.salam.task_manager.dto;


import com.salam.task_manager.models.TaskStatus;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
public class TaskRequestDto {
    private String id;
    private String title;
    private String description;
    private Date dueDate;
    private TaskStatus status;
}
