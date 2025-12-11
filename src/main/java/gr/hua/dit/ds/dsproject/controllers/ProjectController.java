package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.AssignedProjectDTO;
import gr.hua.dit.ds.dsproject.dto.ProjectCreateDTO;
import gr.hua.dit.ds.dsproject.dto.ProjectSummaryDTO;
import gr.hua.dit.ds.dsproject.dto.RequestSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.services.ClientService;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
import gr.hua.dit.ds.dsproject.services.ProjectService;
import gr.hua.dit.ds.dsproject.services.UserService;
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
                             FreelancerService freelancerService
    ) {
        this.projectService = projectService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
    }

    // ================== Δημιουργία Project (Client) ==================

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/new/ok-need")
    public ResponseEntity<?> createProject(
            @Valid @RequestBody ProjectCreateDTO projectDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        try {
            ProjectSummaryDTO responseDto =
                    projectService.createProjectForCurrentClient(projectDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalStateException e) {
            // Δεν υπάρχει client για τον current user
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace(); // να το δεις στο log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating project: " + e.getMessage());
        }
    }

    @Secured("ROLE_FREELANCER")
    @PostMapping("/{projectId}/make-request/freelancer-use/ok-need")
    public ResponseEntity<RequestSummaryDTO> assignRequestToProject(@PathVariable int projectId) {
        return ResponseEntity.ok(freelancerService.makeRequestForProject(projectId));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/pending/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getPendingProjects() {
        return ResponseEntity.ok(projectService.getPendingProjectDTOs());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/accept/ok-need")
    public ResponseEntity<ProjectSummaryDTO> acceptProject(@PathVariable int projectId) {
        ProjectSummaryDTO dto = projectService.acceptProject(projectId);
        return ResponseEntity.ok(dto);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/reject/ok-need")
    public ResponseEntity<ProjectSummaryDTO> rejectProject(@PathVariable int projectId) {
        return ResponseEntity.ok(projectService.rejectProject(projectId));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/rejected/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getRejectedProjects() {
        return ResponseEntity.ok(projectService.getRejectedProjectDTOs());
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin-use/rejected/{projectId}/ok-need")
    public ResponseEntity<Void> deleteRejectedProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/outdated/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllOutdatedProjects() {
        return ResponseEntity.ok(projectService.getAllOutdatedProjectDTOs());
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/all-projects/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjectForThisClient() {
        return ResponseEntity.ok(projectService.getAllProjectsForCurrentClient());
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getUnassignedProjectsForClient() {
        return ResponseEntity.ok(projectService.getUnassignedProjectsForCurrentClient());
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/assigned/ok-need")
    public ResponseEntity<List<AssignedProjectDTO>> getAssignedProjectsForClient() {
        return ResponseEntity.ok(projectService.getAssignedProjectsForCurrentClient());
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned-outdated/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getUnassignedAndOutdatedProjectsForClient() {
        return ResponseEntity.ok(projectService.getUnassignedAndOutdatedProjectsForCurrentClient());
    }

    @Secured("ROLE_CLIENT")
    @DeleteMapping("/client-use/unassigned-outdated/{projectId}/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> deleteUnassignedOutdatedProject(@PathVariable int projectId) {
        return ResponseEntity.ok(projectService.deleteUnassignedOutdatedProjectAndReturnList(projectId));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/completed/ok-need")
    public ResponseEntity<List<AssignedProjectDTO>> getCompletedProjectsForClient() {
        return ResponseEntity.ok(projectService.getCompletedProjectsForCurrentClient());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/accepted/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAcceptedProjects() {
        return ResponseEntity.ok(projectService.getAcceptedProjectDTOs());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/all/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjectDTOs());
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
