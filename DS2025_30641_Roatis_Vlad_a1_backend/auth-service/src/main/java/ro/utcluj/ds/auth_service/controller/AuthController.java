package ro.utcluj.ds.auth_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.utcluj.ds.auth_service.dto.AuthRequest;
import ro.utcluj.ds.auth_service.dto.AuthResponse;
import ro.utcluj.ds.auth_service.dto.RegisterRequest;
import ro.utcluj.ds.auth_service.entities.AuthUser;
import ro.utcluj.ds.auth_service.repositories.AuthUserRepository;
import ro.utcluj.ds.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
 // Toate cererile vor începe cu /auth
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private AuthUserRepository authUserRepository;


    /**
     * Endpoint pentru inregistrarea unui utilizator nou.
     * http://localhost:8083/auth/register
     */
   @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    AuthResponse response = authService.register(request);

    if (response == null) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
    }

    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

    /**
     * Endpoint pentru autentificarea unui utilizator.
     * http://localhost:8083/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);

        if (authResponse != null) {
            // Logarea a avut succes,
            return new ResponseEntity<>(authResponse, HttpStatus.OK); // 200 OK
        } else {
            
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("AUTH-CONTROLLER: Primita cerere PING!");
    return ResponseEntity.ok("Auth-service este online ");
    }

    @GetMapping("/users")
public List<AuthUser> getAllUsers() {
    return authUserRepository.findAll();
}
    

}