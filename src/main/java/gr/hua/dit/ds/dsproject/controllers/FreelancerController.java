package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.FreelancerProfileUpdateDTO;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.entities.Assignment;
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
    @GetMapping("")
    public ResponseEntity<List<Freelancer>> getFreelancers() {
        return ResponseEntity.ok(freelancerService.getFreelancers());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/not-verified")
    public ResponseEntity<List<Freelancer>> getNotVerifiedFreelancers() {
        return ResponseEntity.ok(freelancerService.getNotVerifiedFreelancer());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{freelancerId}/verify")
    public ResponseEntity<Freelancer> verifyFreelancer(@PathVariable("freelancerId") int freelancerId) {
        Freelancer freelancer = freelancerService.getFreelancer(freelancerId);
        freelancer.setVerified(true);
        freelancerService.saveFreelancer(freelancer);
        return ResponseEntity.ok(freelancer);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{freelancerId}")
    public ResponseEntity<Void> deleteFreelancer(@PathVariable("freelancerId") int freelancerId) {
        freelancerService.deleteFreelancer(freelancerId);
        return ResponseEntity.noContent().build();
    }

    // ================== Admin: δημιουργία freelancer (αντί για /new form) ==================

    @Secured("ROLE_ADMIN")
    @PostMapping("")
    public ResponseEntity<?> createFreelancer(
            @Valid @RequestBody Freelancer freelancer,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        freelancerService.saveFreelancer(freelancer);
        return ResponseEntity.status(HttpStatus.CREATED).body(freelancer);
    }

    // ================== Freelancer: διαθέσιμα projects ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/projects")
    public ResponseEntity<Map<String, Object>> getProjectsForFreelancer() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        List<Project> acceptedProjects = projectService.getAcceptedProjects();
        List<Project> requestedProjects =
                projectService.getRequestedProjects(acceptedProjects, freelancer.getRequests());
        List<Project> notRequestedNotAssignedProjects =
                projectService.getNotRequestedAndUnassignedProjects(acceptedProjects, requestedProjects);
        List<Project> notOutdated =
                projectService.getProjectNotOutDated(notRequestedNotAssignedProjects);

        Map<String, Object> body = new HashMap<>();
        body.put("notRequestedProjects", notOutdated);
        body.put("freelancerVerified", freelancer.getVerified());

        return ResponseEntity.ok(body);
    }

    // ================== Freelancer: τα δικά του requests ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/requests")
    public ResponseEntity<List<Request>> getMyRequests() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        return ResponseEntity.ok(freelancer.getRequests());
    }

    // ================== Freelancer: assignments ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/my-assignments")
    public ResponseEntity<List<Assignment>> getMyAssignments() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        return ResponseEntity.ok(freelancer.getAssignments());
    }

    // ================== Freelancer: profile ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/my-profile")
    public ResponseEntity<Freelancer> getMyProfile() {
        Freelancer freelancer =
                freelancerService.getFreelancer(freelancerService.getCurrentFreelancer().getId());
        return ResponseEntity.ok(freelancer);
    }

    @Secured("ROLE_FREELANCER")
    @PutMapping("/my-profile")
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

        return ResponseEntity.ok(freelancer);
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
