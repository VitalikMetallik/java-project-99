package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "mapAssignee")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "mapTaskStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "mapLabels")
    public abstract Task map(TaskCreateDTO taskCreateDTO);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "mapTaskLabelIds")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "mapAssignee")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "mapTaskStatus")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "mapLabels")
    public abstract void update(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);

    @Named("mapTaskStatus")
    public TaskStatus mapTaskStatus(String status) {
        return taskStatusRepository.findBySlug(status)
                .orElseThrow();
    }

    @Named("mapAssignee")
    public User mapAssignee(Long id) {
        return id == null ? null : userRepository.findById(id)
                .orElseThrow();
    }

    @Named("mapLabels")
    public Set<Label> mapLabels(Set<Long> taskLabelIds) {
        System.out.println("labelIds: " + taskLabelIds);
        return taskLabelIds == null ? new HashSet<>() : labelRepository.findAllByIdIn(taskLabelIds);
    }

    @Named("mapTaskLabelIds")
    public Set<Long> mapLabelIds(Set<Label> labels) {
        System.out.println("labels: " + labels);
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }
}
