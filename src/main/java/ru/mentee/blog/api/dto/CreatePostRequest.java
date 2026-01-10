/* @MENTEE_POWER (C)2026 */
package ru.mentee.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title too long")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String author;
}
