package gr.hua.dit.ds.dsproject.controllers;

import gr.hua.dit.ds.dsproject.dto.RegisterClientRequest;
import gr.hua.dit.ds.dsproject.dto.RegisterFreelancerRequest;
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

    @PostMapping("/register/client/works")
    public ResponseEntity<?> registerClient(
            @Valid @RequestBody RegisterClientRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        // 1. Business validation (unique username/email)
        Map<String, String> errors = new HashMap<>();


        if (userService.findByUsername(request.getUsername()).isPresent()) {
            errors.put("username", "Username is already in use. Please choose another one.");
        }

        if (userService.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", errors));
        }

        // 2. Φτιάξε User entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // 3. Φτιάξε Client entity
        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhone(request.getPhone());

        // 4. Set relations
        user.setClient(client);
        client.setUser(user);

        // 5. Save
        Integer id = userService.saveUser(user, "ROLE_CLIENT");
        clientService.saveClient(client);

        // 6. Email
        String fullName = client.getFirstName() + " " + client.getLastName();
        emailService.sendSignupEmailToClient(user.getEmail(), fullName);

        // 7. Response
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("message", "Client user created successfully");
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/register/freelancer/works")
    public ResponseEntity<?> registerFreelancer(
            @Valid @RequestBody RegisterFreelancerRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        Map<String, String> errors = new HashMap<>();

        if (userService.findByUsername(request.getUsername()).isPresent()) {
            errors.put("username", "Username is already in use. Please choose another one.");
        }

        if (userService.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", "Email is already in use. Please use another one.");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errors", errors));
        }

        // User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // Freelancer
        Freelancer freelancer = new Freelancer();
        freelancer.setFirstName(request.getFirstName());
        freelancer.setLastName(request.getLastName());
        freelancer.setPhone(request.getPhone());
        freelancer.setSkills(request.getSkills());
        // verified default = false από το entity :contentReference[oaicite:0]{index=0}

        user.setFreelancer(freelancer);
        freelancer.setUser(user);

        Integer id = userService.saveUser(user, "ROLE_FREELANCER");
        freelancerService.saveFreelancer(freelancer);

        String fullName = freelancer.getFirstName() + " " + freelancer.getLastName();
        emailService.sendSignupEmailToFreelancer(user.getEmail(), fullName);

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

}
