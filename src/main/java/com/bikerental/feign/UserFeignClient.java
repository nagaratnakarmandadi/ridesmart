package com.bikerental.feign;

import com.bikerental.entity.Customer;
import com.bikerental.entity.enums.Enums.VerificationStatus;
import com.bikerental.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClient {

    private static final Logger log = LoggerFactory.getLogger(UserFeignClient.class);

    private final CustomerRepository customerRepository;

    public UserFeignClient(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomerByUserId(Long userId) {
        log.info("OPENFEIGN USER CLIENT: Fetching customer KYC profile for User ID [{}] via Feign Client inter-service REST call", userId);
        return customerRepository.findByUserId(userId).orElse(null);
    }

    public boolean isKycVerified(Long customerId) {
        log.info("OPENFEIGN USER CLIENT: Verifying KYC status for Customer ID [{}] via Feign Client inter-service REST call", customerId);
        return customerRepository.findById(customerId)
                .map(c -> c.getVerificationStatus() == VerificationStatus.VERIFIED)
                .orElse(false);
    }
}
