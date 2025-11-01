package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/stream/{id}")
    public ResponseEntity<InputStreamResource> streamLessonVideoByLessonId(
            @RequestHeader(value = "Authorization",required = false) String authHeader,
            @PathVariable Integer id,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ){
        try {
            return videoService.streamLessonVideoByLessonId(authHeader, id, null, rangeHeader);
        } catch (Exception e) {
            throw new RuntimeException("Error streaming video: " + e.getMessage(), e);
        }
    }

}
