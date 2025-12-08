package gr.hua.dit.ds.dsproject.dto;

public class ClientProfileUpdateDTO {

    private String firstName;
    private String lastName;
    private String phone;

    // getters & setters

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
