package ro.utcluj.ds.user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.ResponseEntity;
import ro.utcluj.ds.user_service.dto.RegisterRequest;
import ro.utcluj.ds.user_service.entities.UserEntity;
import ro.utcluj.ds.user_service.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public UserEntity createUser(UserEntity user) {
        // 1. Verificăm dacă există în user_db
        Optional<UserEntity> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return null; // Username duplicat
        }

        // 2. Păstrăm parola în clar
        String rawPassword = user.getPassword();

        // 3. Criptăm parola pentru user_db
        user.setPassword(passwordEncoder.encode(rawPassword));

        // 4. Salvăm în user_db
        UserEntity savedUser = userRepository.save(user);

        // 5. Încercăm să sincronizăm cu auth-service
        try {
    RegisterRequest authRequest = new RegisterRequest(
        savedUser.getUsername(),
        rawPassword,
        savedUser.getRole()
    );

    String authUrl = "http://auth-service:8080/auth/register";
    System.out.println("➡️ Trimit cerere la " + authUrl + " pentru username=" + savedUser.getUsername());

    ResponseEntity<String> response = restTemplate.postForEntity(authUrl, authRequest, String.class);
    System.out.println("✅ Auth-service a răspuns: " + response.getStatusCode());

} catch (Exception e) {
    System.err.println("❌ Eroare la trimiterea către auth-service: " + e.getMessage());
    e.printStackTrace();
}

        return savedUser;
    }

    public UserEntity updateUser(Long id, UserEntity newUserDetails) {
        Optional<UserEntity> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            UserEntity existingUser = userData.get();
            existingUser.setUsername(newUserDetails.getUsername());
            existingUser.setRole(newUserDetails.getRole());

            if (newUserDetails.getPassword() != null && !newUserDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newUserDetails.getPassword()));
            }

            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}