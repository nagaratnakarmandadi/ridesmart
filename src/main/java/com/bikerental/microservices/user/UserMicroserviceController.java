package com.bikerental.microservices.user;

import com.bikerental.dto.ApiResponse;
import com.bikerental.entity.Customer;
import com.bikerental.entity.enums.Enums.VerificationStatus;
import com.bikerental.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/microservices/users")
public class UserMicroserviceController {

    private static final Logger log = LoggerFactory.getLogger(UserMicroserviceController.class);

    private final CustomerRepository customerRepository;

    public UserMicroserviceController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable("id") Long id) {
        log.info("MICROSERVICE USER REST API: Fetching customer profile for ID [{}]", id);
        return customerRepository.findById(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.success("Customer profile retrieved", customer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/kyc-status")
    public ResponseEntity<ApiResponse<Boolean>> checkKycStatus(@PathVariable("id") Long id) {
        log.info("MICROSERVICE USER REST API: Verifying KYC verification status for Customer ID [{}]", id);
        boolean isVerified = customerRepository.findById(id)
                .map(customer -> customer.getVerificationStatus() == VerificationStatus.VERIFIED)
                .orElse(false);
        return ResponseEntity.ok(ApiResponse.success("KYC Verification Status", isVerified));
    }
}
