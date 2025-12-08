package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class AssignmentSummaryDTO {

    private Integer id;
    private String freelancerUsername;
    private String clientUsername;
    private LocalDate dateSubmitted;
    private LocalDate deadline;
    private String status;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
}
