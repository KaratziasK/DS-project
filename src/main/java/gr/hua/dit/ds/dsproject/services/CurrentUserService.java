package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.repositories.ClientRepository;
import gr.hua.dit.ds.dsproject.repositories.FreelancerRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;

    public CurrentUserService(
            UserRepository userRepository,
            FreelancerRepository freelancerRepository,
            ClientRepository clientRepository
    ) {
        this.userRepository = userRepository;
        this.freelancerRepository = freelancerRepository;
        this.clientRepository = clientRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Freelancer getCurrentFreelancer() {
        User user = getCurrentUser();
        return freelancerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
    }

    public Client getCurrentClient() {
        User user = getCurrentUser();
        return clientRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }
}
