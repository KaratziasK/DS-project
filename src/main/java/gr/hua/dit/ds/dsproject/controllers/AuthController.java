package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.LoginRequestDTO;
import gr.hua.dit.ds.dsproject.dto.LoginResponseDTO;
import gr.hua.dit.ds.dsproject.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }
}
