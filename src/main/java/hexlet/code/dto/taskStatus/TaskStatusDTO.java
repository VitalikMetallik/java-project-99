package hexlet.code.dto.taskStatus;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TaskStatusDTO {
    private Long id;

    private String name;

    private String slug;

    private LocalDate createdAt;
}
