package gr.hua.dit.ds.dsproject.dto;

public class AdminStatsDTO {
    public int projectsTotal;
    public int projectsPending;
    public int projectsOutdated;

    public int clientsTotal;

    public int freelancersTotal;
    public int freelancersPending; // π.χ. not-verified

    public int requestsTotal;
    public int assignmentsTotal;

    public AdminStatsDTO() {}
}
