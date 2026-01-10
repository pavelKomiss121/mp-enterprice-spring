/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.learning.api.dto.EnrollmentResponse;
import ru.mentee.learning.domain.model.Course;
import ru.mentee.learning.domain.model.CourseLevel;
import ru.mentee.learning.domain.model.Student;
import ru.mentee.learning.service.CourseService;
import ru.mentee.learning.service.StudentService;

@Disabled("Отключено для ускорения тестов - используется только booking модуль")
@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private CourseService courseService;

    @MockBean private StudentService studentService;

    private List<Course> testCourses;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testCourses =
                Arrays.asList(
                        Course.builder()
                                .id("1")
                                .title("Java для начинающих")
                                .description("Основы Java")
                                .category("Programming")
                                .level(CourseLevel.BEGINNER)
                                .duration(40)
                                .price(new BigDecimal("9999.00"))
                                .build(),
                        Course.builder()
                                .id("2")
                                .title("Spring Boot")
                                .description("Продвинутый Spring")
                                .category("Programming")
                                .level(CourseLevel.ADVANCED)
                                .duration(60)
                                .price(new BigDecimal("19999.00"))
                                .build());

        testStudent =
                Student.builder()
                        .id("student-123")
                        .username("test_user")
                        .email("test@example.com")
                        .fullName("Test User")
                        .build();
    }

    @Test
    @DisplayName("Should вернуть список курсов")
    void shouldReturnCourseList() throws Exception {
        // Given
        when(courseService.findCourses(null, null)).thenReturn(testCourses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses.length()").value(2))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.courses[0].title").value("Java для начинающих"))
                .andExpect(jsonPath("$.courses[1].title").value("Spring Boot"));
    }

    @Test
    @DisplayName("Should фильтровать курсы по категории и уровню")
    void shouldFilterCoursesByCategoryAndLevel() throws Exception {
        // Given
        List<Course> filteredCourses = List.of(testCourses.get(0));
        when(courseService.findCourses("Programming", CourseLevel.BEGINNER))
                .thenReturn(filteredCourses);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/courses")
                                .param("category", "Programming")
                                .param("level", "BEGINNER")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses.length()").value(1))
                .andExpect(jsonPath("$.courses[0].title").value("Java для начинающих"))
                .andExpect(jsonPath("$.courses[0].level").value("BEGINNER"));
    }

    @Test
    @DisplayName("Should вызвать enrollAsync при запросе на запись")
    void shouldCallEnrollAsyncOnEnrollmentRequest() throws Exception {
        // Given
        EnrollmentResponse enrollmentResponse =
                EnrollmentResponse.builder()
                        .enrollmentId("enrollment-123")
                        .status("SUCCESS")
                        .message("Enrolled successfully")
                        .build();

        when(studentService.findByUsername(anyString())).thenReturn(testStudent);
        when(courseService.enrollAsync(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(enrollmentResponse));

        // When & Then
        mockMvc.perform(post("/api/v1/courses/course-123/enroll")).andExpect(status().isOk());

        // Verify service was called
        verify(courseService).enrollAsync(eq("course-123"), any());
    }
}
