package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private long id;
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String passwordHash;
}
