package com.jkshian.arms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageRedirectController {

    @GetMapping({"/", "/index", "/index.html", "/home"})
    public String home() {
        return "redirect:/api/v1/auth/index";
    }

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "redirect:/api/v1/auth/login";
    }

    @GetMapping({"/registration", "/registration.html", "/register"})
    public String registration() {
        return "redirect:/api/v1/auth/registration";
    }

    @GetMapping({"/signup", "/signup.html"})
    public String signup() {
        return "redirect:/api/v1/auth/registration";
    }

    @GetMapping({"/contact", "/contact.html"})
    public String contact() {
        return "redirect:/api/v1/auth/contact";
    }

    @GetMapping({"/select", "/select.html"})
    public String select() {
        return "redirect:/api/v1/auth/select";
    }

    @GetMapping({"/ticket", "/ticket.html"})
    public String ticket() {
        return "redirect:/api/v1/auth/ticket";
    }

    @GetMapping({"/payment", "/payment.html"})
    public String payment() {
        return "redirect:/api/v1/auth/payment";
    }

    @GetMapping({"/confirmation", "/confirmation.html"})
    public String confirmation() {
        return "redirect:/api/v1/auth/confirmation";
    }

    @GetMapping({"/my-bookings", "/my-bookings.html"})
    public String myBookings() {
        return "redirect:/api/v1/demo-controller/user/my-bookings";
    }

    @GetMapping({"/admin", "/admin/", "/dashboard", "/dashboard.html", "/admin/dashboard", "/admin/dashboard.html"})
    public String adminDashboard() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    @GetMapping({"/admin/flights", "/admin/flights.html"})
    public String adminFlights() {
        return "redirect:/api/v1/demo-controller/admin/flights";
    }

    @GetMapping({"/admin/flights/new", "/admin/newflight", "/admin/newflight.html"})
    public String adminNewFlight() {
        return "redirect:/api/v1/demo-controller/admin/flights/new";
    }

    @GetMapping({"/admin/bookings", "/admin/bookings.html"})
    public String adminBookings() {
        return "redirect:/api/v1/demo-controller/admin/bookings";
    }

    @GetMapping({"/admin/users", "/admin/users.html", "/admin/UserDetails.html"})
    public String adminUsers() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    @GetMapping({"/admin/users/new", "/admin/user", "/admin/user.html"})
    public String adminNewUser() {
        return "redirect:/api/v1/demo-controller/admin/users/new";
    }

    @GetMapping({"/admin/panel", "/admin/panel.html", "/admin/notifications", "/admin/notifications.html"})
    public String adminNotifications() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }
}
