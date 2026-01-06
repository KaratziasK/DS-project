package gr.hua.dit.ds.dsproject.dto;

public class ProjectDetailsForFreelancerResponse {

    private boolean freelancerVerified;
    private boolean requested;
    private String myRequestStatus; // nullable
    private ProjectDetailsDTO project;

    public ProjectDetailsForFreelancerResponse() {}

    public ProjectDetailsForFreelancerResponse(boolean freelancerVerified, boolean requested, String myRequestStatus, ProjectDetailsDTO project) {
        this.freelancerVerified = freelancerVerified;
        this.requested = requested;
        this.myRequestStatus = myRequestStatus;
        this.project = project;
    }

    public boolean isFreelancerVerified() { return freelancerVerified; }
    public void setFreelancerVerified(boolean freelancerVerified) { this.freelancerVerified = freelancerVerified; }

    public boolean isRequested() { return requested; }
    public void setRequested(boolean requested) { this.requested = requested; }

    public String getMyRequestStatus() { return myRequestStatus; }
    public void setMyRequestStatus(String myRequestStatus) { this.myRequestStatus = myRequestStatus; }

    public ProjectDetailsDTO getProject() { return project; }
    public void setProject(ProjectDetailsDTO project) { this.project = project; }
}
