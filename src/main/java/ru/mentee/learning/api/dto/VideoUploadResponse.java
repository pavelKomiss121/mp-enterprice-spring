/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadResponse {
    private String videoUrl;
    private String lessonId;
    private String message;
}
