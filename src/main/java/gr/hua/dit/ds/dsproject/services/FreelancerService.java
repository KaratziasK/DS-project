package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.dto.*;
import gr.hua.dit.ds.dsproject.entities.*;
import gr.hua.dit.ds.dsproject.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FreelancerService {

    private final FreelancerRepository freelancerRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final RequestRepository requestRepository;
    private final ProjectRepository projectRepository;
    private final RequestService requestService;
    private final ProjectService projectService;

    public FreelancerService(FreelancerRepository freelancerRepository, UserRepository userRepository,
                             AssignmentRepository assignmentRepository, RequestRepository requestRepository,
                             ProjectRepository projectRepository,
                            RequestService requestService,
                             ProjectService projectService
    ) {
        this.freelancerRepository = freelancerRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.requestRepository = requestRepository;
        this.projectRepository = projectRepository;
        this.requestService = requestService;
        this.projectService = projectService;
    }

    @Transactional
    public List<FreelancerSummaryDTO> getFreelancerSummaryDTOs() {
        List<Freelancer> freelancers = getFreelancers();

        return freelancers.stream()
                .map(this::toFreelancerSummaryDTO)
                .toList();
    }

    private FreelancerSummaryDTO toFreelancerSummaryDTO(Freelancer freelancer) {

        FreelancerSummaryDTO dto = new FreelancerSummaryDTO();

        dto.setId(freelancer.getId());
        dto.setFirstName(freelancer.getFirstName());
        dto.setLastName(freelancer.getLastName());
        dto.setPhone(freelancer.getPhone());

        // Skills (αν είναι String ή concatenated από entity)
        dto.setSkills(freelancer.getSkills());

        // Verified status
        dto.setVerified(freelancer.getVerified());

        return dto;
    }

    @Transactional
    public List<FreelancerSummaryDTO> getNotVerifiedFreelancerDTOs() {
        List<Freelancer> notVerified = getNotVerifiedFreelancer();

        return notVerified.stream()
                .map(this::toFreelancerSummaryDTO)
                .toList();
    }

    @Transactional
    public FreelancerSummaryDTO verifyFreelancerAndReturnDTO(int freelancerId) {

        Freelancer freelancer = getFreelancer(freelancerId);

        freelancer.setVerified(true);
        saveFreelancer(freelancer);

        return toFreelancerSummaryDTO(freelancer);
    }


    @Transactional
    public List<Freelancer> getFreelancers(){
        return freelancerRepository.findAll();
    }

    @Transactional
    public List<Freelancer> getNotVerifiedFreelancer() {
        List<Freelancer> freelancers = freelancerRepository.findAll();
        List<Freelancer> notVerifiedFreelancers = new ArrayList<>();
        for (Freelancer f : freelancers) {
            if(!f.getVerified()) {
                notVerifiedFreelancers.add(f);
            }
        }
        return notVerifiedFreelancers;
    }

    @Transactional
    public void saveFreelancer(Freelancer freelancer) {freelancerRepository.save(freelancer);}

    @Transactional
    public void updateFreelancer(Freelancer freelancer) {
        Freelancer existingFreelancer = freelancerRepository.findById(freelancer.getId()).get();

        existingFreelancer.setId(existingFreelancer.getId());
        existingFreelancer.setFirstName(freelancer.getFirstName());
        existingFreelancer.setLastName(freelancer.getLastName());
        existingFreelancer.setPhone(freelancer.getPhone());
        existingFreelancer.setSkills(freelancer.getSkills());
    }

    @Transactional
    public Freelancer getFreelancer(Integer freelancerId) {
        return freelancerRepository.findById(freelancerId).get();
    }

    @Transactional
    public Freelancer getCurrentFreelancer() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Freelancer freelancer = user.getFreelancer();
        if (freelancer == null) {
            throw new RuntimeException("Freelancer not found for username: " + username);
        }

        return freelancer;
    }

    @Transactional
    public void deleteFreelancer(Integer freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        List<Assignment> assignments = freelancer.getAssignments();
        List<Request> requests = freelancer.getRequests();

        // Αποσύνδεση από τα requests
        if (requests != null && !requests.isEmpty()) {
            for (Request request : requests) {
                request.setFreelancer(null);  // Αποσύνδεση του freelancer από το request
                requestRepository.save(request); // Αποθήκευση για την ανανέωση της κατάστασης
            }
            // Διαγραφή των requests
            requestRepository.deleteAll(requests);
        }

        // Αποσύνδεση από τα assignments
        if (assignments != null && !assignments.isEmpty()) {
            for (Assignment assignment : assignments) {
                assignment.setFreelancer(null);  // Αποσύνδεση του freelancer από το assignment
                Project project = assignment.getProject();
                project.setAssignment(null);
                assignment.setProject(null);
                projectRepository.save(project);
                assignmentRepository.save(assignment); // Αποθήκευση για την ανανέωση της κατάστασης
            }
            // Διαγραφή των assignments
            assignmentRepository.deleteAll(assignments);
        }
        // Διαγραφή του freelancer
        freelancerRepository.delete(freelancer);

        List<Assignment> allAssignments = assignmentRepository.findAll();
        for (Assignment assignment : allAssignments) {
            if (assignment.getFreelancer() == null) {
                assignmentRepository.delete(assignment);
            }
        }
    }

    @Transactional
    public List<RequestSummaryDTO> getMyRequestSummaries() {

        Freelancer freelancer = getCurrentFreelancer();
        List<Request> requests = freelancer.getRequests();

        return requestService.toRequestSummaryDTOs(requests);
    }

    @Transactional
    public List<FreelancerAssignmentSummaryDTO> getMyAssignmentSummaries() {

        Freelancer freelancer = getCurrentFreelancer();

        return freelancer.getAssignments()
                .stream()
                .map(this::toFreelancerAssignmentSummaryDTO)
                .toList();
    }

    private FreelancerAssignmentSummaryDTO toFreelancerAssignmentSummaryDTO(Assignment assignment) {
        FreelancerAssignmentSummaryDTO dto = new FreelancerAssignmentSummaryDTO();

        dto.setAssignmentId(assignment.getId());
        dto.setDateSubmitted(assignment.getDateSubmitted());
        dto.setStatus(assignment.getStatus());

        Project p = assignment.getProject();
        dto.setProjectId(p.getId());
        dto.setProjectTitle(p.getTitle());
        dto.setProjectDescription(p.getDescription());
        dto.setPaymentAmount(p.getPaymentAmount());
        dto.setProjectStatus(p.getProjectStatus().name());
        dto.setDeadline(p.getDeadline());

        Client c = p.getClient();
        dto.setClientFullName(c.getFirstName() + " " + c.getLastName());
        dto.setClientPhone(c.getPhone());
        if (c.getUser() != null) {
            dto.setClientEmail(c.getUser().getEmail());
        }
        return dto;
    }

    @Transactional
    public FreelancerProfileDTO getCurrentFreelancerProfile() {
        Freelancer freelancer = getCurrentFreelancer();
        return toFreelancerProfileDTO(freelancer);
    }


    private FreelancerProfileDTO toFreelancerProfileDTO(Freelancer freelancer) {
        FreelancerProfileDTO dto = new FreelancerProfileDTO();

        dto.setFreelancerId(freelancer.getId());
        dto.setFirstName(freelancer.getFirstName());
        dto.setLastName(freelancer.getLastName());
        dto.setPhone(freelancer.getPhone());
        dto.setSkills(freelancer.getSkills());
        dto.setVerified(freelancer.getVerified());

        if (freelancer.getUser() != null) {
            dto.setUserId(freelancer.getUser().getId());
            dto.setUsername(freelancer.getUser().getUsername());
            dto.setEmail(freelancer.getUser().getEmail());
        }

        return dto;
    }

    @Transactional
    public FreelancerProfileDTO updateCurrentFreelancerProfile(FreelancerProfileUpdateDTO dto) {
        // Πάντα από το token, όχι από το body
        Freelancer freelancer = getCurrentFreelancer();

        freelancer.setFirstName(dto.getFirstName());
        freelancer.setLastName(dto.getLastName());
        freelancer.setPhone(dto.getPhone());
        freelancer.setSkills(dto.getSkills());

        saveFreelancer(freelancer);

        return toFreelancerProfileDTO(freelancer);
    }

    @Transactional
    public RequestSummaryDTO makeRequestForProject(int projectId) {
        // 1. Πάντα από το token
        Freelancer freelancer = getCurrentFreelancer();

        // 2. Δημιουργία Request για το συγκεκριμένο project
        Request newRequest = projectService.assignRequestToProject(projectId, freelancer);

        // 3. Mapping σε DTO (λογική στο RequestService)
        return requestService.toRequestSummaryDTO(newRequest);
    }

}
