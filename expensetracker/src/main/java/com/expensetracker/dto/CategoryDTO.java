package com.expensetracker.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double budget;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "type of expense")
    private String type; // values: "fixed" or "variable"

}
