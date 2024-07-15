package com.salam.task_manager;

import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication
public class TaskManagerApplication {
	public static void main(String[] args) {
		System.out.println("Hello");
		SpringApplication.run(TaskManagerApplication.class, args);
	}
}
