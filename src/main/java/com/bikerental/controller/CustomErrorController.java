package com.bikerental.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        String errorTitle = "Unexpected Error";
        String errorDescription = "An unexpected error occurred. Please try again or return to homepage.";

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorTitle = "Page Not Found (404)";
            errorDescription = "The page or resource you requested could not be found.";
        } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
            errorTitle = "Invalid Request (400)";
            errorDescription = "The request contained invalid or mismatched parameters. Please check your form input.";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            errorTitle = "Access Denied (403)";
            errorDescription = "You do not have authorization to access this page.";
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorDescription", errorDescription);
        model.addAttribute("detailMessage", message != null ? message.toString() : (exception != null ? exception.toString() : ""));

        return "error";
    }
}
