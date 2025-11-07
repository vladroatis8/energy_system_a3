package ro.utcluj.ds.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ds.user_service.entities.UserEntity;
import ro.utcluj.ds.user_service.services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.getUserById(id);

        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserEntity user) {
        System.out.println("=== CONTROLLER: Primită cerere POST /users ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Role: " + user.getRole());
        
        try {
            UserEntity savedUser = userService.createUser(user);
            System.out.println("=== CONTROLLER: User creat cu succes ===");
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            System.err.println("=== CONTROLLER: IllegalArgumentException - " + message + " ===");
            
            if (message.equals("USERNAME_EXISTS_IN_USER_DB")) {
                return new ResponseEntity<>(
                    "Username există deja în user_db", 
                    HttpStatus.BAD_REQUEST
                );
            } else if (message.equals("USERNAME_EXISTS_IN_AUTH_DB")) {
                return new ResponseEntity<>(
                    "Username există deja în auth_db. Curăță baza de date auth_db!", 
                    HttpStatus.CONFLICT
                );
            } else {
                return new ResponseEntity<>(
                    "Eroare de validare: " + message, 
                    HttpStatus.BAD_REQUEST
                );
            }
            
        } catch (RuntimeException e) {
            String message = e.getMessage();
            System.err.println("=== CONTROLLER: RuntimeException - " + message + " ===");
            e.printStackTrace();
            
            if (message != null && message.startsWith("AUTH_SERVICE_UNAVAILABLE")) {
                return new ResponseEntity<>(
                    "Auth-service nu răspunde! Verifică: docker ps | grep auth-service", 
                    HttpStatus.SERVICE_UNAVAILABLE
                );
            } else {
                return new ResponseEntity<>(
                    "Eroare runtime: " + message, 
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
            
        } catch (Exception e) {
            System.err.println("=== CONTROLLER: Exception generală ===");
            e.printStackTrace();
            return new ResponseEntity<>(
                "Eroare neașteptată: " + e.getClass().getName() + " - " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserEntity newUserDetails) {
        UserEntity updatedUser = userService.updateUser(id, newUserDetails);

        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        boolean wasDeleted = userService.deleteUser(id);

        if (wasDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}