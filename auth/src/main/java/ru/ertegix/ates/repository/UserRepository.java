package ru.ertegix.ates.repository;

import ru.ertegix.ates.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {

    User getByUsername(String username);

    Optional<User> getByPublicId(UUID publicId);
}
