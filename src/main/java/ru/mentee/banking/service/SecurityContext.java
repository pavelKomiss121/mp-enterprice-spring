/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SecurityContext {
    private Set<String> roles = ConcurrentHashMap.newKeySet();

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public String getCurrentUser() {
        return "user";
    }
}
