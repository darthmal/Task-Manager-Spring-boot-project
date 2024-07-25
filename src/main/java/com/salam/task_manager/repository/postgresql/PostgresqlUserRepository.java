package com.salam.task_manager.repository.postgresql;

import com.salam.task_manager.models.user.User;
import com.salam.task_manager.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("postgresqlUserRepository")
@Profile("postgresql")
@Primary
public interface PostgresqlUserRepository extends UserRepository, JpaRepository<User, String> {
}
