package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.entity.ReportStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityReportDto {
    private Long id;

    @NotBlank
    @Size(max = 1000)
    private String description;

    private ReportStatus status;
    private LocalDateTime createdAt;
    private CityObjectDto cityObject;
    private UserDto author;

    @NotNull
    private Long cityObjectId;

    private Long authorId;
}