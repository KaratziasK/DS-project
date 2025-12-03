package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.User;
import gr.hua.dit.ds.dsproject.services.ClientService;
import gr.hua.dit.ds.dsproject.services.EmailService;
import gr.hua.dit.ds.dsproject.services.FreelancerService;
import gr.hua.dit.ds.dsproject.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final ClientService clientService;
    private final FreelancerService freelancerService;
    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService,
                          ClientService clientService,
                          FreelancerService freelancerService,
                          EmailService emailService) {
        this.userService = userService;
        this.clientService = clientService;
        this.freelancerService = freelancerService;
        this.emailService = emailService;
    }

    // ================== Client registration ==================

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(
            @Valid @RequestBody RegisterClientRequest request,
            BindingResult bindingResult) {

        // 1. Bean Validation errors (User/Client)
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        User user = request.getUser();
        Client client = request.getClient();

        // 2. Business validation – unique username/email
        Map<String, String> errors = new HashMap<>();

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            errors.put("user.username", "Username is already in use. Please choose another one.");
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            errors.put("user.email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", errors));
        }

        // 3. Set relations
        user.setClient(client);
        client.setUser(user);

        // 4. Persist
        Integer id = userService.saveUser(user, "ROLE_CLIENT");
        clientService.saveClient(client);

        // 5. Send email
        String fullName = client.getFirstName() + " " + client.getLastName();
        emailService.sendSignupEmailToClient(user.getEmail(), fullName);

        // 6. Response
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "Client user created successfully");
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // ================== Freelancer registration ==================

    @PostMapping("/register/freelancer")
    public ResponseEntity<?> registerFreelancer(
            @Valid @RequestBody RegisterFreelancerRequest request,
            BindingResult bindingResult) {

        // 1. Bean Validation errors (User/Freelancer)
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        User user = request.getUser();
        Freelancer freelancer = request.getFreelancer();

        // 2. Business validation – unique username/email
        Map<String, String> errors = new HashMap<>();

        if (userService.findByUsername(user.getUsername()).isPresent()) {
            errors.put("user.username", "Username is already in use. Please choose another one.");
        }

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            errors.put("user.email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", errors));
        }

        // 3. Set relations
        user.setFreelancer(freelancer);
        freelancer.setUser(user);

        // 4. Persist
        Integer id = userService.saveUser(user, "ROLE_FREELANCER");
        freelancerService.saveFreelancer(freelancer);

        // 5. Send email
        String fullName = freelancer.getFirstName() + " " + freelancer.getLastName();
        emailService.sendSignupEmailToFreelancer(user.getEmail(), fullName);

        // 6. Response
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "Freelancer user created successfully");
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // ================== Helpers ==================

    private ResponseEntity<?> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    // ================== DTOs ==================

    public static class RegisterClientRequest {

        @NotNull
        @Valid
        private User user;

        @NotNull
        @Valid
        private Client client;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }
    }

    public static class RegisterFreelancerRequest {

        @NotNull
        @Valid
        private User user;

        @NotNull
        @Valid
        private Freelancer freelancer;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Freelancer getFreelancer() {
            return freelancer;
        }

        public void setFreelancer(Freelancer freelancer) {
            this.freelancer = freelancer;
        }
    }
}
