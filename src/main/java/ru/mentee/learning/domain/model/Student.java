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
public class Student {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime registeredAt;
}
