/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentee.learning.api.dto.VideoUploadResponse;
import ru.mentee.learning.domain.model.Lesson;
import ru.mentee.learning.domain.repository.LessonRepository;
import ru.mentee.learning.exception.BadRequestException;
import ru.mentee.learning.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService {
    private final LessonRepository lessonRepository;

    public Lesson findById(String id) {
        return lessonRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
    }

    public VideoUploadResponse uploadVideo(
            String courseId, String lessonId, MultipartFile file, String title, Integer duration) {
        log.info(
                "Uploading video: lesson={}, file={}, size={}",
                lessonId,
                file.getOriginalFilename(),
                file.getSize());

        // Валидация
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            throw new BadRequestException("File too large (max 100MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BadRequestException("Invalid file type. Expected video/*");
        }

        // Находим урок
        Lesson lesson = findById(lessonId);
        if (!lesson.getCourseId().equals(courseId)) {
            throw new BadRequestException("Lesson does not belong to the specified course");
        }

        // Имитация загрузки (в реальности — S3/CDN)
        String videoUrl = "https://storage.example.com/videos/" + UUID.randomUUID() + ".mp4";
        lesson.setVideoUrl(videoUrl);
        if (duration != null) {
            lesson.setDuration(duration);
        }

        lessonRepository.save(lesson);

        log.info("Video uploaded successfully: {}", videoUrl);

        return VideoUploadResponse.builder()
                .videoUrl(videoUrl)
                .lessonId(lessonId)
                .message("Video uploaded successfully")
                .build();
    }
}
