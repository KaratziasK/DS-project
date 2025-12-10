package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.config.JwtUtils;
import gr.hua.dit.ds.dsproject.entities.Role;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.repositories.RoleRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(RoleRepository roleRepository,
                          UserRepository userRepository,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    // ================== Init roles + secret admin ==================

    @PostConstruct
    public void setup() {
        createRoleIfNotExists("ROLE_CLIENT");
        createRoleIfNotExists("ROLE_FREELANCER");
        createRoleIfNotExists("ROLE_ADMIN");

        Optional<User> secretAdmin = userRepository.findByUsername("admin");
        if (secretAdmin.isEmpty()) {
            createSecretAdminUser();
        }
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private void createSecretAdminUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User admin = new User("admin", "admin@example.com", encoder.encode("admin"));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        admin.getRoles().add(adminRole);
        userRepository.save(admin);
    }

    // ================== REST Login με JWT ==================

    @PostMapping("/login/ok-need")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // 1. Κάνουμε authenticate με username + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Παράγουμε JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 3. Παίρνουμε principal (Spring Security user)
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // 4. Βρίσκουμε το δικό μας User entity (για id + email)
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after auth"));

        // 5. Παίρνουμε ρόλους
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 6. Επιστρέφουμε JSON με token + πληροφορίες χρήστη
        return ResponseEntity.ok(
                Map.of(
                        "token", jwt,
                        "type", "Bearer",
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "roles", roles
                )
        );
    }

    // ================== DTO ==================

    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
// TODO sign out endpoint