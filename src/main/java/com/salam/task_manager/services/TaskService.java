package com.salam.task_manager.services;

import com.salam.task_manager.dto.TaskResponseDto;
import com.salam.task_manager.dto.TaskRequestDto;
import com.salam.task_manager.exception.ResourceNotFoundException;
import com.salam.task_manager.mapper.TaskMapper;
import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.TaskStatus;
import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.TaskRepository;
import com.salam.task_manager.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: ", username, ""));
        System.out.println(taskRequestDto);
        TaskModel task = taskMapper.requestDtoToEntity(taskRequestDto, user);
        System.out.println(task);
        task.setUser(user);
        task.setStatus(TaskStatus.PENDING);
        return taskMapper.toDto(taskRepository.save(task));
    }


}
