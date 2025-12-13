package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.CareerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.CareerResponseDto;
import com.devmam.taraacademyapi.models.entities.Career;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.CareerService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/careers")
@PreAuthorize("permitAll()")
public class CareerController extends BaseController<Career, Integer, CareerRequestDto, CareerResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public CareerController(CareerService careerService) {
        super(careerService);
    }

    @Override
    protected CareerResponseDto toResponseDto(Career career) {
        return CareerResponseDto.toDTO(career);
    }

    @Override
    protected Career toEntity(CareerRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Career career = new Career();
        career.setTitle(requestDto.getTitle());
        career.setSummary(requestDto.getSummary());
        career.setDescription(requestDto.getDescription());
        career.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        career.setIsDeleted(0);
        career.setCreatedBy(currentUser);
        career.setCreatedAt(Instant.now());
        career.setUpdatedAt(Instant.now());

        return career;
    }

    @Override
    protected Page<CareerResponseDto> convertPage(Page<Career> careerPage) {
        return CareerResponseDto.convertPage(careerPage);
    }

    @Override
    protected String getEntityName() {
        return "Career";
    }
}
