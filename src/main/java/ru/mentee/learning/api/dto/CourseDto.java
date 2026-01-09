/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.learning.domain.model.CourseLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private String id;
    private String title;
    private String description;
    private String category;
    private CourseLevel level;
    private Integer duration;
    private BigDecimal price;
    private InstructorDto instructor;
}
