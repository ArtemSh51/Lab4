package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.WorkOrderDto;
import org.example.entity.WorkOrder;
import org.example.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DISPATCHER','ADMIN')")
public class WorkOrderController {

    private final WorkOrderService service;

    @GetMapping
    public List<WorkOrder> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public WorkOrder getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrder create(@Valid @RequestBody WorkOrderDto dto) {
        return service.create(dto);
    }

    @PatchMapping("/{id}")
    public WorkOrder update(@PathVariable Long id, @RequestBody WorkOrderDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/complete")
    public WorkOrder complete(@PathVariable Long id) {
        return service.completeOrder(id);
    }

    @PatchMapping("/{id}/cancel")
    public WorkOrder cancel(@PathVariable Long id) {
        return service.cancelOrder(id);
    }

    @PatchMapping("/{id}/reassign")
    public WorkOrder reassign(@PathVariable Long id, @RequestParam Long newCrewId) {
        return service.reassignCrew(id, newCrewId);
    }
}