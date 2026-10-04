package com.bikerental.controller.api;

import com.bikerental.entity.CustomerDocument;
import com.bikerental.repository.CustomerDocumentRepository;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
public class DocumentStreamController {

    private final CustomerDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    public DocumentStreamController(CustomerDocumentRepository documentRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/stream/{documentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<Resource> streamDocument(@PathVariable Long documentId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        CustomerDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !doc.getCustomer().getUser().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }

        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());

        String contentType = "application/octet-stream";
        try {
            Path path = Paths.get(resource.getURI());
            contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "image/jpeg";
            }
        } catch (IOException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}
