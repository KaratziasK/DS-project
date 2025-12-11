package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.dto.RequestSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.Assignment;
import gr.hua.dit.ds.dsproject.entities.Project;
import gr.hua.dit.ds.dsproject.entities.Request;
import gr.hua.dit.ds.dsproject.entities.Status;
import gr.hua.dit.ds.dsproject.repositories.RequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final AssignmentService assignmentService;

    public RequestService(RequestRepository requestRepository,
                          AssignmentService assignmentService) {
        this.requestRepository = requestRepository;
        this.assignmentService = assignmentService;
    }

    // ---------------- Βασικές CRUD / Queries ----------------

    @Transactional
    public Request getRequest(Integer id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found: " + id));
    }

    @Transactional
    public List<Request> getRequestsByProjectID(Integer projectId) {
        // Προτιμάμε απευθείας query στο repository
        return requestRepository.findByProjectId(projectId);
    }

    @Transactional
    public Request saveRequest(Request request) {
        return requestRepository.save(request);
    }

    @Transactional
    public List<Request> getRequests() {
        return requestRepository.findAll();
    }

    @Transactional
    public List<Request> getRejectedRequests() {
        List<Request> requests = requestRepository.findAll();
        List<Request> rejectedRequests = new ArrayList<>();

        for (Request r : requests) {
            if (Status.Rejected.equals(r.getRequestStatus())) {
                rejectedRequests.add(r);
            }
        }
        return rejectedRequests;
    }

    @Transactional
    public void deleteRequest(Integer requestId) {
        requestRepository.deleteById(requestId);
    }

    // ---------------- Business logic: acceptFreelancerRequest ----------------

    @Transactional
    public List<RequestSummaryDTO> acceptFreelancerRequest(int requestId) {
        // 1. Φέρνουμε το συγκεκριμένο request
        Request acceptedRequest = getRequest(requestId);
        Project project = acceptedRequest.getProject();

        // 2. Όλα τα requests για αυτό το project
        List<Request> requestsForProject = getRequestsByProjectID(project.getId());

        // 3. Accepted το επιλεγμένο, Rejected όλα τα υπόλοιπα
        for (Request req : requestsForProject) {
            if (req.getId().equals(requestId)) {
                req.setRequestStatus(Status.Accepted);
            } else {
                req.setRequestStatus(Status.Rejected);
            }
            saveRequest(req);
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
        return requestsForProject.stream()
                .map(this::toRequestSummaryDTO)
                .toList();
    }

    // ===== Helper για mapping Request -> RequestSummaryDTO =====
    public RequestSummaryDTO toRequestSummaryDTO(Request request) {
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

    @Transactional
    public List<RequestSummaryDTO> toRequestSummaryDTOs(List<Request> requests) {
        return requests.stream()
                .map(this::toRequestSummaryDTO)
                .toList();
    }

    @Transactional
    public List<RequestSummaryDTO> getRequestSummariesByProjectId(int projectId) {
        List<Request> requests = getRequestsByProjectID(projectId);
        return toRequestSummaryDTOs(requests);
    }

    @Transactional
    public List<RequestSummaryDTO> getAllRequestSummaries() {
        List<Request> requests = getRequests();
        return toRequestSummaryDTOs(requests);
    }

    @Transactional
    public List<RequestSummaryDTO> getRejectedRequestSummaries() {
        List<Request> rejected = getRejectedRequests();
        return toRequestSummaryDTOs(rejected);
    }

}
