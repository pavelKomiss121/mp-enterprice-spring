/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.learning.api.annotation.CurrentUser;
import ru.mentee.learning.api.dto.UserProgressDto;
import ru.mentee.learning.domain.model.Student;
import ru.mentee.learning.service.StudentService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final StudentService studentService;

    @GetMapping("/me/progress")
    public ResponseEntity<UserProgressDto> getCurrentUserProgress(@CurrentUser Student student) {
        log.info("GET /api/v1/users/me/progress - student={}", student.getId());

        UserProgressDto progress = studentService.getUserProgress(student.getId());

        return ResponseEntity.ok(progress);
    }
}
