package com.bikerental.config;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BikeRepository bikeRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final TermsVersionRepository termsVersionRepository;
    private final PrivacyPolicyVersionRepository privacyPolicyVersionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, CustomerRepository customerRepository, BikeRepository bikeRepository, PricingRuleRepository pricingRuleRepository, TermsVersionRepository termsVersionRepository, PrivacyPolicyVersionRepository privacyPolicyVersionRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.bikeRepository = bikeRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.termsVersionRepository = termsVersionRepository;
        this.privacyPolicyVersionRepository = privacyPolicyVersionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Roles
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER)));

        // 2. Default Admin Account
        if (!userRepository.existsByEmail("admin@bikerental.com")) {
            User admin = User.builder()
                    .email("admin@bikerental.com")
                    .fullName("System Owner / Admin")
                    .mobileNumber("9999999999")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(adminRole))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
        }

        // 3. Sample Customer Account
        if (!userRepository.existsByEmail("john@example.com")) {
            User customerUser = User.builder()
                    .email("john@example.com")
                    .fullName("John Doe")
                    .mobileNumber("9876543210")
                    .password(passwordEncoder.encode("customer123"))
                    .roles(Set.of(customerRole))
                    .enabled(true)
                    .build();
            userRepository.save(customerUser);

            Customer customer = Customer.builder()
                    .user(customerUser)
                    .dateOfBirth(LocalDate.of(1995, 5, 15))
                    .address("123 Main Street, Hitech City")
                    .city("Hyderabad")
                    .state("Telangana")
                    .emergencyContact("9876543211")
                    .verificationStatus(VerificationStatus.NOT_VERIFIED)
                    .build();
            customerRepository.save(customer);
        }

        // 4. Default Pricing Rule
        if (pricingRuleRepository.findByIsDefaultTrue().isEmpty()) {
            PricingRule defaultRule = PricingRule.builder()
                    .ruleName("Standard 12H / 80KM Plan")
                    .baseRate(new BigDecimal("500.00"))
                    .includedHours(12)
                    .includedKm(80)
                    .extraKmRate(new BigDecimal("3.00"))
                    .lateHourlyRate(new BigDecimal("50.00"))
                    .cancellationFee(new BigDecimal("100.00"))
                    .active(true)
                    .isDefault(true)
                    .build();
            pricingRuleRepository.save(defaultRule);
        }

        // 5. Sample Fleet Bikes
        if (bikeRepository.count() == 0) {
            createBike("KA-01-EQ-1234", "Royal Enfield", "Classic 350", "Stealth Black", 2023, "Black", "Petrol", 349, 12500L, "GPS-RE-350", 500.00, FuelLevel.FULL, "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800");
            createBike("KA-01-AB-5678", "Honda", "Activa 6G", "DLX", 2022, "Matte Blue", "Petrol", 110, 8400L, "GPS-HONDA-6G", 350.00, FuelLevel.FULL, "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=800");
            createBike("KA-01-CD-9012", "Yamaha", "R15 V4", "Racing Blue", 2024, "Blue", "Petrol", 155, 4200L, "GPS-YAM-R15", 750.00, FuelLevel.FULL, "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=800");
            createBike("KA-01-EF-3456", "TVS", "Jupiter 125", "Disc", 2023, "Titanium Grey", "Petrol", 124, 6100L, "GPS-TVS-JUP", 380.00, FuelLevel.FULL, "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800");
            createBike("KA-01-GH-7890", "Bajaj", "Pulsar NS200", "BS6", 2023, "White & Red", "Petrol", 199, 9800L, "GPS-BAJ-NS200", 600.00, FuelLevel.FULL, "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=800");
        }

        // 6. Default Legal Documents
        if (termsVersionRepository.count() == 0) {
            termsVersionRepository.save(TermsVersion.builder()
                    .versionNumber("v1.0")
                    .title("Smart Bike Rental - Terms & Conditions")
                    .content("1. Driving License & Eligibility: Must hold a valid Indian Driving License for Two-Wheelers with gear.\n2. Vehicle Usage & Speed Limits: Do not exceed 80 km/h. Illegal racing or sub-letting is strictly prohibited.\n3. Fuel Policy & Extra Charges: Return with equal fuel. Extra kilometers beyond 80 KM billed at ₹3/km. Late return billed at ₹50/hr.")
                    .active(true)
                    .build());
        }

        if (privacyPolicyVersionRepository.count() == 0) {
            privacyPolicyVersionRepository.save(PrivacyPolicyVersion.builder()
                    .versionNumber("v1.0")
                    .title("Smart Bike Rental - Privacy Policy")
                    .content("### Data Privacy & Security\nWe collect identity documents (Aadhaar, Driving License, Selfie) solely for rental verification and compliance with vehicle security standards. Your data is stored securely and never shared with third parties.")
                    .active(true)
                    .build());
        }
    }

    private void createBike(String reg, String brand, String model, String variant, int year, String color, String fuel, int cc, long odo, String gpsId, double price, FuelLevel fuelLevel, String imgUrl) {
        Bike bike = Bike.builder()
                .registrationNumber(reg)
                .brand(brand)
                .model(model)
                .variant(variant)
                .manufacturingYear(year)
                .color(color)
                .fuelType(fuel)
                .engineCapacity(cc)
                .currentOdometer(odo)
                .gpsDeviceId(gpsId)
                .gpsProvider("SpeedTrack GPS")
                .status(BikeStatus.AVAILABLE)
                .rentalPrice(new BigDecimal(price))
                .includedHours(12)
                .includedKm(80)
                .extraKmRate(new BigDecimal("3.00"))
                .lateHourlyRate(new BigDecimal("50.00"))
                .currentFuelLevel(fuelLevel)
                .insuranceExpiry(LocalDate.now().plusMonths(8))
                .registrationExpiry(LocalDate.now().plusYears(4))
                .pollutionExpiry(LocalDate.now().plusMonths(5))
                .lastServiceOdometer(odo - 1500)
                .lastServiceDate(LocalDate.now().plusMonths(-2))
                .imageUrl(imgUrl)
                .description("Well-maintained " + brand + " " + model + " with GPS live tracking, helmet included, and full tank capacity.")
                .build();
        bikeRepository.save(bike);
    }
}
