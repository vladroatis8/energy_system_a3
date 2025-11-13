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


    @Autowired
    private AuthUserRepository authUserRepository;

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private JwtService jwtService;

    
    public AuthResponse register(RegisterRequest request) {
    System.out.println("📥 Register request primit pentru username=" + request.getUsername());

    if (authUserRepository.findByUsername(request.getUsername()).isPresent()) {
        System.out.println("⚠️ User deja există: " + request.getUsername());
        return null;
    }

    
    AuthUser newUser = new AuthUser();
    newUser.setUsername(request.getUsername());
    newUser.setPassword(passwordEncoder.encode(request.getPassword()));
    newUser.setRole(request.getRole());

    
    AuthUser savedUser = authUserRepository.save(newUser);

    //  GenerAm token și returnAm ID-ul
    String token = jwtService.generateToken(savedUser);

    return new AuthResponse(
            token,
            savedUser.getRole(),
            savedUser.getId().toString() // trimitem ID-ul generat în auth-db
    );
}

    
    public AuthResponse login(AuthRequest request) {
       
        Optional<AuthUser> userOptional = authUserRepository.findByUsername(request.getUsername());
        
      
        if (userOptional.isEmpty()) {
            return null; 
        }

        AuthUser authUser = userOptional.get();

        
        if (passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            
            
            String token = jwtService.generateToken(authUser);
            
            return new AuthResponse(token, authUser.getRole(), authUser.getId().toString());
            
        } else {
            return null;
        }
    }
}