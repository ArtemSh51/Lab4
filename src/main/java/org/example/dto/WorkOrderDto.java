package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.entity.OrderStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderDto {
    private Long id;

    @NotNull
    private LocalDateTime deadline;

    private OrderStatus status;
    private String resultDescription;
    private CityReportDto report;
    private WorkCrewDto crew;

    @NotNull
    private Long reportId;

    @NotNull
    private Long crewId;
}