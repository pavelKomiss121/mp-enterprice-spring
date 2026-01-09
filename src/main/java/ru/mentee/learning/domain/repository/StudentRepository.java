/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import ru.mentee.learning.domain.model.Student;

@Repository
public class StudentRepository {
    private final Map<String, Student> storage = new ConcurrentHashMap<>();

    public StudentRepository() {
        // Тестовый пользователь для @CurrentUser
        save(
                Student.builder()
                        .id("current-user-123")
                        .username("demo_user")
                        .email("demo@example.com")
                        .fullName("Demo User")
                        .registeredAt(LocalDateTime.now())
                        .build());
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(UUID.randomUUID().toString());
        }
        storage.put(student.getId(), student);
        return student;
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Student> findByUsername(String username) {
        return storage.values().stream().filter(s -> s.getUsername().equals(username)).findFirst();
    }
}
