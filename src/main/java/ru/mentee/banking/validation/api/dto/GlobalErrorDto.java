/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalErrorDto {
    private String objectName;
    private String code;
    private String message;
}
