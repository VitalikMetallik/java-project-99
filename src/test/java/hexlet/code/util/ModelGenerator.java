package hexlet.code.util;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;

    @PostConstruct
    private void init() {
        var faker = new Faker();

        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getName), () -> faker.text().text(1, 10))
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .supply(Select.field(Task::getName), () -> faker.text().text(1, 100))
                .supply(Select.field(Task::getDescription), () -> faker.text().text(255))
                .supply(Select.field(Task::getTaskStatus), () -> Instancio.of(taskStatusModel).create())
                .supply(Select.field(Task::getAssignee), () -> Instancio.of(userModel).create())
                .supply(Select.field(Task::getLabels), () -> Set.of(Instancio.of(labelModel).create()))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
                .toModel();
    }
}
