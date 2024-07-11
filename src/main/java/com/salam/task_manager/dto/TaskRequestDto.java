package com.salam.task_manager.dto;


import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class TaskRequestDto {
    private Long id;
    private String title;
    private String description;
    private ZonedDateTime dueDate;
}
