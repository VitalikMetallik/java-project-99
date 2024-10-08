package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtRequestPostProcessor token;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users").with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("hexlet@example.com");
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
        var user = userRepository.findByEmail(data.getEmail()).orElseThrow();
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getLastName()).isEqualTo(data.getLastName());
    }

    @Test
    public void testGetById() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", testUser.getId())
                        .with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(testUser.getEmail());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new UserUpdateDTO();
        data.setEmail("testmail@testmail.com");
        data.setFirstName("Test");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", testUser.getId())
                    .with(token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(data)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("testmail@testmail.com");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", testUser.getId()).with(token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

    @Test
    public void testDeleteAnotherUser() throws Exception {
        var anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(anotherUser);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", anotherUser.getId()).with(token))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        assertThat(userRepository.findById(anotherUser.getId())).isNotEmpty();
    }

    @Test
    public void testUpdateAnotherUser() throws Exception {
        var anotherUser = Instancio.of(modelGenerator.getUserModel()).create();
        var initialName = anotherUser.getFirstName();
        var initialEmail = anotherUser.getEmail();
        userRepository.save(anotherUser);

        var data = new UserUpdateDTO();
        data.setEmail("testmail@testmail.com");
        data.setFirstName("Test");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", anotherUser.getId())
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        var user = userRepository.findById(anotherUser.getId()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo(initialName);
        assertThat(user.getEmail()).isEqualTo(initialEmail);
    }
}
