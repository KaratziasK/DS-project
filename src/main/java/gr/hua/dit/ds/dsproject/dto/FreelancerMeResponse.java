package gr.hua.dit.ds.dsproject.dto;

public class FreelancerMeResponse {

    private int id;
    private String email;
    private boolean verified;

    public FreelancerMeResponse() {}

    public FreelancerMeResponse(int id, String email, boolean verified) {
        this.id = id;
        this.email = email;
        this.verified = verified;
    }

    public int getId() { return id; }
    public String getEmail() { return email; }
    public boolean isVerified() { return verified; }

    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
