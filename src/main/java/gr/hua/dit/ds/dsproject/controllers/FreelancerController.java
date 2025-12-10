package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.*;
import gr.hua.dit.ds.dsproject.entities.*;
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

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController {

    private final FreelancerService freelancerService;
    private final ProjectService projectService;

    public FreelancerController(FreelancerService freelancerService,
                                ProjectService projectService) {
        this.freelancerService = freelancerService;
        this.projectService = projectService;
    }

    // ================== Admin: λίστα freelancers ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<FreelancerSummaryDTO>> getFreelancers() {
        List<Freelancer> freelancers = freelancerService.getFreelancers();

        List<FreelancerSummaryDTO> dtoList = freelancers.stream()
                .map(this::toFreelancerSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/not-verified/ok-need")
    public ResponseEntity<List<FreelancerSummaryDTO>> getNotVerifiedFreelancers() {
        List<Freelancer> notVerifiedFreelancers = freelancerService.getNotVerifiedFreelancer();

        List<FreelancerSummaryDTO> dtoList = notVerifiedFreelancers.stream()
                .map(this::toFreelancerSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin-use/{freelancerId}/verify/ok-need")
    public ResponseEntity<FreelancerSummaryDTO> verifyFreelancer(@PathVariable("freelancerId") int freelancerId) {
        Freelancer freelancer = freelancerService.getFreelancer(freelancerId);

        freelancer.setVerified(true);
        freelancerService.saveFreelancer(freelancer);

        FreelancerSummaryDTO dto = toFreelancerSummaryDTO(freelancer);
        return ResponseEntity.ok(dto);
    }


    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin-use/{freelancerId}/ok-need")
    public ResponseEntity<Void> deleteFreelancer(@PathVariable("freelancerId") int freelancerId) {
        freelancerService.deleteFreelancer(freelancerId);
        return ResponseEntity.noContent().build();
    }

    // ================== Freelancer: διαθέσιμα projects ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/projects-available-to-request/ok-need")
    public ResponseEntity<Map<String, Object>> getProjectsForFreelancer() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        List<Project> acceptedProjects = projectService.getAcceptedProjects();
        List<Project> requestedProjects =
                projectService.getRequestedProjects(acceptedProjects, freelancer.getRequests());
        List<Project> notRequestedNotAssignedProjects =
                projectService.getNotRequestedAndUnassignedProjects(acceptedProjects, requestedProjects);
        List<Project> notOutdated =
                projectService.getProjectNotOutDated(notRequestedNotAssignedProjects);

        // DTOs για τα διαθέσιμα projects
        List<ProjectSummaryDTO> availableProjectDTOs = notOutdated.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("notRequestedProjects", availableProjectDTOs);
        body.put("freelancerVerified", freelancer.getVerified());

        return ResponseEntity.ok(body);
    }

    private ProjectSummaryDTO toProjectSummaryDTO(Project project) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();

        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setPaymentAmount(project.getPaymentAmount());
        dto.setProjectStatus(
                project.getProjectStatus() != null ? project.getProjectStatus().name() : null
        );
        dto.setDeadline(project.getDeadline());

        return dto;
    }

    // ================== Freelancer: τα δικά του requests ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-requests/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getMyRequests() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();

        List<Request> requests = freelancer.getRequests();

        List<RequestSummaryDTO> dtoList = requests.stream()
                .map(this::toRequestSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
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


    // ================== Freelancer: assignments ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-assignments/ok-need")
    public ResponseEntity<List<FreelancerAssignmentSummaryDTO>> getMyAssignments() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();

        List<FreelancerAssignmentSummaryDTO> dtoList = freelancer.getAssignments()
                .stream()
                .map(this::toFreelancerAssignmentSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    private FreelancerAssignmentSummaryDTO toFreelancerAssignmentSummaryDTO(Assignment assignment) {
        FreelancerAssignmentSummaryDTO dto = new FreelancerAssignmentSummaryDTO();

        dto.setAssignmentId(assignment.getId());
        dto.setDateSubmitted(assignment.getDateSubmitted());
        dto.setStatus(assignment.getStatus());

        Project p = assignment.getProject();
        dto.setProjectId(p.getId());
        dto.setProjectTitle(p.getTitle());
        dto.setProjectDescription(p.getDescription());
        dto.setPaymentAmount(p.getPaymentAmount());
        dto.setProjectStatus(p.getProjectStatus().name());
        dto.setDeadline(p.getDeadline());

        Client c = p.getClient();
        dto.setClientFullName(c.getFirstName() + " " + c.getLastName());
        dto.setClientPhone(c.getPhone());
        if (c.getUser() != null) {
            dto.setClientEmail(c.getUser().getEmail());
        }

        return dto;
    }

    // ================== Freelancer: profile ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-profile/ok-need")
    public ResponseEntity<FreelancerProfileDTO> getMyProfile() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        FreelancerProfileDTO dto = toFreelancerProfileDTO(freelancer);
        return ResponseEntity.ok(dto);
    }


    private FreelancerProfileDTO toFreelancerProfileDTO(Freelancer freelancer) {
        FreelancerProfileDTO dto = new FreelancerProfileDTO();

        dto.setFreelancerId(freelancer.getId());
        dto.setFirstName(freelancer.getFirstName());
        dto.setLastName(freelancer.getLastName());
        dto.setPhone(freelancer.getPhone());
        dto.setSkills(freelancer.getSkills());
        dto.setVerified(freelancer.getVerified());

        if (freelancer.getUser() != null) {
            dto.setUserId(freelancer.getUser().getId());
            dto.setUsername(freelancer.getUser().getUsername());
            dto.setEmail(freelancer.getUser().getEmail());
        }

        return dto;
    }


    @Secured("ROLE_FREELANCER")
    @PutMapping("/freelancer-use/my-profile/ok-need")
    public ResponseEntity<?> updateMyProfile(
            @Valid @RequestBody FreelancerProfileUpdateDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        // Πάντα από το token, όχι από το body
        Freelancer freelancer = freelancerService.getCurrentFreelancer();

        freelancer.setFirstName(dto.getFirstName());
        freelancer.setLastName(dto.getLastName());
        freelancer.setPhone(dto.getPhone());
        freelancer.setSkills(dto.getSkills());

        freelancerService.saveFreelancer(freelancer);

        FreelancerProfileDTO responseDto = toFreelancerProfileDTO(freelancer);
        return ResponseEntity.ok(responseDto);

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

    private FreelancerSummaryDTO toFreelancerSummaryDTO(Freelancer freelancer) {
        FreelancerSummaryDTO dto = new FreelancerSummaryDTO();
        dto.setId(freelancer.getId());
        dto.setFirstName(freelancer.getFirstName());
        dto.setLastName(freelancer.getLastName());
        dto.setPhone(freelancer.getPhone());
        dto.setSkills(freelancer.getSkills());
        dto.setVerified(freelancer.getVerified());
        return dto;
    }


}
