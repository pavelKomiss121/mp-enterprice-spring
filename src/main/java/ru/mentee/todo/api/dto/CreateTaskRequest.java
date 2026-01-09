/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {
    @NotBlank(message = "Название необходимо")
    private String title;
}
