package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class ProjectDetailsDTO {
    private Integer id;
    private String title;
    private String description;
    private Float paymentAmount;
    private String projectStatus;
    private LocalDate deadline;
    private String clientName; // optional

    public ProjectDetailsDTO() {}

    public ProjectDetailsDTO(Integer id, String title, String description, Float paymentAmount, String projectStatus, LocalDate deadline, String clientName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.paymentAmount = paymentAmount;
        this.projectStatus = projectStatus;
        this.deadline = deadline;
        this.clientName = clientName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Float getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(Float paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getProjectStatus() { return projectStatus; }
    public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
}
