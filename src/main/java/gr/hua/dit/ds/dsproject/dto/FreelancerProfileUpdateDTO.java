package gr.hua.dit.ds.dsproject.dto;

import jakarta.validation.constraints.NotBlank;

public class FreelancerProfileUpdateDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;
    private String skills;


    // getters / setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

}
