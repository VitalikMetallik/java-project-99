package hexlet.code.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "task_statuses")
@EntityListeners(AuditingEntityListener.class)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskStatus implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @Size(min = 1)
    @Column(unique = true)
    @ToString.Include
    private String name;

    @Size(min = 1)
    @Column(unique = true)
    @ToString.Include
    private String slug;

    @CreatedDate
    private LocalDate createdAt;
}
