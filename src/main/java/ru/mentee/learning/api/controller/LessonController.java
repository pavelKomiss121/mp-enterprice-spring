/* @MENTEE_POWER (C)2026 */
package ru.mentee.learning.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mentee.learning.api.dto.VideoUploadResponse;
import ru.mentee.learning.service.LessonService;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}")
@RequiredArgsConstructor
@Slf4j
public class LessonController {
    private final LessonService lessonService;

    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoUploadResponse> uploadVideo(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer duration) {
        log.info(
                "POST /api/v1/courses/{}/lessons/{}/video - file={}",
                courseId,
                lessonId,
                file.getOriginalFilename());

        VideoUploadResponse response =
                lessonService.uploadVideo(courseId, lessonId, file, title, duration);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
