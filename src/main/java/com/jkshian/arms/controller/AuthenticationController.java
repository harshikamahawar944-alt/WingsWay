package com.jkshian.arms.controller;

import com.jkshian.arms.config.JwtService;
import com.jkshian.arms.dto.AuthenticationRequest;
import com.jkshian.arms.dto.AuthenticationResponse;
import com.jkshian.arms.dto.BookingDto;
import com.jkshian.arms.dto.FlightOptionDto;
import com.jkshian.arms.dto.RegisterRequest;
import com.jkshian.arms.dto.RouteSuggestionDto;
import com.jkshian.arms.entity.User;
import com.jkshian.arms.service.AuthenticationService;
import com.jkshian.arms.service.PlaneService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final PlaneService planeService;
    private final JwtService jwtService;

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(Model model, Authentication authentication, HttpServletRequest request) {
        boolean loggedIn = (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken))
                || hasValidJwtCookie(request);
        model.addAttribute("loggedIn", loggedIn);

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                    + (user.getLastName() != null ? user.getLastName() : "")).trim();
            model.addAttribute("profileName", fullName.isBlank() ? user.getEmail() : fullName);
            model.addAttribute("profileEmail", user.getEmail());
            model.addAttribute("profileRole", user.getRole() != null ? user.getRole().name().replace("ROLE_", "") : "USER");
            model.addAttribute("profileInitial", !fullName.isBlank()
                    ? fullName.substring(0, 1).toUpperCase()
                    : user.getEmail().substring(0, 1).toUpperCase());
        } else {
            model.addAttribute("profileName", "WingWay Passenger");
            model.addAttribute("profileEmail", "Signed in");
            model.addAttribute("profileRole", "USER");
            model.addAttribute("profileInitial", "W");
        }
        return "User/index";
    }

    @GetMapping({"/login", "/login.html"})
    public String login(){
        return "User/login";
    }

    @GetMapping({"/registration", "/registration.html"})
    public String registration() {
        return "User/registration";}

    @GetMapping({"/contact", "/contact.html"})
    public String contact() {
        return "User/contact";
    }

    @GetMapping({"/payment", "/payment.html"})
    public String payment() {
        return "User/payment";
    }

    @GetMapping({"/ticket", "/ticket.html"})
    public String ticket() {
        return "User/ticket";
    }

    @GetMapping({"/select", "/select.html"})
    public String select() {
        return "User/select";
    }

    @GetMapping("/flights/suggestions")
    @ResponseBody
    public ResponseEntity<java.util.List<RouteSuggestionDto>> flightSuggestions(@RequestParam(name = "query", defaultValue = "") String query) {
        return ResponseEntity.ok(planeService.getRouteSuggestions(query));
    }

    @PostMapping("/flights/search")
    @ResponseBody
    public ResponseEntity<java.util.List<FlightOptionDto>> searchFlights(@RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(planeService.searchFlightOptions(bookingDto.getBstart(), bookingDto.getBend(), bookingDto.getBnumofseat()));
    }

    @GetMapping("/confirmation")
    public String confirmation() {
        return "User/confirmation";
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request, HttpServletResponse response){
        AuthenticationResponse authResponse = service.registerUser(request);
        if (authResponse == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        response.addHeader("Set-Cookie", buildJwtCookie(authResponse.getToken()).toString());
        return ResponseEntity.ok(authResponse);
    }



  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response){
    AuthenticationResponse authResponse;
    try {
        authResponse = service.authenticate(request);
    } catch (AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
        if (authResponse != null) {
            response.addHeader("Set-Cookie", buildJwtCookie(authResponse.getToken()).toString());
            return ResponseEntity.ok(authResponse);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", clearJwtCookie().toString());
        return "redirect:/api/v1/auth/login?logout=true";
    }

    private ResponseCookie buildJwtCookie(String token) {
        return ResponseCookie.from("jwtToken", token)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 24)
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie clearJwtCookie() {
        return ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    private boolean hasValidJwtCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return false;
        }

        for (Cookie cookie : request.getCookies()) {
            if (!"jwtToken".equals(cookie.getName()) || cookie.getValue() == null || cookie.getValue().isBlank()) {
                continue;
            }

            try {
                String token = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                return jwtService.extractUsername(token) != null;
            } catch (JwtException | IllegalArgumentException ex) {
                return false;
            }
        }

        return false;
    }


}
