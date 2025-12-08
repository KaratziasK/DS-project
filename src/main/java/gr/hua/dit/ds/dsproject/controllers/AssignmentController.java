package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.entities.Assignment;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.entities.Status;
import gr.hua.dit.ds.dsproject.services.AssignmentService;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
import gr.hua.dit.ds.dsproject.services.ProjectService;
import gr.hua.dit.ds.dsproject.services.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import gr.hua.dit.ds.dsproject.dto.AssignmentSummaryDTO;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final FreelancerService freelancerService;
    private final ProjectService projectService;
    private final RequestService requestService;

    public AssignmentController(AssignmentService assignmentService,
                                FreelancerService freelancerService,
                                ProjectService projectService,
                                RequestService requestService) {
        this.assignmentService = assignmentService;
        this.freelancerService = freelancerService;
        this.projectService = projectService;
        this.requestService = requestService;
    }

    // ================== Admin: λίστα αναθέσεων ==================

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-use")
    public ResponseEntity<List<AssignmentSummaryDTO>> getAssignments() {
        List<Assignment> assignments = assignmentService.getAssignments();

        List<AssignmentSummaryDTO> dtoList = assignments.stream()
                .map(this::toAssignmentSummaryDTO)
                .toList();  // ή .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    private AssignmentSummaryDTO toAssignmentSummaryDTO(Assignment assignment) {
        AssignmentSummaryDTO dto = new AssignmentSummaryDTO();

        dto.setId(assignment.getId());
        dto.setDateSubmitted(assignment.getDateSubmitted());
        dto.setStatus(assignment.getStatus()); // "Under Construction" / "Completed"

        // Deadline από το project
        if (assignment.getProject() != null) {
            dto.setDeadline(assignment.getProject().getDeadline());
        }

        // Freelancer username
        if (assignment.getFreelancer() != null &&
                assignment.getFreelancer().getUser() != null) {
            dto.setFreelancerUsername(
                    assignment.getFreelancer().getUser().getUsername()
            );
        }

        // Client username (μέσα από το project → client → user)
        if (assignment.getProject() != null &&
                assignment.getProject().getClient() != null &&
                assignment.getProject().getClient().getUser() != null) {
            dto.setClientUsername(
                    assignment.getProject().getClient().getUser().getUsername()
            );
        }

        return dto;
    }



    // ================== Admin: ανάθεση freelancer σε project ==================
    @Secured("ROLE_CLIENT")
    @PostMapping("/client-use/assign-freelancer/{requestId}")
    public ResponseEntity<Project> assignFreelancerToProject(@PathVariable int requestId) {

        Request request = requestService.getRequest(requestId);
        Freelancer freelancer = freelancerService.getFreelancer(request.getFreelancer().getId());
        Project project = projectService.getProject(request.getProject().getId());

        Assignment assignment = new Assignment();

        // Συνδέουμε assignment με freelancer
        List<Assignment> freelancerAssignments = freelancer.getAssignments();
        if (freelancerAssignments == null) {
            freelancerAssignments = new ArrayList<>();
        }
        freelancerAssignments.add(assignment);
        freelancer.setAssignments(freelancerAssignments);
        assignment.setFreelancer(freelancer);

        // Συνδέουμε assignment με project
        project.setAssignment(assignment);
        assignment.setProject(project);

        // Ενημέρωση όλων των requests του project
        List<Request> allRequestsForProject = project.getRequests();
        List<Request> requestsToUpdate = new ArrayList<>(allRequestsForProject);

        for (Request req : requestsToUpdate) {
            if (req.getId().equals(requestId)) {
                req.setRequestStatus(Status.Accepted);
            } else {
                req.setRequestStatus(Status.Rejected);
            }
            requestService.saveRequest(req);
        }

        // Αν το Assignment δεν αποθηκεύεται μέσω cascade,
        // εδώ πρέπει να καλέσεις και assignmentService.saveAssignment(assignment)
        // και πιθανώς projectService.saveProject(project).

        return ResponseEntity.status(HttpStatus.OK).body(project);
    }
}
