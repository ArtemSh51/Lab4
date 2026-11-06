package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkCrewDto {
    private Long id;

    @NotBlank
    private String name;

    private List<String> supportedObjectTypes;

    @NotNull
    @Min(1)
    private Integer activeTasksLimit;
}