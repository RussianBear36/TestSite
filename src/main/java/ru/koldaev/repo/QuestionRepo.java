package ru.koldaev.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.koldaev.entity.Question;

public interface QuestionRepo extends JpaRepository<Question, Long> {
}
