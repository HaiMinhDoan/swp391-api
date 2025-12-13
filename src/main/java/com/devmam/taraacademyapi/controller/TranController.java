package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.TranRequestDto;
import com.devmam.taraacademyapi.models.dto.response.TranResponseDto;
import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.service.ExcelExportService;
import com.devmam.taraacademyapi.service.impl.entities.CourseService;
import com.devmam.taraacademyapi.service.impl.entities.TranService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@PreAuthorize("permitAll()")
public class TranController extends BaseController<Tran, Integer, TranRequestDto, TranResponseDto> {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ExcelExportService excelExportService;

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
//        tran.setCourse(course);
        tran.setAmount(requestDto.getAmount());
//        tran.setPaymentMethod(requestDto.getPaymentMethod());
//        tran.setTransactionId(requestDto.getTransactionId());
//        tran.setTransactionDate(requestDto.getTransactionDate() != null ? requestDto.getTransactionDate() : Instant.now());
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

    /**
     * Export Transactions to Excel with filter and sort
     * POST /api/v1/transactions/export/excel
     *
     * Request Body (optional):
     * {
     *   "filters": [...],      // Filter criteria (same as /filter endpoint)
     *   "sorts": [...],        // Sort criteria (same as /filter endpoint)
     *   "page": 0,             // Starting page (default: 0, will fetch all pages)
     *   "size": 1000           // Page size for pagination (default: 1000)
     * }
     *
     * Note: This endpoint will automatically fetch all pages of filtered results
     * to ensure complete export, regardless of the page/size parameters.
     */
    @PostMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportTransactionsToExcel(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) BaseFilterRequest filterRequest) {
        try {
            // Validate JWT and admin role
            validateAdminUser(authHeader);

            // If no filter request provided, create default one
            if (filterRequest == null) {
                filterRequest = BaseFilterRequest.builder()
                        .page(0)
                        .size(1000) // Reasonable page size
                        .build();
            } else {
                // Set reasonable page size if not provided
                if (filterRequest.getSize() == null) {
                    filterRequest.setSize(1000);
                }
            }

            // Get all filtered and sorted transactions by iterating through pages
            List<Tran> transactions = new java.util.ArrayList<>();
            int currentPage = filterRequest.getPage() != null ? filterRequest.getPage() : 0;
            int pageSize = filterRequest.getSize();

            while (true) {
                BaseFilterRequest pageRequest = BaseFilterRequest.builder()
                        .filters(filterRequest.getFilters())
                        .sorts(filterRequest.getSorts())
                        .page(currentPage)
                        .size(pageSize)
                        .build();

                Page<Tran> transactionPage = baseService.filter(pageRequest);
                transactions.addAll(transactionPage.getContent());

                // If this is the last page, break
                if (transactionPage.isLast() || transactionPage.getContent().isEmpty()) {
                    break;
                }

                currentPage++;
            }

            // Export to Excel
            byte[] excelBytes = excelExportService.exportTransactionsToExcel(transactions);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "transactions_export.xlsx");
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
