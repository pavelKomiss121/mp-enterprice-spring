/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private Integer orderIndex;
    private String videoUrl;
    private Integer duration; // в секундах
}
