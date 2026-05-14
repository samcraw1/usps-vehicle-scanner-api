package com.usps.scanner.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Spring Data JPA auto-generates the SQL for all standard CRUD operations.
// `findByUsername` follows Spring's naming convention - it generates
// "SELECT * FROM users WHERE username = ?" automatically. No SQL written by hand.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
