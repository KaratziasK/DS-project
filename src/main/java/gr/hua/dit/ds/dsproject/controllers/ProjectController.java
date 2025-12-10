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

    @Secured("ROLE_FREELANCER")
    @PostMapping("/{projectId}/make-request")
    public ResponseEntity<RequestSummaryDTO> assignRequestToProject(@PathVariable int projectId) {

        Freelancer freelancer = freelancerService.getCurrentFreelancer();

        Request newRequest = projectService.assignRequestToProject(projectId, freelancer);

        RequestSummaryDTO dto = toRequestSummaryDTO(newRequest);

        return ResponseEntity.ok(dto);
    }

    private RequestSummaryDTO toRequestSummaryDTO(Request request) {
        RequestSummaryDTO dto = new RequestSummaryDTO();

        dto.setId(request.getId());

        dto.setRequestStatus(
                request.getRequestStatus() != null
                        ? request.getRequestStatus().name()
                        : null
        );

        if (request.getProject() != null) {
            dto.setProjectTitle(request.getProject().getTitle());
            dto.setProjectDescription(request.getProject().getDescription());
        }

        dto.setDateSubmitted(request.getDateSubmitted());

        if (request.getFreelancer() != null &&
                request.getFreelancer().getUser() != null) {
            dto.setFreelancerUsername(
                    request.getFreelancer().getUser().getUsername()
            );
        }

        return dto;
    }


    // ================== Admin: Pending projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/pending/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getPendingProjects() {
        List<Project> pendingProjects = projectService.getProjectsPending();

        List<ProjectSummaryDTO> dtoList = pendingProjects.stream()
                .map(this::toProjectSummaryDTO)
                .toList(); // ή .collect(Collectors.toList()) σε παλιότερο Java

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/accept/ok-need")
    public ResponseEntity<ProjectSummaryDTO> acceptProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Accepted);
        Project saved = projectService.saveProject(project);

        ProjectSummaryDTO dto = toProjectSummaryDTO(saved);
        return ResponseEntity.ok(dto);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{projectId}/reject/ok-need")
    public ResponseEntity<ProjectSummaryDTO> rejectProject(@PathVariable int projectId) {
        Project project = projectService.getProject(projectId);
        project.setProjectStatus(Rejected);
        Project saved = projectService.saveProject(project);

        ProjectSummaryDTO dto = toProjectSummaryDTO(saved);
        return ResponseEntity.ok(dto);
    }


    // ================== Admin: Rejected projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/rejected/ok-need")
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
    @GetMapping("/admin-use/outdated/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllOutdatedProjects() {
        List<Project> outdatedProjects = projectService.getAllOutdatedProjects();

        List<ProjectSummaryDTO> dtoList = outdatedProjects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    // ================== Client: Unassigned, Assigned, Completed, Unassigned+Outdated ==================

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/all-projects")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjectForThisClient() {
        Client currentClient = clientService.getCurrentClient();

        List<Project> projects = projectService.getAllProjectForThisClient(currentClient);

        List<ProjectSummaryDTO> dtoList = projects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned")
    public ResponseEntity<List<ProjectSummaryDTO>> getUnassignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        List<Project> unassigned = projectService.getUnassignedProjects(currentClient);

        List<ProjectSummaryDTO> dtoList = unassigned.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/assigned")
    public ResponseEntity<List<AssignedProjectDTO>> getAssignedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        List<Project> assignedProjects = projectService.getAssignedProjects(currentClient);

        List<AssignedProjectDTO> dtoList = assignedProjects.stream()
                .map(this::toAssignedProjectDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }



    private AssignedProjectDTO toAssignedProjectDTO(Project project) {
        AssignedProjectDTO dto = new AssignedProjectDTO();

        dto.setProjectId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setPaymentAmount(project.getPaymentAmount());
        dto.setDeadline(project.getDeadline());

        if (project.getAssignment() != null &&
                project.getAssignment().getFreelancer() != null &&
                project.getAssignment().getFreelancer().getUser() != null) {
            dto.setFreelancerUsername(
                    project.getAssignment().getFreelancer().getUser().getUsername()
            );
        }

        return dto;
    }


    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/unassigned-outdated")
    public ResponseEntity<List<ProjectSummaryDTO>> getUnassignedAndOutdatedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        List<Project> unassignedOutdated = projectService.getUnassignedAndOutdatedProjects(currentClient);

        List<ProjectSummaryDTO> dtoList = unassignedOutdated.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
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
    public ResponseEntity<List<AssignedProjectDTO>> getCompletedProjectsForClient() {
        Client currentClient = clientService.getCurrentClient();
        List<Project> completedProjects = projectService.getCompletedProjects(currentClient);

        List<AssignedProjectDTO> dtoList = completedProjects.stream()
                .map(this::toAssignedProjectDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    // ================== Admin: Accepted projects ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/accepted/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAcceptedProjects() {
        List<Project> accepted = projectService.getAcceptedProjects();

        List<ProjectSummaryDTO> dtoList = accepted.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/all/ok-need")
    public ResponseEntity<List<ProjectSummaryDTO>> getAllProjects() {
        List<Project> projects = projectService.getProjects();

        List<ProjectSummaryDTO> dtoList = projects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
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
