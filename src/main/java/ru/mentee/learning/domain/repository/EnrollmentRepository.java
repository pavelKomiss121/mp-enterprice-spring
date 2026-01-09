/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.mentee.learning.domain.model.Enrollment;

@Repository
public class EnrollmentRepository {
    private final Map<String, Enrollment> storage = new ConcurrentHashMap<>();

    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            enrollment.setId(UUID.randomUUID().toString());
        }
        storage.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    public Optional<Enrollment> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Enrollment> findByStudentId(String studentId) {
        return storage.values().stream()
                .filter(enrollment -> enrollment.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId) {
        return storage.values().stream()
                .filter(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId))
                .findFirst();
    }
}
