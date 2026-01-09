/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDto {
    private String studentId;
    private String username;
    private Integer totalCoursesEnrolled;
    private Integer completedCourses;
    private List<CourseProgressDto> courseProgress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseProgressDto {
        private String courseId;
        private String courseTitle;
        private Integer progressPercentage;
        private String status;
    }
}
