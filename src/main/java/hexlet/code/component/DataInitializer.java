package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final CustomUserDetailsService userService;
    @Autowired
    private final TaskStatusRepository taskStatusRepository;
    @Autowired
    private final LabelRepository labelRepository;

    private final Map<String, String> taskStatuses = Map.of(
            "Draft", "draft",
            "ToReview", "to_review",
            "ToBeFixed", "to_be_fixed",
            "ToPublish", "to_publish",
            "Published", "published");

    private final List<String> labels = List.of(
            "bug",
            "feature");

    @Override
    public void run(ApplicationArguments args) {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        userData.setPasswordDigest("qwerty");
        userService.createUser(userData);

        for (Map.Entry<String, String> status : taskStatuses.entrySet()) {
            var taskStatus = new TaskStatus();
            taskStatus.setName(status.getKey());
            taskStatus.setSlug(status.getValue());
            taskStatusRepository.save(taskStatus);
        }

        for (String label : labels) {
            var taskLabel = new Label();
            taskLabel.setName(label);
            labelRepository.save(taskLabel);
        }
    }

}
