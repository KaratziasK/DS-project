package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class RequestSummaryDTO {

    private String requestStatus;
    private String projectTitle;
    private String projectDescription;
    private LocalDate dateSubmitted;
    private String freelancerUsername;
    private Integer id;
    private Integer projectId;
    private Integer freelancerId;
    private Integer clientId;

    public String getRequestStatus() {
        return requestStatus;
    }
    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getProjectTitle() {
        return projectTitle;
    }
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }
    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getFreelancerUsername() {
        return freelancerUsername;
    }
    public void setFreelancerUsername(String freelancerUsername) {
        this.freelancerUsername = freelancerUsername;
    }
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

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}
