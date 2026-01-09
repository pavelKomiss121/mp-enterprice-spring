/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.domain.repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.mentee.learning.domain.model.Course;
import ru.mentee.learning.domain.model.CourseLevel;

@Repository
public class CourseRepository {
    private final Map<String, Course> storage = new ConcurrentHashMap<>();

    public CourseRepository() {
        // Инициализируем тестовые данные
        initTestData();
    }

    private void initTestData() {
        save(
                Course.builder()
                        .id("1")
                        .title("Java для начинающих")
                        .description("Изучите основы Java с нуля")
                        .category("Programming")
                        .level(CourseLevel.BEGINNER)
                        .duration(40)
                        .price(new BigDecimal("9999.00"))
                        .instructor(null)
                        .build());

        save(
                Course.builder()
                        .id("2")
                        .title("Spring Boot Mastery")
                        .description("Продвинутый курс по Spring Boot")
                        .category("Programming")
                        .level(CourseLevel.ADVANCED)
                        .duration(60)
                        .price(new BigDecimal("19999.00"))
                        .instructor(null)
                        .build());

        save(
                Course.builder()
                        .id("3")
                        .title("Web Design Fundamentals")
                        .description("Основы веб-дизайна")
                        .category("Design")
                        .level(CourseLevel.INTERMEDIATE)
                        .duration(30)
                        .price(new BigDecimal("7999.00"))
                        .instructor(null)
                        .build());
    }

    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(UUID.randomUUID().toString());
        }
        storage.put(course.getId(), course);
        return course;
    }

    public Optional<Course> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Course> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Course> findByCategoryAndLevel(String category, CourseLevel level) {
        return storage.values().stream()
                .filter(
                        course ->
                                (category == null
                                        || course.getCategory().equalsIgnoreCase(category)))
                .filter(course -> (level == null || course.getLevel() == level))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        storage.remove(id);
    }
}
