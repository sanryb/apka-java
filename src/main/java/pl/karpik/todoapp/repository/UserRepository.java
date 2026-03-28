package pl.karpik.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.karpik.todoapp.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}