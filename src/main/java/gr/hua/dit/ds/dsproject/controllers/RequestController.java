package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.RequestSummaryDTO;
import gr.hua.dit.ds.dsproject.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/get-requests-from-freelancer-for-this-project/{projectId}")
    public ResponseEntity<List<RequestSummaryDTO>> getRequestsFromFreelancersForThisProject(@PathVariable int projectId) {
        return ResponseEntity.ok(requestService.getRequestSummariesByProjectId(projectId));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use")
    public ResponseEntity<List<RequestSummaryDTO>> getRequests() {
        return ResponseEntity.ok(requestService.getAllRequestSummaries());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/rejected/admin-use")
    public ResponseEntity<List<RequestSummaryDTO>> getRejectedRequests() {
        return ResponseEntity.ok(requestService.getRejectedRequestSummaries());
    }

    @DeleteMapping("/rejected/{requestId}/admin-use")
    public ResponseEntity<Void> deleteRejectedRequest(@PathVariable int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

}
