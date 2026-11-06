package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CityReportDto;
import org.example.entity.CityReport;
import org.example.service.CityReportService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city-reports")
@RequiredArgsConstructor
public class CityReportController {

    private final CityReportService service;

    @GetMapping
    public List<CityReport> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CityReport getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER','DISPATCHER','ADMIN')")
    public CityReport create(@Valid @RequestBody CityReportDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
    public CityReport reject(@PathVariable Long id) {
        return service.rejectReport(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','DISPATCHER','ADMIN')")
    public CityReport update(@PathVariable Long id, @Valid @RequestBody CityReportDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}