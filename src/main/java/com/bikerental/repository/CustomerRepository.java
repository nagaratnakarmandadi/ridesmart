package com.bikerental.repository;

import com.bikerental.entity.Customer;
import com.bikerental.entity.User;
import com.bikerental.entity.enums.Enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
    Optional<Customer> findByUserId(Long userId);
    List<Customer> findByVerificationStatus(VerificationStatus status);
}
