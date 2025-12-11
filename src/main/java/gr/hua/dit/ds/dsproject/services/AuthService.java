package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.config.JwtUtils;
import gr.hua.dit.ds.dsproject.dto.LoginResponseDTO;
import gr.hua.dit.ds.dsproject.entities.Role;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.repositories.RoleRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(RoleRepository roleRepository,
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

    // ================== Login logic ==================

    public LoginResponseDTO login(String username, String password) {

        // 1. Κάνουμε authenticate με username + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
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

        // 6. Γυρνάμε DTO αντί για Map
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(jwt);
        dto.setType("Bearer");
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(roles);

        return dto;
    }
}
