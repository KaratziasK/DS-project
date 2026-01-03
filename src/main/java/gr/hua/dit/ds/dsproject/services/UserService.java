package gr.hua.dit.ds.dsproject.services;

import gr.hua.dit.ds.dsproject.dto.RegisterClientRequest;
import gr.hua.dit.ds.dsproject.dto.RegisterFreelancerRequest;
import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.Role;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.repositories.RoleRepository;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Integer saveUser(User user, String roleName) {
        String passwd = user.getPassword();
        String encodedPassword = passwordEncoder.encode(passwd);
        user.setPassword(encodedPassword);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user = userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByUsername(username);

        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User with username: " + username + " not found!");
        } else {
            User user = opt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toSet())
            );
        }
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public Map<String, Object> registerClient(RegisterClientRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (findByUsername(request.getUsername()).isPresent()) {
            errors.put("username", "Username is already in use. Please choose another one.");
        }
        if (findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhone(request.getPhone());

        user.setClient(client);
        client.setUser(user);

        Integer id = saveUser(user, "ROLE_CLIENT");


        return Map.of(
                "id", id,
                "message", "Client user created successfully",
                "username", user.getUsername(),
                "email", user.getEmail()
        );
    }

    @Transactional
    public Map<String, Object> registerFreelancer(RegisterFreelancerRequest request) {

        Map<String, String> errors = new HashMap<>();

        if (findByUsername(request.getUsername()).isPresent()) {
            errors.put("username", "Username is already in use. Please choose another one.");
        }
        if (findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        Freelancer freelancer = new Freelancer();
        freelancer.setFirstName(request.getFirstName());
        freelancer.setLastName(request.getLastName());
        freelancer.setPhone(request.getPhone());
        freelancer.setSkills(request.getSkills());

        user.setFreelancer(freelancer);
        freelancer.setUser(user);

        Integer id = saveUser(user, "ROLE_FREELANCER");


        return Map.of(
                "id", id,
                "message", "Freelancer user created successfully",
                "username", user.getUsername(),
                "email", user.getEmail()
        );
    }

}
