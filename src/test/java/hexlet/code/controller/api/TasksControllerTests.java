package hexlet.code.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    @BeforeAll
    public static void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @BeforeEach
    public void beforeEach() {
        testTask = Instancio.of(modelGenerator.getTaskModel())
                .create();
        taskRepository.save(testTask);
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks").with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(testTask.getDescription());
    }

    @Test
    @Transactional
    public void testGetAllFilter() throws Exception {
        var firstUser = Instancio.of(modelGenerator.getUserModel()).create();
        var secondUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.saveAll(List.of(firstUser, secondUser));

        var firstTask = Instancio.of(modelGenerator.getTaskModel()).create();
        firstTask.setName("first task");
        firstTask.setAssignee(firstUser);
        firstTask.setTaskStatus(taskStatusRepository.findBySlug("draft").get());
        firstTask.setLabels(Set.of(labelRepository.findByName("bug").get()));

        var secondTask = Instancio.of(modelGenerator.getTaskModel()).create();
        secondTask.setName("second task");
        secondTask.setAssignee(secondUser);
        secondTask.setTaskStatus(taskStatusRepository.findBySlug("to_review").get());
        secondTask.setLabels(Set.of(labelRepository.findByName("feature").get()));
        taskRepository.saveAll(List.of(firstTask, secondTask));

        var result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/tasks?titleCont=" + firstTask.getName()
                + "&assigneeId=" + firstTask.getAssignee().getId()
                + "&status=" + firstTask.getTaskStatus().getSlug()
                + "&labelId=" + firstTask.getLabels().stream().findFirst().get().getId()
        ).with(token)).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(firstTask.getName());
        assertThat(result.getResponse().getContentAsString()).contains(firstTask.getAssignee().getId().toString());
        assertThat(result.getResponse().getContentAsString()).contains(firstTask.getTaskStatus().getSlug());
        assertThat(result.getResponse().getContentAsString()).contains(firstTask.getLabels()
                .stream().findFirst().get().getId().toString());
    }

    @Test
    public void testCreate() throws Exception {
        var data = new TaskCreateDTO();
        data.setIndex(9000);
        data.setAssigneeId(1L);
        data.setTitle("Task 1");
        data.setContent("Description of task 1");
        data.setStatus("to_be_fixed");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/tasks")
                                .with(token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(data))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated());
        var task = taskRepository.findByName(data.getTitle()).get();
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(data.getTitle());
        assertThat(task.getIndex()).isEqualTo(data.getIndex());
        assertThat(task.getDescription()).isEqualTo(data.getContent());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(data.getStatus());
        assertThat(task.getAssignee().getId()).isEqualTo(data.getAssigneeId());
    }

    @Test
    public void testGetById() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        assertThat(result.getResponse().getContentAsString()).contains(testTask.getDescription());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new TaskUpdateDTO();
        data.setIndex(JsonNullable.of(9000));
        data.setAssigneeId(JsonNullable.of(1L));
        data.setTitle(JsonNullable.of("Task 1"));
        data.setContent(JsonNullable.of("Description of task 1"));
        data.setStatus(JsonNullable.of("to_be_fixed"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/tasks/{id}", testTask.getId())
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo("Task 1");
        assertThat(task.getIndex()).isEqualTo(9000);
        assertThat(task.getDescription()).isEqualTo("Description of task 1");
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("to_be_fixed");
        assertThat(task.getAssignee().getId()).isEqualTo(1);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", testTask.getId()).with(token))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}
