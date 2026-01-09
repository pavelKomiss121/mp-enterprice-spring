/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mentee.learning.api.dto.EnrollmentResponse;
import ru.mentee.learning.domain.model.Course;
import ru.mentee.learning.domain.model.CourseLevel;
import ru.mentee.learning.domain.model.Enrollment;
import ru.mentee.learning.domain.repository.CourseRepository;
import ru.mentee.learning.domain.repository.EnrollmentRepository;
import ru.mentee.learning.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<Course> findCourses(String category, CourseLevel level) {
        return courseRepository.findByCategoryAndLevel(category, level);
    }

    public Course findById(String id) {
        return courseRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }

    @Async
    public CompletableFuture<EnrollmentResponse> enrollAsync(String courseId, String studentId) {
        log.info("Starting async enrollment: course={}, student={}", courseId, studentId);

        // Имитация долгой операции
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Проверка существования курса
        Course course = findById(courseId);

        // Проверка дублирования
        var existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (existing.isPresent()) {
            return CompletableFuture.completedFuture(
                    EnrollmentResponse.builder()
                            .enrollmentId(existing.get().getId())
                            .status("ALREADY_ENROLLED")
                            .message("Student already enrolled in this course")
                            .build());
        }

        // Создание записи
        Enrollment enrollment =
                Enrollment.builder()
                        .id(UUID.randomUUID().toString())
                        .studentId(studentId)
                        .courseId(courseId)
                        .enrolledAt(LocalDateTime.now())
                        .status(Enrollment.EnrollmentStatus.ACTIVE)
                        .progressPercentage(0)
                        .build();

        enrollmentRepository.save(enrollment);

        log.info("Enrollment completed: {}", enrollment.getId());

        return CompletableFuture.completedFuture(
                EnrollmentResponse.builder()
                        .enrollmentId(enrollment.getId())
                        .status("SUCCESS")
                        .message("Successfully enrolled in course: " + course.getTitle())
                        .build());
    }
}
