package ru.ertegix.ates.tasktracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ertegix.ates.common.Role;
import ru.ertegix.ates.tasktracker.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByRole(Role role);

    @Query(nativeQuery = true,
            value = "SELECT * FROM user order by random() limit 1;")
    User getRandomUser();

    Optional<User> findByPublicId(UUID fromString);

    Optional<User> findByUsername(String username);
}
