package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.ProjectCreateDTO;
import gr.hua.dit.ds.dsproject.dto.ProjectSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.services.ClientService;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
import gr.hua.dit.ds.dsproject.services.ProjectService;
import gr.hua.dit.ds.dsproject.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserService userService;

    public ProjectController(ProjectService projectService,
                             ClientService clientService,
                             FreelancerService freelancerService,
                             UserService userService
    ) {
        this.projectService = projectService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
        this.userService = userService;
    }

    // ================== Δημιουργία Project (Client) ==================

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/new")
    public ResponseEntity<?> createProject(
            @Valid @RequestBody ProjectCreateDTO projectDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Client currentClient = clientService.getCurrentClient();
        if (currentClient == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("No client is associated with the current user");
        }

        try {
            Project project = toProjectEntity(projectDto, currentClient);
            Project saved = projectService.saveProject(project);
            ProjectSummaryDTO responseDto = toProjectSummaryDTO(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            e.printStackTrace(); // να το δεις στο log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating project: " + e.getMessage());
        }
    }



    private Project toProjectEntity(ProjectCreateDTO dto, Client client) {
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setPaymentAmount(dto.getPaymentAmount());
        project.setDeadline(dto.getDeadline());
        project.setClient(client);
        // projectStatus μένει Pending από το constructor ή το ορίζεις ρητά:
        // project.setProjectStatus(Status.Pending);
        return project;
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
    @GetMapping("/admin-use/pending")
    public ResponseEntity<List<ProjectSummaryDTO>> getPendingProjects() {
        List<Project> pendingProjects = projectService.getProjectsPending();

        List<ProjectSummaryDTO> dtoList = pendingProjects.stream()
                .map(this::toProjectSummaryDTO)
                .toList(); // ή .collect(Collectors.toList()) σε παλιότερο Java

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/accept")
    public ResponseEntity<ProjectSummaryDTO> acceptProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Accepted);
        Project saved = projectService.saveProject(project);

        ProjectSummaryDTO dto = toProjectSummaryDTO(saved);
        return ResponseEntity.ok(dto);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/reject")
    public ResponseEntity<ProjectSummaryDTO> rejectProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Rejected);
        Project saved = projectService.saveProject(project);

        ProjectSummaryDTO dto = toProjectSummaryDTO(saved);
        return ResponseEntity.ok(dto);
    }


    // ================== Admin: Rejected projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/rejected")
    public ResponseEntity<List<ProjectSummaryDTO>> getRejectedProjects() {
        List<Project> rejected = projectService.getRejectedProjects();

        List<ProjectSummaryDTO> dtoList = rejected.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin-use/rejected/{projectId}")
    public ResponseEntity<Void> deleteRejectedProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    // ================== Admin: Outdated projects (όλα) ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/outdated")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllOutdatedProjects() {
        List<Project> outdatedProjects = projectService.getAllOutdatedProjects();

        List<ProjectSummaryDTO> dtoList = outdatedProjects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    // ================== Client: Unassigned, Assigned, Completed, Unassigned+Outdated ==================

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned")
    public ResponseEntity<List<Project>> getUnassignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getUnassignedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/assigned")
    public ResponseEntity<List<Project>> getAssignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getAssignedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned-outdated")
    public ResponseEntity<List<Project>> getUnassignedAndOutdatedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getUnassignedAndOutdatedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @DeleteMapping("/client-use/unassigned-outdated/{projectId}")
    public ResponseEntity<List<Project>> deleteUnassignedOutdatedProject(@PathVariable int projectId) {
        projectService.deleteProject(projectId);
        Client currentClient = clientService.getCurrentClient();
        // όπως παλιά, επιστρέφουμε τη «φρέσκια» λίστα μετά τη διαγραφή
        return ResponseEntity.ok(projectService.getUnassignedAndOutdatedProjects(currentClient));
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/completed")
    public ResponseEntity<List<Project>> getCompletedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        return ResponseEntity.ok(projectService.getCompletedProjects(currentClient));
    }

    // ================== Admin: Accepted projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/accepted")
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

    private ProjectSummaryDTO toProjectSummaryDTO(Project project) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setPaymentAmount(project.getPaymentAmount());
        dto.setProjectStatus(
                project.getProjectStatus() != null ? project.getProjectStatus().toString() : null
        );
        dto.setDeadline(project.getDeadline());
        return dto;
    }

}
