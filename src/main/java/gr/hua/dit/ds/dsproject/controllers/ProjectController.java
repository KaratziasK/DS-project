package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.services.ClientService;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
import gr.hua.dit.ds.dsproject.services.ProjectService;
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

import static gr.hua.dit.ds.dsproject.entities.Status.Accepted;
import static gr.hua.dit.ds.dsproject.entities.Status.Rejected;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ClientService clientService;
    private final FreelancerService freelancerService;

    public ProjectController(ProjectService projectService,
                             ClientService clientService,
                             FreelancerService freelancerService) {
        this.projectService = projectService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
    }

    // ================== Δημιουργία Project (Client) ==================

    @Secured("ROLE_CLIENT")
    @PostMapping("")
    public ResponseEntity<?> createProject(
            @Valid @RequestBody Project project,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Client currentClient = clientService.getCurrentClient();
        project.setClient(currentClient);

        Project saved = projectService.saveProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ================== Assign Request σε Project (Freelancer) ==================

    @PostMapping("/{projectId}/assign-request")
    public ResponseEntity<?> assignRequestToProject(@PathVariable int projectId) {

        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        projectService.assignRequestToProject(projectId, freelancer);

        // Επιστρέφουμε τα requests του freelancer (όπως έκανε το view "request/myrequests")
        return ResponseEntity.ok(freelancer.getRequests());
    }

    // ================== Admin: Pending projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/pending")
    public ResponseEntity<List<Project>> getPendingProjects() {
        return ResponseEntity.ok(projectService.getProjectsPending());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{projectId}/accept")
    public ResponseEntity<Project> acceptProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Accepted);
        Project saved = projectService.saveProject(project);
        return ResponseEntity.ok(saved);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{projectId}/reject")
    public ResponseEntity<Project> rejectProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Rejected);
        Project saved = projectService.saveProject(project);
        return ResponseEntity.ok(saved);
    }

    // ================== Admin: Rejected projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/rejected")
    public ResponseEntity<List<Project>> getRejectedProjects() {
        return ResponseEntity.ok(projectService.getRejectedProjects());
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/rejected/{projectId}")
    public ResponseEntity<Void> deleteRejectedProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    // ================== Admin: Outdated projects (όλα) ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/outdated")
    public ResponseEntity<List<Project>> getAllOutdatedProjects() {
        return ResponseEntity.ok(projectService.getAllOutdatedProjects());
    }

    // ================== Client: Unassigned, Assigned, Completed, Unassigned+Outdated ==================

    @Secured("ROLE_CLIENT")
    @GetMapping("/unassigned")
    public ResponseEntity<List<Project>> getUnassignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getUnassignedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/assigned")
    public ResponseEntity<List<Project>> getAssignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getAssignedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/unassigned-outdated")
    public ResponseEntity<List<Project>> getUnassignedAndOutdatedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getUnassignedAndOutdatedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @DeleteMapping("/unassigned-outdated/{projectId}")
    public ResponseEntity<List<Project>> deleteUnassignedOutdatedProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
        Client currentClient = clientService.getCurrentClient();
        // όπως παλιά, επιστρέφουμε τη «φρέσκια» λίστα μετά τη διαγραφή
        return ResponseEntity.ok(projectService.getUnassignedAndOutdatedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/completed")
    public ResponseEntity<List<Project>> getCompletedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getCompletedProjects(currentClient));
    }

    // ================== Admin: Accepted projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/accepted")
    public ResponseEntity<List<Project>> getAcceptedProjects() {
        return ResponseEntity.ok(projectService.getAcceptedProjects());
    }

    // ================== Helper για validation errors ==================

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
