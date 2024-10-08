package hexlet.code.dto.user;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCreateDTO {
    private String firstName;
    private String lastName;

    @Email
    @Column(unique = true)
    @NotBlank
    private String email;

    @NotNull
    @Size(min = 3)
    private String password;
}
