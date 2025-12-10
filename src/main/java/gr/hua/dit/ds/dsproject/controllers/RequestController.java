package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.RequestSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.entities.Status;
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
import gr.hua.dit.ds.dsproject.entities.Assignment;
import gr.hua.dit.ds.dsproject.services.AssignmentService;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final AssignmentService assignmentService;

    public RequestController(RequestService requestService,
                             AssignmentService assignmentService) {
        this.requestService = requestService;
        this.assignmentService = assignmentService;
    }


    // Προαιρετικά, αν θέλεις να το βλέπει μόνο client:
    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/get-requests-from-freelancer-for-this-project/{projectId}")
    public ResponseEntity<List<RequestSummaryDTO>> getRequestsFromFreelancersForThisProject(
            @PathVariable int projectId) {

        // Όλα τα requests για το συγκεκριμένο project
        List<Request> requests = requestService.getRequestsByProjectID(projectId);

        List<RequestSummaryDTO> dtoList = requests.stream()
                .map(this::toRequestSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/accept-freelancer-request/{requestId}")
    public ResponseEntity<List<RequestSummaryDTO>> acceptFreelancerRequest(
            @PathVariable int requestId) {

        // 1. Φέρνουμε το συγκεκριμένο request
        Request acceptedRequest = requestService.getRequest(requestId);
        Project project = acceptedRequest.getProject();

        // 2. Όλα τα requests για αυτό το project
        List<Request> requestsForProject =
                requestService.getRequestsByProjectID(project.getId());

        // 3. Accepted το επιλεγμένο, Rejected όλα τα υπόλοιπα
        for (Request req : requestsForProject) {
            if (req.getId().equals(requestId)) {
                req.setRequestStatus(Status.Accepted);
            } else {
                req.setRequestStatus(Status.Rejected);
            }
            requestService.saveRequest(req);
        }

        // 4. Δημιουργούμε Assignment για τον επιλεγμένο freelancer
        Assignment assignment = new Assignment();   // dateSubmitted μπαίνει μόνο του
        assignment.setProject(project);
        assignment.setFreelancer(acceptedRequest.getFreelancer());

        // (προαιρετικό για bidirectional συνέπεια στη μνήμη)
        project.setAssignment(assignment);
        // acceptedRequest.getFreelancer().getAssignments().add(assignment);

        assignmentService.saveAssignment(assignment);

        // 5. Γυρνάμε πίσω τη λίστα σε DTOs
        List<RequestSummaryDTO> dtoList = requestsForProject.stream()
                .map(this::toRequestSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }




    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getRequests() {
        List<Request> requests = requestService.getRequests();

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

    @Secured("ROLE_ADMIN")
    @GetMapping("/rejected/admin-use/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getRejectedRequests() {
        List<Request> rejectedRequests = requestService.getRejectedRequests();

        List<RequestSummaryDTO> dtoList = rejectedRequests.stream()
                .map(this::toRequestSummaryDTO)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @DeleteMapping("/rejected/{requestId}/admin-use/ok-need")
    public ResponseEntity<Void> deleteRejectedRequest(@PathVariable int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("")
    public ResponseEntity<?> createRequest(
            @Valid @RequestBody Request request,
            BindingResult bindingResult) {

        // Validation errors
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Request saved = requestService.saveRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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
