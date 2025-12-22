package com.devmam.taraacademyapi.service;

import com.devmam.taraacademyapi.models.dto.response.ChatDto;
import com.devmam.taraacademyapi.models.dto.response.CourseAdviceDto;
import com.devmam.taraacademyapi.models.dto.response.CourseResponseDto;

import java.util.List;

public interface ClaudeAdviceService {
    CourseAdviceDto getAdviceForChat(ChatDto chat, List<CourseResponseDto> availableCourses);

}
