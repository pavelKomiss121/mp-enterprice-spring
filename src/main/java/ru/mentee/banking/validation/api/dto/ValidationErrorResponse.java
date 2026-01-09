/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import java.time.Instant;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorDto> fieldErrors;
    private List<GlobalErrorDto> globalErrors;
}
