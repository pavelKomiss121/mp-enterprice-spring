/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private String id;
    private String studentId;
    private String courseId;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
    private Integer progressPercentage;

    public enum EnrollmentStatus {
        PENDING,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}
