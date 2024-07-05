package com.salam.task_manager.mapper;

import com.salam.task_manager.dto.TaskDto;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import org.mapstruct.Mapper;

public interface TaskMapper {

    TaskDto toDto(TaskModel task);

    TaskModel toEntity(TaskDto taskDto, User user);
}