package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDTO;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.eclipse.angus.mail.imap.protocol.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("permitAll()")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<UserResponseDTO>> getOne(@PathVariable UUID id) {
        Optional<User> user = userService.getOne(id);
        if (user.isEmpty()) {
            throw new RuntimeException("user not found");
        }
        return ResponseEntity.<ResponseData<UserResponseDTO>>ok(
                ResponseData.<UserResponseDTO>builder()
                        .status(200)
                        .message("user found")
                        .error(null)
                        .data(UserResponseDTO.toDTO(user.get()))
                        .build()
        );
    }

    @PostMapping("/filter")
    public ResponseEntity<ResponseData<Page<UserResponseDTO>>> filter(@RequestBody BaseFilterRequest filter) {
        Page<User> result = userService.filter(filter);

        return ResponseEntity.ok(ResponseData.<Page<UserResponseDTO>>builder()
                .status(200)
                .data(UserResponseDTO.convertPage(result))
                .message("Success")
                .build());
    }
}
