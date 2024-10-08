package hexlet.code.dto.label;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class LabelDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
