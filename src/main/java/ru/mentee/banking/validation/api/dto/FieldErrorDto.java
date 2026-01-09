/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDto {
    private String field;
    private String code;
    private String message;
    private Object rejectedValue;
}
