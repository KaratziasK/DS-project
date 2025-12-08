package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class AssignedProjectDTO {

    private Integer projectId;
    private String title;
    private Float paymentAmount;
    private LocalDate deadline;
    private String freelancerUsername;

    public Integer getProjectId() {
        return projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(Float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getFreelancerUsername() {
        return freelancerUsername;
    }
    public void setFreelancerUsername(String freelancerUsername) {
        this.freelancerUsername = freelancerUsername;
    }
}
