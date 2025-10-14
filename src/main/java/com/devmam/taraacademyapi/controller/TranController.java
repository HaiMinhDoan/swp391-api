package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.TranRequestDto;
import com.devmam.taraacademyapi.models.dto.response.TranResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.TranService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/transactions")
@PreAuthorize("permitAll()")
public class TranController extends BaseController<Tran, Integer, TranRequestDto, TranResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    public TranController(TranService tranService) {
        super(tranService);
    }

    @Override
    protected TranResponseDto toResponseDto(Tran tran) {
        return TranResponseDto.toDTO(tran);
    }

    @Override
    protected Tran toEntity(TranRequestDto requestDto) {
        // Get user and course entities
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userService.getOne(requestDto.getUserId()).orElse(null);
        }

        Course course = null;
        if (requestDto.getCourseId() != null) {
            course = courseService.getOne(requestDto.getCourseId()).orElse(null);
        }

        Tran tran = new Tran();
        tran.setUser(user);
        tran.setCourse(course);
        tran.setAmount(requestDto.getAmount());
        tran.setPaymentMethod(requestDto.getPaymentMethod());
        tran.setTransactionId(requestDto.getTransactionId());
        tran.setTransactionDate(requestDto.getTransactionDate() != null ? requestDto.getTransactionDate() : Instant.now());
        tran.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        tran.setIsDeleted(0);
        tran.setCreatedAt(Instant.now());
        tran.setUpdatedAt(Instant.now());

        return tran;
    }

    @Override
    protected Page<TranResponseDto> convertPage(Page<Tran> tranPage) {
        return TranResponseDto.convertPage(tranPage);
    }

    @Override
    protected String getEntityName() {
        return "Transaction";
    }
}
