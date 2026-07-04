package com.medminder.domain.repository;

import com.medminder.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findAllByOrderByCreatedAtDesc();
    boolean existsByEmailIgnoreCase(String email);
}
