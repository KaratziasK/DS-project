package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.*;
import gr.hua.dit.ds.dsproject.entities.*;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
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
@RequestMapping("/api/freelancers")
public class FreelancerController {

    private final FreelancerService freelancerService;
    private final ProjectService projectService;
    private final RequestService requestService;

    public FreelancerController(FreelancerService freelancerService,
                                ProjectService projectService,
                                RequestService requestService) {
        this.freelancerService = freelancerService;
        this.projectService = projectService;
        this.requestService = requestService;
    }

    // ================== Admin: λίστα freelancers ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<FreelancerSummaryDTO>> getFreelancers() {
        return ResponseEntity.ok(freelancerService.getFreelancerSummaryDTOs());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/not-verified/ok-need")
    public ResponseEntity<List<FreelancerSummaryDTO>> getNotVerifiedFreelancers() {
        return ResponseEntity.ok(freelancerService.getNotVerifiedFreelancerDTOs());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/admin-use/{freelancerId}/verify/ok-need")
    public ResponseEntity<FreelancerSummaryDTO> verifyFreelancer(@PathVariable int freelancerId) {
        return ResponseEntity.ok(freelancerService.verifyFreelancerAndReturnDTO(freelancerId));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/admin-use/{freelancerId}/ok-need")
    public ResponseEntity<Void> deleteFreelancer(@PathVariable int freelancerId) {
        freelancerService.deleteFreelancer(freelancerId);
        return ResponseEntity.noContent().build();
    }

    // ================== Freelancer: διαθέσιμα projects ==================

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/projects-available-to-request/ok-need")
    public ResponseEntity<Map<String, Object>> getProjectsForFreelancer() {
        Freelancer freelancer = freelancerService.getCurrentFreelancer();
        return ResponseEntity.ok(projectService.getAvailableProjectsForFreelancer(freelancer));
    }


    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-requests/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getMyRequests() {
        return ResponseEntity.ok(freelancerService.getMyRequestSummaries());
    }

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-assignments/ok-need")
    public ResponseEntity<List<FreelancerAssignmentSummaryDTO>> getMyAssignments() {
        return ResponseEntity.ok(freelancerService.getMyAssignmentSummaries());
    }

    @Secured("ROLE_FREELANCER")
    @GetMapping("/freelancer-use/my-profile/ok-need")
    public ResponseEntity<FreelancerProfileDTO> getMyProfile() {
        return ResponseEntity.ok(freelancerService.getCurrentFreelancerProfile());
    }




    @Secured("ROLE_FREELANCER")
    @PutMapping("/freelancer-use/my-profile/ok-need")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody FreelancerProfileUpdateDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }
        FreelancerProfileDTO responseDto = freelancerService.updateCurrentFreelancerProfile(dto);
        return ResponseEntity.ok(responseDto);
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
