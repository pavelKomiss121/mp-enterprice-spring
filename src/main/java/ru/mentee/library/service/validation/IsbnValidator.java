/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.service.validation;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class IsbnValidator {

    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$");

    public boolean isValid(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }
        return ISBN_PATTERN.matcher(isbn).matches();
    }
}
