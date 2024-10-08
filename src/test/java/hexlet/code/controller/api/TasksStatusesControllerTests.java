package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
public class TasksStatusesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private TaskStatus testTaskStatus;

    @BeforeAll
    public static void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @BeforeEach
    public void beforeEach() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        taskStatusRepository.save(testTaskStatus);
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/task_statuses").with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("to_publish");
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/task_statuses")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
        var taskStatus = taskStatusRepository.findBySlug(data.getSlug()).get();
        assertThat(taskStatus).isNotNull();
        assertThat(taskStatus.getName()).isEqualTo(data.getName());
        assertThat(taskStatus.getSlug()).isEqualTo(data.getSlug());
    }

    @Test
    public void testGetById() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/task_statuses/{id}", testTaskStatus.getId())
                        .with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(testTaskStatus.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new TaskStatusUpdateDTO();
        data.setName("testName");
        data.setSlug("test_slug");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/task_statuses/{id}", testTaskStatus.getId())
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var taskStatus = taskStatusRepository.findById(testTaskStatus.getId()).get();
        assertThat(taskStatus.getName()).isEqualTo("testName");
        assertThat(taskStatus.getSlug()).isEqualTo("test_slug");
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(taskStatusRepository.findById(testTaskStatus.getId())).isEmpty();
    }
}
