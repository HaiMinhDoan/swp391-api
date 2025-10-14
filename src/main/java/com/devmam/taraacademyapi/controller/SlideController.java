package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.SlideRequestDto;
import com.devmam.taraacademyapi.models.dto.response.SlideResponseDto;
import com.devmam.taraacademyapi.models.entities.Slide;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.SlideService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/slides")
@PreAuthorize("permitAll()")
public class SlideController extends BaseController<Slide, Integer, SlideRequestDto, SlideResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public SlideController(SlideService slideService) {
        super(slideService);
    }

    @Override
    protected SlideResponseDto toResponseDto(Slide slide) {
        return SlideResponseDto.toDTO(slide);
    }

    @Override
    protected Slide toEntity(SlideRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Slide slide = new Slide();
        slide.setTitle(requestDto.getTitle());
        slide.setDescription(requestDto.getDescription());
        slide.setImageUrl(requestDto.getImageUrl());
        slide.setLinkUrl(requestDto.getLinkUrl());
        slide.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        slide.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        slide.setIsDeleted(0);
        slide.setCreatedBy(currentUser);
        slide.setCreatedAt(Instant.now());
        slide.setUpdatedAt(Instant.now());

        return slide;
    }

    @Override
    protected Page<SlideResponseDto> convertPage(Page<Slide> slidePage) {
        return SlideResponseDto.convertPage(slidePage);
    }

    @Override
    protected String getEntityName() {
        return "Slide";
    }
}
