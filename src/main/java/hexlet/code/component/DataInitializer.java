package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    final UserRepository userRepository;
    @Autowired
    final CustomUserDetailsService customUserDetailsService;
    @Autowired
    final TaskStatusService taskStatusService;
    @Autowired
    final LabelService labelService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        var userData = new User();
        userData.setEmail(email);
        userData.setPasswordDigest("qwerty");
        customUserDetailsService.createUser(userData);

        taskStatusService.create(new TaskStatusCreateDTO("Draft", "draft"));
        taskStatusService.create(new TaskStatusCreateDTO("To review", "to_review"));
        taskStatusService.create(new TaskStatusCreateDTO("To be fixed", "to_be_fixed"));
        taskStatusService.create(new TaskStatusCreateDTO("To publish", "to_publish"));
        taskStatusService.create(new TaskStatusCreateDTO("Published", "published"));

        labelService.create(new LabelCreateDTO("feature"));
        labelService.create(new LabelCreateDTO("bug"));
    }
}
