package pl.karpik.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.karpik.todoapp.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}