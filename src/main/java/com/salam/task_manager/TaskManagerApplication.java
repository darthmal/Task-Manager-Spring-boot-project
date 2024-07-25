package com.salam.task_manager;

import com.salam.task_manager.models.TaskModel;
import com.salam.task_manager.models.user.Role;
import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import com.salam.task_manager.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication
public class TaskManagerApplication {
	public static void main(String[] args)  {
		SpringApplication.run(TaskManagerApplication.class, args);
	}

	//	Creating a default admin account if not exist
	@Bean
	CommandLineRunner run(UserService userService, UserRepository userRespo, PasswordEncoder passwordEncoder) { // Inject UserService
		return args -> {

			// Default admin user creation:
			if (userService.finUserByUsername("admin").isEmpty()) {
				User adminUser = new User();
				adminUser.setFirstname("Admin");
				adminUser.setLastName("Admin");
				adminUser.setEmail("admin@example.com");
				adminUser.setUsername("admin");
				adminUser.setPassword(passwordEncoder.encode("admin"));
				adminUser.setRole(Role.ADMIN);

				userRespo.save(adminUser);
				System.out.println("Default admin user created! Details:");
				System.out.println("username: admin, password: admin");
			}
		};
	}
}