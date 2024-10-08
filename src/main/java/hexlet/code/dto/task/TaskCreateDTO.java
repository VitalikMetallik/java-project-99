package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDTO {
    @NotNull
    @Size(min = 1)
    private String title;
    private Integer index;
    private String content;
    @NotNull
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds = new HashSet<>();
}
