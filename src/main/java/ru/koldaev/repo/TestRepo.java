package ru.koldaev.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.koldaev.entity.Test;

import java.util.List;

public interface TestRepo extends JpaRepository<Test, Long> {
    List<Test> findByAuthorId(Long id);
}
