package ro.utcluj.ds.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.utcluj.ds.auth_service.dto.AuthRequest;
import ro.utcluj.ds.auth_service.dto.AuthResponse;
import ro.utcluj.ds.auth_service.dto.RegisterRequest;
import ro.utcluj.ds.auth_service.entities.AuthUser;
import ro.utcluj.ds.auth_service.repositories.AuthUserRepository;

import java.util.Optional;

@Service
public class AuthService {

    // 1. Injectăm "Frigiderul"
    @Autowired
    private AuthUserRepository authUserRepository;

    // 2. Injectăm "Mașina de Criptat"
    // Spring o găsește automat, pentru că am definit-o cu @Bean în SecurityConfig
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 3. Injectăm "Atelierul de Token-uri"
    @Autowired
    private JwtService jwtService;

    /**
     * Înregistrează un utilizator nou.
     * Criptează parola înainte de salvare.
     */
    public boolean register(RegisterRequest request) {
        System.out.println("📥 Register request primit pentru username=" + request.getUsername());
    if (authUserRepository.findByUsername(request.getUsername()).isPresent()) {
        System.out.println("⚠️ User deja există: " + request.getUsername());
        return false;
    }

        // Creăm un utilizator nou
        AuthUser newUser = new AuthUser();
        newUser.setUsername(request.getUsername());
        
        // --- PARTEA DE SECURITATE ---
        // CRIPTĂM parola înainte de a o salva! Nu salvăm niciodată parole în clar.
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Setăm rolul (ex: "CLIENT" sau "ADMINISTRATOR")
        newUser.setRole(request.getRole());

        // Salvăm în baza de date
        authUserRepository.save(newUser);
        return true; // Succes
    }

    /**
     * Autentifică un utilizator și returnează un token JWT.
     */
    public AuthResponse login(AuthRequest request) {
        // 1. Căutăm user-ul în baza de date
        Optional<AuthUser> userOptional = authUserRepository.findByUsername(request.getUsername());
        
        // Verificăm dacă user-ul există
        if (userOptional.isEmpty()) {
            return null; // User-ul nu a fost găsit
        }

        AuthUser authUser = userOptional.get();

        // 2. --- PARTEA DE SECURITATE ---
        // Verificăm dacă parola trimisă de client (request.getPassword())
        // se potrivește cu parola criptată din baza de date (authUser.getPassword())
        if (passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            
            // 3. Parolele se potrivesc! Generăm un token.
            String token = jwtService.generateToken(authUser);
            
            // 4. Returnăm token-ul și rolul (folosind DTO-ul AuthResponse)
            return new AuthResponse(token, authUser.getRole());
            
        } else {
            // 5. Parola este greșită
            return null;
        }
    }
}