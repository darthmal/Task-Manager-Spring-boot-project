package com.salam.task_manager.mapper;

import com.salam.task_manager.dto.TaskRequestUpdatdeDto;
import com.salam.task_manager.dto.TaskResponseDto;
import com.salam.task_manager.dto.TaskRequestDto;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    TaskResponseDto toDto(TaskModel taskModel);

    @Mapping(target = "id", source = "taskResponseDto.id")
    @Mapping(target = "user", source = "user")
    TaskModel toEntity(TaskResponseDto taskResponseDto, User user);

    @Mapping(target = "id", source = "taskRequestDto.id")
    @Mapping(target = "user", source = "user")
    TaskModel requestDtoToEntity(TaskRequestDto taskRequestDto, User user);

    void updateTaskFromDto(TaskRequestUpdatdeDto taskRequestDto, @MappingTarget TaskModel taskModel);
}