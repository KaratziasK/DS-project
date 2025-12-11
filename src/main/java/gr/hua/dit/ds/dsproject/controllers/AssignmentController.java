package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.RequestSummaryDTO;
import gr.hua.dit.ds.dsproject.services.AssignmentService;
import gr.hua.dit.ds.dsproject.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import gr.hua.dit.ds.dsproject.dto.AssignmentSummaryDTO;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final RequestService requestService;

    public AssignmentController(AssignmentService assignmentService,
                                RequestService requestService) {
        this.assignmentService = assignmentService;
        this.requestService = requestService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<AssignmentSummaryDTO>> getAssignments() {
        List<AssignmentSummaryDTO> dtoList = assignmentService.getAssignmentSummaries();
        return ResponseEntity.ok(dtoList);
    }

    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/accept-freelancer-request/{requestId}/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> acceptFreelancerRequest(
            @PathVariable int requestId) {

        return ResponseEntity.ok(requestService.acceptFreelancerRequest(requestId));
    }
}
