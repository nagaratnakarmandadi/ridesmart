package com.bikerental.microservices.fleet;

import com.bikerental.dto.ApiResponse;
import com.bikerental.entity.Bike;
import com.bikerental.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/microservices/fleet")
public class FleetMicroserviceController {

    private static final Logger log = LoggerFactory.getLogger(FleetMicroserviceController.class);

    private final BikeRepository bikeRepository;

    public FleetMicroserviceController(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @GetMapping("/bikes")
    public ResponseEntity<ApiResponse<List<Bike>>> getAllFleetBikes() {
        log.info("MICROSERVICE FLEET REST API: Fetching all physical fleet bikes");
        List<Bike> bikes = bikeRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Fleet bikes inventory retrieved", bikes));
    }

    @GetMapping("/bikes/{id}")
    public ResponseEntity<ApiResponse<Bike>> getBikeById(@PathVariable("id") Long id) {
        log.info("MICROSERVICE FLEET REST API: Fetching bike details for ID [{}]", id);
        return bikeRepository.findById(id)
                .map(bike -> ResponseEntity.ok(ApiResponse.success("Bike details retrieved", bike)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
