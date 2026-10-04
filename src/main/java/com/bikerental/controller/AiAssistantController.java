package com.bikerental.controller;

import com.bikerental.service.AiAdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/ai")
public class AiAssistantController {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantController.class);

    private final AiAdvisorService aiAdvisorService;

    public AiAssistantController(AiAdvisorService aiAdvisorService) {
        this.aiAdvisorService = aiAdvisorService;
    }

    @GetMapping("/advisor")
    public String aiAdvisorPage(Model model) {
        log.info("Accessing AI Advisor Page");
        return "ai-advisor";
    }

    @PostMapping("/recommend")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecommendation(@RequestParam("hours") int hours,
                                                                 @RequestParam("distanceKm") int distanceKm,
                                                                 @RequestParam("tripType") String tripType,
                                                                 @RequestParam(value = "maxBudget", required = false) BigDecimal maxBudget) {
        log.info("REST AI Recommend Endpoint triggered");
        Map<String, Object> result = aiAdvisorService.recommendBike(hours, distanceKm, tripType, maxBudget);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/faq")
    @ResponseBody
    public ResponseEntity<Map<String, String>> askFaq(@RequestParam("query") String query) {
        log.info("REST AI FAQ Endpoint triggered with query: {}", query);
        String answer = aiAdvisorService.answerFaq(query);
        return ResponseEntity.ok(Map.of("query", query, "answer", answer));
    }
}
