package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.services.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * GET /api/requests
     * Επιστρέφει όλες τις requests
     */
    @GetMapping("")
    public ResponseEntity<List<Request>> getRequests() {
        List<Request> requests = requestService.getRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * GET /api/requests/rejected
     * Επιστρέφει μόνο τις απορριφθείσες requests
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<Request>> getRejectedRequests() {
        List<Request> rejectedRequests = requestService.getRejectedRequests();
        return ResponseEntity.ok(rejectedRequests);
    }

    /**
     * DELETE /api/requests/rejected/{requestId}
     * Διαγράφει μια rejected request (όπως έκανε το παλιό POST /rejectedRequests/{id})
     */
    @DeleteMapping("/rejected/{requestId}")
    public ResponseEntity<Void> deleteRejectedRequest(@PathVariable int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/requests
     * Δημιουργεί νέα request
     */
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
