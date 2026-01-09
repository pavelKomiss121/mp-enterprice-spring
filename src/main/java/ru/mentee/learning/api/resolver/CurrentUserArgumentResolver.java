/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.mentee.learning.api.annotation.CurrentUser;
import ru.mentee.learning.domain.model.Student;
import ru.mentee.learning.service.StudentService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final StudentService studentService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && Student.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        log.debug("Resolving @CurrentUser argument");

        // В реальном приложении здесь был бы SecurityContext / JWT token
        // Для демо возвращаем тестового пользователя
        String username = "demo_user"; // В реальности: из SecurityContextHolder
        Student student = studentService.findByUsername(username);

        log.debug("Resolved current user: {}", student.getUsername());
        return student;
    }
}
