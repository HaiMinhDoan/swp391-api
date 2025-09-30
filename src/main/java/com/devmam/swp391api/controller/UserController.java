package com.devmam.swp391api.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.dto.response.UserResponseDTO;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("permitAll()")
public class UserController {

    @Autowired
    private UserService userService;

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
