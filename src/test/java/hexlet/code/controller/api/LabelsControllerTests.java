package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Label testLabel;

    @BeforeAll
    public static void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @BeforeEach
    public void beforeEach() {
        testLabel = Instancio.of(modelGenerator.getLabelModel())
                .create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/labels").with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("feature");
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/labels")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
        var label = labelRepository.findByName(data.getName()).get();
        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testGetById() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/labels/{id}", testLabel.getId())
                        .with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(testLabel.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new LabelUpdateDTO();
        data.setName("testName");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/labels/{id}", testLabel.getId())
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var label = labelRepository.findById(testLabel.getId()).get();
        assertThat(label.getName()).isEqualTo("testName");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/labels/{id}", testLabel.getId()).with(token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }

}
