package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.ContactRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ContactResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Contact;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.service.ExcelExportService;
import com.devmam.taraacademyapi.service.impl.entities.ContactService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/contacts")
@PreAuthorize("permitAll()")
public class ContactController extends BaseController<Contact, Integer, ContactRequestDto, ContactResponseDto> {

    @Autowired
    private SerService serService;

    @Autowired
    private ExcelExportService excelExportService;

    public ContactController(ContactService contactService) {
        super(contactService);
    }

    @Override
    protected ContactResponseDto toResponseDto(Contact contact) {
        return ContactResponseDto.toDTO(contact);
    }

    @Override
    protected Contact toEntity(ContactRequestDto requestDto) {
        // Get service entity
        Ser service = null;
        if (requestDto.getServicesId() != null) {
            service = serService.getOne(requestDto.getServicesId()).orElse(null);
        }

        Contact contact = new Contact();
        contact.setFullName(requestDto.getFullName());
        contact.setEmail(requestDto.getEmail());
        contact.setPhone(requestDto.getPhone());
        contact.setCompany(requestDto.getCompany());
        contact.setPersonalRole(requestDto.getPersonalRole());
        contact.setSubject(requestDto.getSubject());
        contact.setMessage(requestDto.getMessage());
        contact.setServices(service);
        contact.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        contact.setIsDeleted(0);
        contact.setCreatedAt(Instant.now());
        contact.setUpdatedAt(Instant.now());

        return contact;
    }

    @Override
    protected Page<ContactResponseDto> convertPage(Page<Contact> contactPage) {
        return ContactResponseDto.convertPage(contactPage);
    }

    @Override
    protected String getEntityName() {
        return "Contact";
    }

    /**
     * Create new Contact - Allow anonymous users (no authentication required)
     */
    @Override
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseData<ContactResponseDto>> create(
            @Valid @RequestBody ContactRequestDto requestDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // No authentication required - allow anonymous users to create contacts
            Contact entity = toEntity(requestDto);
            Contact createdEntity = baseService.create(entity);
            ContactResponseDto responseDto = toResponseDto(createdEntity);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<ContactResponseDto>builder()
                            .status(201)
                            .message("Tạo " + getEntityName() + " thành công")
                            .error(null)
                            .data(responseDto)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<ContactResponseDto>builder()
                            .status(500)
                            .message("Tạo " + getEntityName() + " thất bại")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Export Contact List to Excel with filter and sort
     * POST /api/v1/contacts/export/excel
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
    public ResponseEntity<byte[]> exportContactsToExcel(
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

            // Get all filtered and sorted contacts by iterating through pages
            List<Contact> contacts = new java.util.ArrayList<>();
            int currentPage = filterRequest.getPage() != null ? filterRequest.getPage() : 0;
            int pageSize = filterRequest.getSize();

            while (true) {
                BaseFilterRequest pageRequest = BaseFilterRequest.builder()
                        .filters(filterRequest.getFilters())
                        .sorts(filterRequest.getSorts())
                        .page(currentPage)
                        .size(pageSize)
                        .build();

                Page<Contact> contactPage = baseService.filter(pageRequest);
                contacts.addAll(contactPage.getContent());

                // If this is the last page, break
                if (contactPage.isLast() || contactPage.getContent().isEmpty()) {
                    break;
                }

                currentPage++;
            }

            // Export to Excel
            byte[] excelBytes = excelExportService.exportContactsToExcel(contacts);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "contacts_export.xlsx");
            headers.setContentLength(excelBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
