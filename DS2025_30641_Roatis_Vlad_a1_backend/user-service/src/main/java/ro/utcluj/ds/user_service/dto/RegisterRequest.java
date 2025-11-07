package ro.utcluj.ds.user_service.dto;

// Folosim Lombok pentru a genera gettere, settere, constructori
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Această clasă "oglindă" DTO-ul din auth-service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String role;
}