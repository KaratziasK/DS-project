package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.ClientDTO;
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
    @GetMapping("/admin-use")
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

    @PostMapping("/project-requests/{projectId}")
    public ResponseEntity<Map<String, Object>> getRequestsForProject(@PathVariable int projectId) {
        Project currentProject = projectService.getProject(projectId);
        List<Request> requestsForProject = requestService.getRequestsByProjectID(projectId);

        Map<String, Object> body = new HashMap<>();
        body.put("currentProject", currentProject);
        body.put("requestsForProject", requestsForProject);

        return ResponseEntity.ok(body);
    }


    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/my-profile")
    public ResponseEntity<Client> getMyProfile() {
        Client client = clientService.getCurrentClient();
        return ResponseEntity.ok(client);
    }

    @GetMapping("/edit-profile")
    public ResponseEntity<Client> getEditProfileData() {
        Client client = clientService.getCurrentClient();
        return ResponseEntity.ok(client);
    }

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/edit-profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody Client client,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        clientService.updateClient(client);
        return ResponseEntity.ok(client);
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
