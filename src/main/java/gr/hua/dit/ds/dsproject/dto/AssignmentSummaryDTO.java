package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class AssignmentSummaryDTO {

    private Integer id;
    private Integer projectId;
    private Integer freelancerId;

    private String freelancerUsername;
    private String clientUsername;

    private LocalDate dateSubmitted;
    private LocalDate deadline;
    private String status;

    private String projectName;
    private String projectDescription;

    private String freelancerFullName;
    private String clientFullName;
    private Integer clientId;

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getFreelancerId() {
        return freelancerId;
    }
    public void setFreelancerId(Integer freelancerId) {
        this.freelancerId = freelancerId;
    }

    public String getFreelancerUsername() {
        return freelancerUsername;
    }
    public void setFreelancerUsername(String freelancerUsername) {
        this.freelancerUsername = freelancerUsername;
    }

    public String getClientUsername() {
        return clientUsername;
    }
    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }
    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getFreelancerFullName() {
        return freelancerFullName;
    }
    public void setFreelancerFullName(String freelancerFullName) {
        this.freelancerFullName = freelancerFullName;
    }

    public String getClientFullName() {
        return clientFullName;
    }
    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }
}
