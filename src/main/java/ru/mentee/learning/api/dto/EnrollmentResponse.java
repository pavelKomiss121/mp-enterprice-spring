/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private String enrollmentId;
    private String status;
    private String message;
}
