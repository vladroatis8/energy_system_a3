package ro.utcluj.ds.auth_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.ds.auth_service.entities.AuthUser;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

   // o folosim la login
    Optional<AuthUser> findByUsername(String username);
}