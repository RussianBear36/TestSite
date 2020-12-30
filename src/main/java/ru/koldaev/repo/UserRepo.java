package ru.koldaev.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.koldaev.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
