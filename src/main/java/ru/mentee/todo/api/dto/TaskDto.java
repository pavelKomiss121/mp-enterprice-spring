/* @MENTEE_POWER (C)2026 */
package ru.mentee.todo.api.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime createdAt;
}
