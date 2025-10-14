package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.SerRequestDto;
import com.devmam.taraacademyapi.models.dto.response.SerResponseDto;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/services")
@PreAuthorize("permitAll()")
public class SerController extends BaseController<Ser, Integer, SerRequestDto, SerResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public SerController(SerService serService) {
        super(serService);
    }

    @Override
    protected SerResponseDto toResponseDto(Ser ser) {
        return SerResponseDto.toDTO(ser);
    }

    @Override
    protected Ser toEntity(SerRequestDto requestDto) {
        // Get current user from token
        String currentUserEmail = jwtService.getCurrentUserId();
        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userService.findByEmail(currentUserEmail).orElse(null);
        }

        Ser ser = new Ser();
        ser.setName(requestDto.getName());
        ser.setDescription(requestDto.getDescription());
        ser.setThumnail(requestDto.getThumnail());
        ser.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        ser.setIsDeleted(0);
        ser.setCreatedBy(currentUser);
        ser.setCreatedAt(Instant.now());
        ser.setUpdatedAt(Instant.now());

        return ser;
    }

    @Override
    protected Page<SerResponseDto> convertPage(Page<Ser> serPage) {
        return SerResponseDto.convertPage(serPage);
    }

    @Override
    protected String getEntityName() {
        return "Service";
    }
}
