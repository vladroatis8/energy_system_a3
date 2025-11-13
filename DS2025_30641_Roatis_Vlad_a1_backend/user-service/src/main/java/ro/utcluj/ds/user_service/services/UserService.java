package ro.utcluj.ds.user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.ResponseEntity;

import ro.utcluj.ds.user_service.dto.AuthResponse;
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
    // 1️⃣ Verificăm dacă există deja în user_db
    Optional<UserEntity> existingUser = userRepository.findByUsername(user.getUsername());
    if (existingUser.isPresent()) {
        System.err.println("⚠️ Username deja există în user_db: " + user.getUsername());
        return null;
    }

    // 2️⃣ Reținem parola brută (înainte de criptare)
    String rawPassword = user.getPassword();

    // 3️⃣ Criptăm parola pentru user_db
    user.setPassword(passwordEncoder.encode(rawPassword));

    // 4️⃣ Salvăm utilizatorul local (temporar fără authId)
    UserEntity savedUser = userRepository.save(user);

    // 5️⃣ Sincronizare cu auth_service
    try {
        RegisterRequest authRequest = new RegisterRequest(
            savedUser.getUsername(),
            rawPassword,           // parola necriptată, auth_service o va cripta el
            savedUser.getRole()
        );

        String authUrl = "http://auth-service:8080/auth/register";
        System.out.println("➡️ Trimit cerere la " + authUrl + " pentru username=" + savedUser.getUsername());

        // 🔹 Primim un obiect AuthResponse cu token, role, id
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(authUrl, authRequest, AuthResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            AuthResponse authResp = response.getBody();
            String authId = authResp.getId(); // ✅ extragem ID-ul din auth-db
            System.out.println("✅ User creat și în auth-service, id=" + authId);

            // 🔹 Actualizăm userul local cu authId
            savedUser.setAuthId(authId);
            userRepository.save(savedUser);
        } else {
            System.err.println("⚠️ Auth-service a returnat un răspuns invalid: " + response.getStatusCode());
        }

    } catch (HttpClientErrorException e) {
        System.err.println("❌ Eroare HTTP la trimiterea către auth-service: " + e.getStatusCode());
        System.err.println(e.getResponseBodyAsString());
    } catch (Exception e) {
        System.err.println("❌ Eroare generală la trimiterea către auth-service: " + e.getMessage());
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