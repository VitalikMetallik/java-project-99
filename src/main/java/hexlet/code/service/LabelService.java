package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {

    @Autowired
    final LabelRepository labelRepository;
    @Autowired
    final LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        List<Label> labels = labelRepository.findAll();
        return labels.stream().map(labelMapper::map).toList();
    }

    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        var label = labelMapper.map(labelCreateDTO);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO findById(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow();
        return labelMapper.map(label);
    }

    public LabelDTO update(LabelUpdateDTO labelUpdateDTO, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow();
        labelMapper.update(labelUpdateDTO, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
