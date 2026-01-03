package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.dto.AssignmentSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.Assignment;
import gr.hua.dit.ds.dsproject.repositories.AssignmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public List<Assignment> getAssignments() {
        return assignmentRepository.findAll();
    }

    @Transactional
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public List<AssignmentSummaryDTO> getAssignmentSummaries() {
        List<Assignment> assignments = assignmentRepository.findAll();

        return assignments.stream()
                .map(this::toAssignmentSummaryDTO)
                .toList();
    }

    private AssignmentSummaryDTO toAssignmentSummaryDTO(Assignment assignment) {
        AssignmentSummaryDTO dto = new AssignmentSummaryDTO();

        dto.setId(assignment.getId());
        dto.setDateSubmitted(assignment.getDateSubmitted());
        dto.setStatus(assignment.getStatus());

        if (assignment.getProject() != null) {
            dto.setProjectName(assignment.getProject().getTitle());
            dto.setProjectDescription(assignment.getProject().getDescription());
            dto.setDeadline(assignment.getProject().getDeadline());
        }

        if (assignment.getFreelancer() != null) {
            var freelancer = assignment.getFreelancer();

            dto.setFreelancerFullName(
                    freelancer.getFirstName() + " " + freelancer.getLastName()
            );

            if (freelancer.getUser() != null) {
                dto.setFreelancerUsername(freelancer.getUser().getUsername());
            }
        }

        if (assignment.getProject() != null &&
                assignment.getProject().getClient() != null) {

            var client = assignment.getProject().getClient();

            dto.setClientFullName(
                    client.getFirstName() + " " + client.getLastName()
            );

            if (client.getUser() != null) {
                dto.setClientUsername(client.getUser().getUsername());
            }
        }

        return dto;
    }

}
