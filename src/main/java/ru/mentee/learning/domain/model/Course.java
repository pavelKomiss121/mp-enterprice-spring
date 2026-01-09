/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String id;
    private String title;
    private String description;
    private String category;
    private CourseLevel level;
    private Integer duration; // в часах
    private BigDecimal price;
    private Instructor instructor;
}
