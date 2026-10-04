package com.bikerental.controller;

import com.bikerental.entity.Customer;
import com.bikerental.entity.Role;
import com.bikerental.entity.User;
import com.bikerental.entity.enums.Enums.RoleName;
import com.bikerental.entity.enums.Enums.VerificationStatus;
import com.bikerental.repository.CustomerRepository;
import com.bikerental.repository.RoleRepository;
import com.bikerental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email/mobile number or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("registrationForm") RegistrationForm form,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (userRepository.existsByEmail(form.getEmail())) {
            model.addAttribute("errorMessage", "Email is already registered!");
            return "register";
        }
        if (userRepository.existsByMobileNumber(form.getMobileNumber())) {
            model.addAttribute("errorMessage", "Mobile number is already registered!");
            return "register";
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER)));

        User user = User.builder()
                .fullName(form.getFullName())
                .email(form.getEmail())
                .mobileNumber(form.getMobileNumber())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles(Set.of(customerRole))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(savedUser)
                .dateOfBirth(form.getDateOfBirth())
                .address(form.getAddress())
                .city(form.getCity())
                .state(form.getState())
                .emergencyContact(form.getEmergencyContact())
                .verificationStatus(VerificationStatus.NOT_VERIFIED)
                .build();

        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login to complete document upload.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Password reset instructions have been sent to " + email);
        return "redirect:/forgot-password";
    }

    public static class RegistrationForm {
        private String fullName;
        private String email;
        private String mobileNumber;
        private String password;
        private LocalDate dateOfBirth;
        private String address;
        private String city;
        private String state;
        private String emergencyContact;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMobileNumber() { return mobileNumber; }
        public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    }
}
