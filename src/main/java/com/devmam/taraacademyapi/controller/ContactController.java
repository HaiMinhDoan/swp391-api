package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.ContactRequestDto;
import com.devmam.taraacademyapi.models.dto.response.ContactResponseDto;
import com.devmam.taraacademyapi.models.entities.Contact;
import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.service.impl.entities.ContactService;
import com.devmam.taraacademyapi.service.impl.entities.SerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/contacts")
@PreAuthorize("permitAll()")
public class ContactController extends BaseController<Contact, Integer, ContactRequestDto, ContactResponseDto> {

    @Autowired
    private SerService serService;

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
}
