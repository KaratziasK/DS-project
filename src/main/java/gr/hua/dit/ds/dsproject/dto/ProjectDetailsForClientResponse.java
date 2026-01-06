package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;
import java.util.List;

public class ProjectDetailsForClientResponse {
    private int id;
    private String title;
    private String projectStatus;
    private LocalDate deadline;
    private Float paymentAmount;
    private String description;

    private List<AssignedFreelancerDTO> assignedFreelancers;

    public ProjectDetailsForClientResponse() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AssignedFreelancerDTO> getAssignedFreelancers() {
        return assignedFreelancers;
    }

    public void setAssignedFreelancers(List<AssignedFreelancerDTO> assignedFreelancers) {
        this.assignedFreelancers = assignedFreelancers;
    }

    public static class AssignedFreelancerDTO {
        private int id;
        private String firstName;
        private String lastName;
        private String skills;

        public AssignedFreelancerDTO() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getSkills() {
            return skills;
        }

        public void setSkills(String skills) {
            this.skills = skills;
        }
    }
}
