package com.bikerental.controller;

import com.bikerental.entity.Customer;
import com.bikerental.entity.CustomerDocument;
import com.bikerental.entity.enums.Enums.DocumentStatus;
import com.bikerental.entity.enums.Enums.VerificationStatus;
import com.bikerental.repository.CustomerDocumentRepository;
import com.bikerental.repository.CustomerRepository;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.AuditService;
import com.bikerental.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/documents")
public class AdminDocumentController {

    private final CustomerDocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public AdminDocumentController(CustomerDocumentRepository documentRepository, CustomerRepository customerRepository, NotificationService notificationService, AuditService auditService) {
        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @GetMapping
    public String documentVerificationList(Model model) {
        List<CustomerDocument> pendingDocs = documentRepository.findByStatus(DocumentStatus.SUBMITTED);
        List<CustomerDocument> allDocs = documentRepository.findAll();
        model.addAttribute("pendingDocs", pendingDocs);
        model.addAttribute("allDocs", allDocs);
        return "admin/document-verification";
    }

    @PostMapping("/verify/{id}")
    public String verifyDocument(@PathVariable Long id,
                                 @RequestParam("status") DocumentStatus status,
                                 @RequestParam(value = "notes", required = false) String notes,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        CustomerDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        document.setStatus(status);
        document.setVerificationNotes(notes);
        document.setVerifiedAt(LocalDateTime.now());
        documentRepository.save(document);

        Customer customer = document.getCustomer();
        List<CustomerDocument> customerDocs = documentRepository.findByCustomerId(customer.getId());

        boolean allVerified = !customerDocs.isEmpty() && customerDocs.stream()
                .allMatch(doc -> doc.getStatus() == DocumentStatus.VERIFIED);
        boolean anyRejected = customerDocs.stream()
                .anyMatch(doc -> doc.getStatus() == DocumentStatus.REJECTED);

        if (allVerified) {
            customer.setVerificationStatus(VerificationStatus.VERIFIED);
            notificationService.sendUserNotification(
                    customer.getUser(),
                    "Identity Documents Verified! 🎉",
                    "All your identity documents have been approved by business owner. You are ready to rent bikes!",
                    "VERIFICATION",
                    "/customer/dashboard"
            );
        } else if (anyRejected) {
            customer.setVerificationStatus(VerificationStatus.REJECTED);
            notificationService.sendUserNotification(
                    customer.getUser(),
                    "Document Verification Failed",
                    "One or more documents were rejected. Reason: " + notes,
                    "VERIFICATION",
                    "/customer/documents"
            );
        } else {
            customer.setVerificationStatus(VerificationStatus.PENDING);
        }
        customerRepository.save(customer);

        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "DOCUMENT_VERIFICATION", "CustomerDocument", id, "Verified status to " + status + " for doc: " + document.getDocumentType(), null);
        redirectAttributes.addFlashAttribute("successMessage", "Document status updated to " + status);

        return "redirect:/admin/documents";
    }
}
