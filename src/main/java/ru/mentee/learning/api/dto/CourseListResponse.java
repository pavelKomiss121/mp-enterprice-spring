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
public class CourseListResponse {
    private List<CourseDto> courses;
    private Integer totalCount;
}
