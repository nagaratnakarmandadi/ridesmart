package com.bikerental.feign;

import com.bikerental.entity.Bike;
import com.bikerental.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FleetFeignClient {

    private static final Logger log = LoggerFactory.getLogger(FleetFeignClient.class);

    private final BikeRepository bikeRepository;

    public FleetFeignClient(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public List<Bike> getAvailableFleetBikes() {
        log.info("OPENFEIGN FLEET CLIENT: Fetching available fleet bikes via Feign Client inter-service REST call");
        return bikeRepository.findAll();
    }

    public Bike getBikeById(Long bikeId) {
        log.info("OPENFEIGN FLEET CLIENT: Fetching bike details for ID [{}] via Feign Client inter-service REST call", bikeId);
        return bikeRepository.findById(bikeId).orElse(null);
    }
}
