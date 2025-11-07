package ro.utcluj.ds.auth_service.controller;

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
import ro.utcluj.ds.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
 // Toate cererile vor începe cu /auth
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint pentru înregistrarea unui utilizator nou.
     * http://localhost:8083/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        boolean isRegistered = authService.register(request);

        if (isRegistered) {
            // Utilizatorul a fost creat cu succes
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED); // 201 Created
        } else {
            // Service-ul a returnat false (username-ul există deja)
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }

    /**
     * Endpoint pentru autentificarea unui utilizator.
     * http://localhost:8083/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);

        if (authResponse != null) {
            // Logarea a avut succes, trimitem token-ul și rolul
            return new ResponseEntity<>(authResponse, HttpStatus.OK); // 200 OK
        } else {
            // Service-ul a returnat null (username sau parolă greșită)
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("✅ === AUTH-CONTROLLER: Primită cerere PING! ===");
    return ResponseEntity.ok("Auth-service este online ✅");
    }

}