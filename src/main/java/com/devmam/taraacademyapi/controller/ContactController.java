package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.ContactRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ContactResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Contact;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.service.impl.entities.ContactService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contacts")
@PreAuthorize("permitAll()")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private SerService serService;

    /**
     * Create a new contact
     */
    @PostMapping
    public ResponseEntity<ResponseData<ContactResponseDto>> createContact(@Valid @RequestBody ContactRequestDto request) {
        // Get services if servicesId is provided
        Ser services = null;
        if (request.getServicesId() != null) {
            services = serService.getOne(request.getServicesId()).orElse(null);
        }

        Contact contact = Contact.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .company(request.getCompany())
                .personalRole(request.getPersonalRole())
                .subject(request.getSubject())
                .message(request.getMessage())
                .services(services)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .isDeleted(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Contact createdContact = contactService.create(contact);
        ContactResponseDto responseDto = ContactResponseDto.toDTO(createdContact);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<ContactResponseDto>builder()
                        .status(201)
                        .message("Contact created successfully")
                        .error(null)
                        .data(responseDto)
                        .build());
    }

    /**
     * Get contact by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ContactResponseDto>> getContactById(@PathVariable Integer id) {
        Optional<Contact> contact = contactService.getOne(id);
        if (contact.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<ContactResponseDto>builder()
                            .status(404)
                            .message("Contact not found")
                            .error("Contact with id " + id + " not found")
                            .data(null)
                            .build());
        }

        ContactResponseDto responseDto = ContactResponseDto.toDTO(contact.get());
        return ResponseEntity.ok(ResponseData.<ContactResponseDto>builder()
                .status(200)
                .message("Contact found")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Update contact by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ContactResponseDto>> updateContact(
            @PathVariable Integer id, 
            @Valid @RequestBody ContactRequestDto request) {
        
        Optional<Contact> existingContact = contactService.getOne(id);
        if (existingContact.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<ContactResponseDto>builder()
                            .status(404)
                            .message("Contact not found")
                            .error("Contact with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Contact contact = existingContact.get();
        contact.setFullName(request.getFullName());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
        contact.setCompany(request.getCompany());
        contact.setPersonalRole(request.getPersonalRole());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());
        
        // Update services if servicesId is provided
        if (request.getServicesId() != null) {
            Ser services = serService.getOne(request.getServicesId()).orElse(null);
            contact.setServices(services);
        }
        
        contact.setStatus(request.getStatus() != null ? request.getStatus() : contact.getStatus());
        contact.setUpdatedAt(Instant.now());

        Contact updatedContact = contactService.update(id, contact);
        ContactResponseDto responseDto = ContactResponseDto.toDTO(updatedContact);

        return ResponseEntity.ok(ResponseData.<ContactResponseDto>builder()
                .status(200)
                .message("Contact updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Delete contact by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteContact(@PathVariable Integer id) {
        Optional<Contact> contact = contactService.getOne(id);
        if (contact.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Void>builder()
                            .status(404)
                            .message("Contact not found")
                            .error("Contact with id " + id + " not found")
                            .data(null)
                            .build());
        }

        contactService.delete(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(200)
                .message("Contact deleted successfully")
                .error(null)
                .data(null)
                .build());
    }

    /**
     * Change contact status
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ContactResponseDto>> changeContactStatus(
            @PathVariable Integer id, 
            @RequestParam Integer status) {
        
        Optional<Contact> contact = contactService.getOne(id);
        if (contact.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<ContactResponseDto>builder()
                            .status(404)
                            .message("Contact not found")
                            .error("Contact with id " + id + " not found")
                            .data(null)
                            .build());
        }

        Contact updatedContact = contactService.changeStatus(id, status);
        ContactResponseDto responseDto = ContactResponseDto.toDTO(updatedContact);

        return ResponseEntity.ok(ResponseData.<ContactResponseDto>builder()
                .status(200)
                .message("Contact status updated successfully")
                .error(null)
                .data(responseDto)
                .build());
    }

    /**
     * Get all contacts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<ContactResponseDto>>> getAllContacts() {
        List<Contact> contacts = contactService.getAll();
        List<ContactResponseDto> responseDtos = contacts.stream()
                .map(ContactResponseDto::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseData.<List<ContactResponseDto>>builder()
                .status(200)
                .message("Contacts retrieved successfully")
                .error(null)
                .data(responseDtos)
                .build());
    }

    /**
     * Filter contacts with pagination and sorting
     */
    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Page<ContactResponseDto>>> filterContacts(@RequestBody BaseFilterRequest filter) {
        Page<Contact> result = contactService.filter(filter);
        Page<ContactResponseDto> responsePage = ContactResponseDto.convertPage(result);

        return ResponseEntity.ok(ResponseData.<Page<ContactResponseDto>>builder()
                .status(200)
                .message("Contacts filtered successfully")
                .error(null)
                .data(responsePage)
                .build());
    }

    /**
     * Get contacts count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Long>> getContactsCount() {
        long count = contactService.count();
        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(200)
                .message("Contacts count retrieved successfully")
                .error(null)
                .data(count)
                .build());
    }
}
