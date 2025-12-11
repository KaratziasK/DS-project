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

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @Secured("ROLE_CLIENT")
    @GetMapping("/client-use/get-requests-from-freelancer-for-this-project/{projectId}/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getRequestsFromFreelancersForThisProject(@PathVariable int projectId) {
        return ResponseEntity.ok(requestService.getRequestSummariesByProjectId(projectId));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getRequests() {
        return ResponseEntity.ok(requestService.getAllRequestSummaries());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/rejected/admin-use/ok-need")
    public ResponseEntity<List<RequestSummaryDTO>> getRejectedRequests() {
        return ResponseEntity.ok(requestService.getRejectedRequestSummaries());
    }

    @DeleteMapping("/rejected/{requestId}/admin-use/ok-need")
    public ResponseEntity<Void> deleteRejectedRequest(@PathVariable int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

}
