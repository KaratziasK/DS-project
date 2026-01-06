package gr.hua.dit.ds.dsproject.dto;

import java.time.LocalDate;

public class ProjectAdminDTO {

    private Integer id;
    private String title;
    private String description;
    private Float paymentAmount;
    private String projectStatus;
    private LocalDate deadline;

    private ClientSummaryDTO client;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ClientSummaryDTO getClient() {
        return client;
    }

    public void setClient(ClientSummaryDTO client) {
        this.client = client;
    }
}

