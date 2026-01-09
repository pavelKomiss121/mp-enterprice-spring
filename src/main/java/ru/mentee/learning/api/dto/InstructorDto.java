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
public class InstructorDto {
    private String id;
    private String name;
    private String email;
    private String bio;
}
