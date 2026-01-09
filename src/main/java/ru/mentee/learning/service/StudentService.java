/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.learning.api.dto.UserProgressDto;
import ru.mentee.learning.domain.model.Course;
import ru.mentee.learning.domain.model.Enrollment;
import ru.mentee.learning.domain.model.Student;
import ru.mentee.learning.domain.repository.CourseRepository;
import ru.mentee.learning.domain.repository.EnrollmentRepository;
import ru.mentee.learning.domain.repository.StudentRepository;
import ru.mentee.learning.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public Student findByUsername(String username) {
        return studentRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + username));
    }

    public UserProgressDto getUserProgress(String studentId) {
        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        long completedCount =
                enrollments.stream()
                        .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.COMPLETED)
                        .count();

        List<UserProgressDto.CourseProgressDto> courseProgress =
                enrollments.stream()
                        .map(
                                e -> {
                                    Course course =
                                            courseRepository.findById(e.getCourseId()).orElse(null);
                                    return UserProgressDto.CourseProgressDto.builder()
                                            .courseId(e.getCourseId())
                                            .courseTitle(
                                                    course != null ? course.getTitle() : "Unknown")
                                            .progressPercentage(e.getProgressPercentage())
                                            .status(e.getStatus().name())
                                            .build();
                                })
                        .collect(Collectors.toList());

        return UserProgressDto.builder()
                .studentId(studentId)
                .username(student.getUsername())
                .totalCoursesEnrolled(enrollments.size())
                .completedCourses((int) completedCount)
                .courseProgress(courseProgress)
                .build();
    }
}
