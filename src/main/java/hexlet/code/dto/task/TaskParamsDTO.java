package hexlet.code.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskParamsDTO {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;
}
