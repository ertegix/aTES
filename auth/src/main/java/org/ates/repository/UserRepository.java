package org.ates.repository;

import org.ates.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User getByUsername(String username);
}
