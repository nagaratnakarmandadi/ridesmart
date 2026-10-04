package com.bikerental.repository;

import com.bikerental.entity.CustomerDocument;
import com.bikerental.entity.enums.Enums.DocumentStatus;
import com.bikerental.entity.enums.Enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long> {
    List<CustomerDocument> findByCustomerId(Long customerId);
    Optional<CustomerDocument> findByCustomerIdAndDocumentType(Long customerId, DocumentType documentType);
    List<CustomerDocument> findByStatus(DocumentStatus status);
}
