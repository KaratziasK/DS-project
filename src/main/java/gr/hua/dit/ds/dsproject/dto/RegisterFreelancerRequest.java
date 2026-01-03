package gr.hua.dit.ds.dsproject.dto;

import jakarta.validation.constraints.NotNull;

public class RegisterFreelancerRequest {

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String username;

    @NotNull
    @jakarta.validation.constraints.Email
    private String email;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String password;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String firstName;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String lastName;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String phone;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    private String skills;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
