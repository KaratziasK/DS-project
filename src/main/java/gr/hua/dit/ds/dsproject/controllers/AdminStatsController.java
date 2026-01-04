package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.AdminStatsDTO;
import gr.hua.dit.ds.dsproject.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminStatsController {

    private final ProjectService projectService;
    private final ClientService clientService;
    private final FreelancerService freelancerService;
    private final RequestService requestService;
    private final AssignmentService assignmentService;

    public AdminStatsController(ProjectService projectService,
                                ClientService clientService,
                                FreelancerService freelancerService,
                                RequestService requestService,
                                AssignmentService assignmentService) {
        this.projectService = projectService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
        this.requestService = requestService;
        this.assignmentService = assignmentService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        AdminStatsDTO dto = new AdminStatsDTO();

        // Projects
        dto.projectsTotal    = projectService.getAllProjectDTOs().size();
        dto.projectsPending  = projectService.getPendingProjectDTOs().size();
        dto.projectsOutdated = projectService.getAllOutdatedProjectDTOs().size();

        // Clients
        dto.clientsTotal = clientService.getClientDTOs().size();

        // Freelancers
        dto.freelancersTotal   = freelancerService.getFreelancerSummaryDTOs().size();
        dto.freelancersPending = freelancerService.getNotVerifiedFreelancerDTOs().size();

        // Requests
        dto.requestsTotal = requestService.getAllRequestSummaries().size();

        // Assignments
        dto.assignmentsTotal = assignmentService.getAssignmentSummaries().size();

        return ResponseEntity.ok(dto);
    }
}
