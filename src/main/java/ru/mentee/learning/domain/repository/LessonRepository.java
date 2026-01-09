/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.mentee.learning.domain.model.Lesson;

@Repository
public class LessonRepository {
    private final Map<String, Lesson> storage = new ConcurrentHashMap<>();

    public Lesson save(Lesson lesson) {
        if (lesson.getId() == null) {
            lesson.setId(UUID.randomUUID().toString());
        }
        storage.put(lesson.getId(), lesson);
        return lesson;
    }

    public Optional<Lesson> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Lesson> findByCourseId(String courseId) {
        return storage.values().stream()
                .filter(lesson -> lesson.getCourseId().equals(courseId))
                .sorted(Comparator.comparing(Lesson::getOrderIndex))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
