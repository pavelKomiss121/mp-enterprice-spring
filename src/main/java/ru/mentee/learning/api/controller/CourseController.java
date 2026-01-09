/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.controller;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.learning.api.annotation.CurrentUser;
import ru.mentee.learning.api.dto.*;
import ru.mentee.learning.domain.model.Course;
import ru.mentee.learning.domain.model.CourseLevel;
import ru.mentee.learning.domain.model.Student;
import ru.mentee.learning.service.CourseService;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @GetMapping(
            produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                "text/csv"
            })
    public ResponseEntity<CourseListResponse> getCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseLevel level) {
        log.info("GET /api/v1/courses - category={}, level={}", category, level);

        List<Course> courses = courseService.findCourses(category, level);

        List<CourseDto> courseDtos =
                courses.stream().map(this::mapToDto).collect(Collectors.toList());

        CourseListResponse response =
                CourseListResponse.builder()
                        .courses(courseDtos)
                        .totalCount(courseDtos.size())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/enroll")
    public CompletableFuture<ResponseEntity<EnrollmentResponse>> enrollInCourse(
            @PathVariable String courseId, @CurrentUser Student student) {
        log.info("POST /api/v1/courses/{}/enroll - student={}", courseId, student.getId());

        return courseService
                .enrollAsync(courseId, student.getId())
                .thenApply(
                        result -> {
                            URI location =
                                    URI.create("/api/v1/enrollments/" + result.getEnrollmentId());
                            return ResponseEntity.status(HttpStatus.ACCEPTED)
                                    .header("Location", location.toString())
                                    .body(result);
                        });
    }

    private CourseDto mapToDto(Course course) {
        InstructorDto instructorDto = null;
        if (course.getInstructor() != null) {
            instructorDto =
                    InstructorDto.builder()
                            .id(course.getInstructor().getId())
                            .name(course.getInstructor().getName())
                            .email(course.getInstructor().getEmail())
                            .bio(course.getInstructor().getBio())
                            .build();
        }

        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .level(course.getLevel())
                .duration(course.getDuration())
                .price(course.getPrice())
                .instructor(instructorDto)
                .build();
    }
}
