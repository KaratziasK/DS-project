package gr.hua.dit.ds.dsproject.services;


import gr.hua.dit.ds.dsproject.dto.AssignedProjectDTO;
import gr.hua.dit.ds.dsproject.dto.ProjectCreateDTO;
import gr.hua.dit.ds.dsproject.dto.ProjectSummaryDTO;
import gr.hua.dit.ds.dsproject.entities.*;
import gr.hua.dit.ds.dsproject.repositories.ProjectRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientService clientService;

    public ProjectService(ProjectRepository projectRepository, ClientService clientService) {
        this.projectRepository = projectRepository;
        this.clientService = clientService;
    }

    @Transactional
    public List<Project> getProjects(){
        return projectRepository.findAll();
    }

    @Transactional
    public List<Project> getProjectsPending(){
        List<Project> projects = projectRepository.findAll();
        List<Project> projectsPending = new ArrayList<>();
        for (Project p : projects) {
            if(p.getProjectStatus().equals(Status.Pending) && p.getDeadline().isAfter(LocalDate.now())){
                projectsPending.add(p);
            }
        }
        return projectsPending;
    }

    @Transactional
    public List<Project> getAllOutdatedProjects(){
        List<Project> projects = projectRepository.findAll();
        List<Project> projectsOutdated = new ArrayList<>();
        for (Project p : projects) {
            if(p.getDeadline().isBefore(LocalDate.now())){
                projectsOutdated.add(p);
            }
        }
        return projectsOutdated;
    }

    @Transactional
    public List<Project> getProjectNotOutDated(List <Project> FilterProjects){
        List<Project> projectsNotOutdated = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Project p : FilterProjects) {
            if(p.getDeadline().isAfter(today)){
                projectsNotOutdated.add(p);
            }
        }
        return projectsNotOutdated;
    }

    @Transactional
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project getProject(Integer projectId) {
        return projectRepository.findById(projectId).get();
    }

    @Transactional
    public List<Project> getAcceptedProjects() {
        List<Project> projects = projectRepository.findAll();
        List<Project> projectsAccepted = new ArrayList<>();
        for (Project p : projects) {
            if(p.getProjectStatus().equals(Status.Accepted) && p.getDeadline().isAfter(LocalDate.now())){
                projectsAccepted.add(p);
            }
        }
        return projectsAccepted;
    }

    @Transactional
    public List<Project> getRejectedProjects() {
        List<Project> projects = projectRepository.findAll();
        List<Project> projectsRejected = new ArrayList<>();
        for (Project p : projects) {
            if(p.getProjectStatus().equals(Status.Rejected)){
                projectsRejected.add(p);
            }
        }
        return projectsRejected;
    }

    @Transactional
    public List<Project> getRequestedProjects(List<Project> projects , List<Request> requests) {

        List<Project> requestedProjects = new ArrayList<>();

        for(Project project: projects){
            for(Request request : project.getRequests()){
                if (requests.contains(request)) {
                    requestedProjects.add(project);
                }
            }
        }

        return requestedProjects;
    }

    @Transactional
    public List<Project> getNotRequestedAndUnassignedProjects(List<Project> accProjects, List<Project> reqProjects ){
        List<Project> notRequestedNotAssignedProjects = new ArrayList<>();

        for (Project p : accProjects) {
            if(!reqProjects.contains(p) && p.getAssignment() == null){
                notRequestedNotAssignedProjects.add(p);
            }
        }
        return notRequestedNotAssignedProjects;
    }

    @Transactional
    public Request assignRequestToProject(Integer projectId, Freelancer freelancer) {

        Project project = projectRepository.findById(projectId).orElseThrow();
        for (Request r : project.getRequests()) {
            if (r.getFreelancer() != null &&
                    r.getFreelancer().getId().equals(freelancer.getId())) {
                throw new RuntimeException("You have already requested this project");
            }
        }

        Request request = new Request();
        freelancer.getRequests().add(request);
        request.setFreelancer(freelancer);
        project.getRequests().add(request);
        request.setProject(project);
        return request;
    }

    @Transactional
    public void deleteProject(Integer projectId) {
        List<Project> projects = projectRepository.findAll();
        for(Project p : projects){
            if(p.getId().equals(projectId)){
                projectRepository.delete(p);
            }
        }
    }

    @Transactional
    public List<Project> getUnassignedProjects(Client  client) {
        List<Project> projects = projectRepository.findAll();
        List<Project> unassignedProjects = new ArrayList<>();

        for (Project project : projects) {
            if (project.getAssignment() == null && project.getClient() == client
                    && project.getProjectStatus() == Status.Accepted) {
                unassignedProjects.add(project);
            }
        }
        return unassignedProjects;
    }

    @Transactional
    public List<Project> getAssignedProjects(Client client) {
        List<Project> projects = projectRepository.findAll();
        List<Project> assignedProjects = new ArrayList<>();
        for (Project p : projects) {
            if (p.getClient().equals(client) && p.getAssignment() != null) {
                assignedProjects.add(p);
            }
        }
        return assignedProjects;
    }

    @Transactional
    public List<Project> getUnassignedAndOutdatedProjects(Client  client) {
        List<Project> unassignedProjects = getUnassignedProjects(client);
        List<Project> UnassignedAndOutdated = new ArrayList<>();

        for (Project project : unassignedProjects) {
            if (project.getDeadline().isBefore(LocalDate.now())) {
                UnassignedAndOutdated.add(project);
            }
        }
        return UnassignedAndOutdated;
    }

    @Transactional
    public List<Project> getCompletedProjects(Client  client) {
        List<Project> projects = projectRepository.findAll();
        List<Project> completedProjects = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Project project : projects) {
            if (project.getAssignment() != null && project.getClient() == client &&
                    project.getProjectStatus() == Status.Accepted
                    && project.getDeadline().isBefore(today)) {
                completedProjects.add(project);
            }
        }
        return completedProjects;
    }

    @Transactional
    public List<Project> getAllProjectForThisClient(Client currentClient) {
        List<Project> projects = projectRepository.findAll();
        List<Project> clientProjects = new ArrayList<>();

        for (Project p : projects) {
            if (p.getClient() != null && p.getClient().equals(currentClient)) {
                clientProjects.add(p);
            }
        }

        return clientProjects;
    }

    @Transactional
    public Map<String, Object> getAvailableProjectsForFreelancer(Freelancer freelancer) {

        List<Project> acceptedProjects = getAcceptedProjects();

        List<Project> requestedProjects =
                getRequestedProjects(acceptedProjects, freelancer.getRequests());

        List<Project> notRequestedNotAssigned =
                getNotRequestedAndUnassignedProjects(acceptedProjects, requestedProjects);

        List<Project> available =
                getProjectNotOutDated(notRequestedNotAssigned);

        List<ProjectSummaryDTO> dtoList = available.stream()
                .map(this::toProjectSummaryDTO)
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("notRequestedProjects", dtoList);
        body.put("freelancerVerified", freelancer.getVerified());

        return body;
    }


    private ProjectSummaryDTO toProjectSummaryDTO(Project project) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();

        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setPaymentAmount(project.getPaymentAmount());
        dto.setProjectStatus(
                project.getProjectStatus() != null
                        ? project.getProjectStatus().name()
                        : null
        );
        dto.setDeadline(project.getDeadline());

        return dto;
    }

    @Transactional
    public ProjectSummaryDTO createProjectForCurrentClient(ProjectCreateDTO projectDto) {

        Client currentClient = clientService.getCurrentClient();
        if (currentClient == null) {
            throw new IllegalStateException("No client is associated with the current user");
        }

        Project project = toProjectEntity(projectDto, currentClient);
        Project saved = saveProject(project);
        return toProjectSummaryDTO(saved);
    }

    private Project toProjectEntity(ProjectCreateDTO dto, Client client) {
        Project project = new Project();

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setPaymentAmount(dto.getPaymentAmount());
        project.setDeadline(dto.getDeadline());
        project.setClient(client);

        return project;
    }

    @Transactional
    public List<ProjectSummaryDTO> getPendingProjectDTOs() {
        List<Project> pending = getProjectsPending();

        return pending.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public ProjectSummaryDTO acceptProject(int projectId) {
        Project project = getProject(projectId);

        project.setProjectStatus(Status.Accepted);

        Project saved = saveProject(project);

        return toProjectSummaryDTO(saved);
    }

    @Transactional
    public ProjectSummaryDTO rejectProject(int projectId) {
        Project project = getProject(projectId);
        project.setProjectStatus(Status.Rejected);
        Project saved = saveProject(project);
        return toProjectSummaryDTO(saved);
    }

    @Transactional
    public List<ProjectSummaryDTO> getRejectedProjectDTOs() {
        List<Project> rejected = getRejectedProjects();

        return rejected.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> getAllOutdatedProjectDTOs() {
        List<Project> outdated = getAllOutdatedProjects();

        return outdated.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> getAllProjectsForCurrentClient() {

        Client currentClient = clientService.getCurrentClient();

        List<Project> projects = getAllProjectForThisClient(currentClient);

        return projects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> getUnassignedProjectsForCurrentClient() {

        Client currentClient = clientService.getCurrentClient();

        List<Project> unassigned = getUnassignedProjects(currentClient);

        return unassigned.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<AssignedProjectDTO> getAssignedProjectsForCurrentClient() {
        Client currentClient = clientService.getCurrentClient();

        List<Project> assignedProjects = getAssignedProjects(currentClient);

        return assignedProjects.stream()
                .map(this::toAssignedProjectDTO)
                .toList();
    }

    private AssignedProjectDTO toAssignedProjectDTO(Project project) {
        AssignedProjectDTO dto = new AssignedProjectDTO();

        dto.setProjectId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setPaymentAmount(project.getPaymentAmount());
        dto.setDeadline(project.getDeadline());

        if (project.getAssignment() != null &&
                project.getAssignment().getFreelancer() != null &&
                project.getAssignment().getFreelancer().getUser() != null) {
            dto.setFreelancerUsername(
                    project.getAssignment().getFreelancer().getUser().getUsername()
            );
        }

        return dto;
    }

    @Transactional
    public List<ProjectSummaryDTO> getUnassignedAndOutdatedProjectsForCurrentClient() {

        Client currentClient = clientService.getCurrentClient();

        List<Project> unassignedOutdated = getUnassignedAndOutdatedProjects(currentClient);

        return unassignedOutdated.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<AssignedProjectDTO> getCompletedProjectsForCurrentClient() {
        Client currentClient = clientService.getCurrentClient();

        List<Project> completedProjects = getCompletedProjects(currentClient);

        return completedProjects.stream()
                .map(this::toAssignedProjectDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> deleteUnassignedOutdatedProjectAndReturnList(int projectId) {

        deleteProject(projectId);

        Client currentClient = clientService.getCurrentClient();

        List<Project> updatedList = getUnassignedAndOutdatedProjects(currentClient);

        return updatedList.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> getAcceptedProjectDTOs() {
        List<Project> accepted = getAcceptedProjects();

        return accepted.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

    @Transactional
    public List<ProjectSummaryDTO> getAllProjectDTOs() {
        List<Project> projects = getProjects();

        return projects.stream()
                .map(this::toProjectSummaryDTO)
                .toList();
    }

}
