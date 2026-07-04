package com.medminder.domain.repository;

import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserUserId(Long userId);
    Optional<UserRole> findByUserUserIdAndRoleRoleName(Long userId, RoleName roleName);
    boolean existsByUserUserIdAndRoleRoleName(Long userId, RoleName roleName);
    long countByRoleRoleNameAndUserActiveTrue(RoleName roleName);
}
