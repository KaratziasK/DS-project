package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.ClientDTO;
import gr.hua.dit.ds.dsproject.dto.ClientProfileDTO;
import gr.hua.dit.ds.dsproject.dto.ClientProfileUpdateDTO;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.services.ClientService;
import gr.hua.dit.ds.dsproject.services.ProjectService;
import gr.hua.dit.ds.dsproject.services.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    private final ProjectService projectService;
    private final RequestService requestService;

    public ClientController(ClientService clientService,
                            ProjectService projectService,
                            RequestService requestService) {
        this.clientService = clientService;
        this.projectService = projectService;
        this.requestService = requestService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<ClientDTO>> getClients() {
        return ResponseEntity.ok(clientService.getClientDTOs());
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<Project>> getMyProjects() {
        Client client = clientService.getCurrentClient();
        return ResponseEntity.ok(client.getProjects());
    }

    @PostMapping("")
    public ResponseEntity<?> createClient(
            @Valid @RequestBody Client client,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        clientService.saveClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }


    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/my-profile/ok-need")
    public ResponseEntity<ClientProfileDTO> getMyProfile() {
        Client client = clientService.getCurrentClient();
        ClientProfileDTO dto = toClientProfileDTO(client);
        return ResponseEntity.ok(dto);
    }

    private ClientProfileDTO toClientProfileDTO(Client client) {
        ClientProfileDTO dto = new ClientProfileDTO();

        dto.setClientId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setPhone(client.getPhone());

        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getId());
            dto.setEmail(client.getUser().getEmail());
            dto.setUsername(client.getUser().getUsername());
        }

        return dto;
    }

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/edit-profile/ok-need")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ClientProfileUpdateDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Client client = clientService.getCurrentClient();

        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setPhone(dto.getPhone());

        clientService.updateClient(client);

        return ResponseEntity.ok("Profile updated successfully");
    }


    private ResponseEntity<?> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Validation failed");
        responseBody.put("errors", errors);

        return ResponseEntity.badRequest().body(responseBody);
    }
}
