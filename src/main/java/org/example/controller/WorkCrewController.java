package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.WorkCrew;
import org.example.service.WorkCrewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-crews")
@RequiredArgsConstructor
public class WorkCrewController {

    private final WorkCrewService service;

    @GetMapping
    public List<WorkCrew> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public WorkCrew getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public WorkCrew create(@Valid @RequestBody WorkCrew crew) {
        return service.create(crew);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public WorkCrew update(@PathVariable Long id, @RequestBody WorkCrew crew) {
        return service.update(id, crew);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}