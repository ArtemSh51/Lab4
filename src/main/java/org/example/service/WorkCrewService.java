package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.WorkCrew;
import org.example.repository.WorkCrewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkCrewService {

    private final WorkCrewRepository repository;

    public List<WorkCrew> findAll() {
        return repository.findAll();
    }

    public WorkCrew findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkCrew not found with id: " + id));
    }

    public WorkCrew create(WorkCrew crew) {
        return repository.save(crew);
    }

    public WorkCrew update(Long id, WorkCrew details) {
        WorkCrew crew = findById(id);
        crew.setName(details.getName());
        crew.setSupportedObjectTypes(details.getSupportedObjectTypes());
        crew.setActiveTasksLimit(details.getActiveTasksLimit());
        return repository.save(crew);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}