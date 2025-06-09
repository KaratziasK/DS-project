package gr.hua.dit.ds.dsproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import gr.hua.dit.ds.dsproject.services.EmailService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import gr.hua.dit.ds.dsproject.repositories.UserRepository;
import gr.hua.dit.ds.dsproject.repositories.ClientRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DsprojectApplicationTests {

	@MockBean
    private EmailService emailService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClientRepository clientRepository;


	@Test
	void contextLoads() {
	}

	@BeforeEach
	void cleanUp() {
		clientRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void testRegisterClientForm() throws Exception {
		mockMvc.perform(post("/auth/saveUserClient")
        .param("username", "myuser")
        .param("email", "myuser@example.com")
        .param("password", "secret123")
        .param("firstName", "Νίκος")
        .param("lastName", "Παπαδόπουλος")
        .param("phone", "+306971234567")
        .with(csrf()))
    .andExpect(status().isOk())
    .andExpect(view().name("index"))
    .andExpect(model().attributeExists("msg"));

	}
	@Test
	void testRegisterClientForm_DuplicateUsername() throws Exception {
		mockMvc.perform(post("/auth/saveUserClient")
				.param("username", "myuser")
				.param("email", "myuser@example.com")
				.param("password", "secret123")
				.param("firstName", "Νίκος")
				.param("lastName", "Παπαδόπουλος")
				.param("phone", "+306971234567")
				.with(csrf()));

		mockMvc.perform(post("/auth/saveUserClient")
				.param("username", "myuser")
				.param("email", "other@example.com")
				.param("password", "secret123")
				.param("firstName", "Κώστας")
				.param("lastName", "Παπαδόπουλος")
				.param("phone", "+306971234568")
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("auth/register_client"))
			.andExpect(model().attributeExists("msg"));
	}

}
