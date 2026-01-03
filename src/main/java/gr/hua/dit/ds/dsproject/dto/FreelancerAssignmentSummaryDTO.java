package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class FreelancerAssignmentSummaryDTO {

    private Integer assignmentId;
    private LocalDate dateSubmitted;
    private String status;

    private Integer projectId;
    private String projectTitle;
    private String projectDescription;
    private Float paymentAmount;
    private String projectStatus;
    private LocalDate deadline;

    private String clientFullName;
    private String clientPhone;
    private String clientEmail;


    public Integer getAssignmentId() {
        return assignmentId;
    }
    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public LocalDate getDateSubmitted() {
        return dateSubmitted;
    }
    public void setDateSubmitted(LocalDate dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProjectId() {
        return projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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

    public Float getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(Float paymentAmount) {
        this.paymentAmount = paymentAmount;
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

    public String getClientFullName() {
        return clientFullName;
    }
    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getClientPhone() {
        return clientPhone;
    }
    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}