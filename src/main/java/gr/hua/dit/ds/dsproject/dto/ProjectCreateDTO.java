package gr.hua.dit.ds.dsproject.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ProjectCreateDTO {

    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters" )
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;

    @Min(value = 50, message = "Payment amount must be at least 50.0 $")
    @Max(value = 10000, message = "Payment amount must be less than 10,000.0 $")
    @NotNull(message = "Payment amount can't be null")
    private Float paymentAmount;

    @NotNull(message = "Deadline can't be null")
    @Future(message = "Deadline must be a future date")
    private LocalDate deadline;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Float getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(Float paymentAmount) { this.paymentAmount = paymentAmount; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}
