package ro.utcluj.ds.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcluj.ds.user_service.entities.UserEntity;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByUsername(String username);
}