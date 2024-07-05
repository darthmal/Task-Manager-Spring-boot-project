package com.salam.task_manager.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private ZonedDateTime dueDate;
    private boolean completed;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}