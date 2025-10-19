package com.devmam.taraacademyapi.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface VideoService {
    ResponseEntity<InputStreamResource> streamLessonVideoByLessonId(
            String authHeader,
            Integer id,
            String quality,
            String rangeHeader) throws Exception;
}
