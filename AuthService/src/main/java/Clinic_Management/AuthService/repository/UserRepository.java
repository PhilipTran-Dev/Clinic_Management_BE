package Clinic_Management.AuthService.repository;

import Clinic_Management.AuthService.entity.User;
import Clinic_Management.AuthService.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRoleInOrderByCreatedAtDesc(List<UserRole> roles);

    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
}