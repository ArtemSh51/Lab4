package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityObjectDto {
    private Long id;

    @NotBlank
    private String address;

    @NotBlank
    private String type;

    private String condition;
}