package ru.ertegix.ates.accounting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ertegix.ates.accounting.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPublicId(UUID fromString);
}
