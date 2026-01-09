/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime createdAt;
}
