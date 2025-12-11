package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.ClientDTO;
import gr.hua.dit.ds.dsproject.dto.ClientProfileDTO;
import gr.hua.dit.ds.dsproject.dto.ClientProfileUpdateDTO;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<ClientDTO>> getClients() {
        return ResponseEntity.ok(clientService.getClientDTOs());
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/my-profile/ok-need")
    public ResponseEntity<ClientProfileDTO> getMyProfile() {
        return ResponseEntity.ok(clientService.toClientProfileDTO(clientService.getCurrentClient()));
    }

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/edit-profile/ok-need")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ClientProfileUpdateDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Validation failed");
            responseBody.put("errors", errors);

            return ResponseEntity.badRequest().body(responseBody);
        }

        clientService.updateCurrentClientProfile(dto);

        return ResponseEntity.ok("Profile updated successfully");
    }

}
