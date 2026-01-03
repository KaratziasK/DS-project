package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.dto.*;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.repositories.ClientRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import gr.hua.dit.ds.dsproject.dto.ClientDTO;
import gr.hua.dit.ds.dsproject.entities.Project;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository,  UserService userService) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public Client getCurrentClient() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println(">>> Authenticated username = " + username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Client client = user.getClient();
        if (client == null) {
            throw new RuntimeException("Current user is not a client: " + username);
        }

        return client;
    }

    @Transactional
    public List<Client> getClients(){
        return clientRepository.findAll();
    }

    @Transactional
    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    @Transactional
    public void updateClient(Client client) {
        Client existingClient = clientRepository.findById(client.getId()).get();

        existingClient.setId(existingClient.getId());
        existingClient.setFirstName(client.getFirstName());
        existingClient.setLastName(client.getLastName());
        existingClient.setPhone(client.getPhone());
    }

    @Transactional
    public List<ClientDTO> getClientDTOs() {
        return clientRepository.findAll()
                .stream()
                .map(this::toClientDTO)
                .collect(Collectors.toList());
    }
    private ClientDTO toClientDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setPhone(client.getPhone());

        if (client.getProjects() != null) {
            List<ProjectSummaryDTO> projectDTOs = client.getProjects()
                    .stream()
                    .map(this::toProjectSummaryDTO)
                    .collect(Collectors.toList());
            dto.setProjects(projectDTOs);
        }

        return dto;
    }

    private ProjectSummaryDTO toProjectSummaryDTO(Project project) {
        ProjectSummaryDTO dto = new ProjectSummaryDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setPaymentAmount(project.getPaymentAmount());

        dto.setProjectStatus(
                project.getProjectStatus() != null ? project.getProjectStatus().toString() : null
        );

        dto.setDeadline(project.getDeadline());

        return dto;
    }

    public ClientProfileDTO toClientProfileDTO(Client client) {
        ClientProfileDTO dto = new ClientProfileDTO();

        dto.setClientId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setPhone(client.getPhone());

        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getId());
            dto.setEmail(client.getUser().getEmail());
            dto.setUsername(client.getUser().getUsername());
        }

        return dto;
    }

    @Transactional
    public void updateCurrentClientProfile(ClientProfileUpdateDTO dto) {
        Client client = getCurrentClient();

        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setPhone(dto.getPhone());

        updateClient(client);
    }
}
