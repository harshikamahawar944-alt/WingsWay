package com.jkshian.arms.demo;

import com.jkshian.arms.dto.AuthenticationRequest;
import com.jkshian.arms.dto.AuthenticationResponse;
import com.jkshian.arms.dto.BookingDto;
import com.jkshian.arms.dto.Planedto;
import com.jkshian.arms.dto.RegisterRequest;
import com.jkshian.arms.entity.Booking;
import com.jkshian.arms.entity.User;
import com.jkshian.arms.service.AuthenticationService;
import com.jkshian.arms.service.BookingService;
import com.jkshian.arms.service.PlaneService;
import com.jkshian.arms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    private final PlaneService planeService;
    private final UserService userService;
    private final AuthenticationService service;
    private final BookingService bookingService;

    public DemoController(PlaneService planeService, UserService userService, AuthenticationService service, BookingService bookingService) {
        this.planeService = planeService;
        this.userService = userService;
        this.service = service;
        this.bookingService = bookingService;
    }

    @GetMapping("/indexForRegistation")
    public String indexForRegistationUsers() {
        return "/User/index";
    }

    @GetMapping("/user/my-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public String myBookings(Model model, Authentication authentication) {
        model.addAttribute("bookings", bookingService.getBookingsForUser(authentication.getName()));
        return "/User/my-bookings";
    }

    @PostMapping("/index")
    public String index(@RequestBody AuthenticationRequest request) {
        if (userService.checkUserRoleIsAdmin(request)) {
            return "redirect:/api/v1/demo-controller/admin/getAllUsers";
        } else {
            return "/User/index";
        }
    }

    @GetMapping({"/admin/dashboard", "/admin/dashboard.html"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String adminDashboard() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    @GetMapping("/admin/flights")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String flightsPage(Model model) {
        model.addAttribute("stuList", planeService.getAllPlane());
        return "/Admin/flights";
    }

    @GetMapping("/admin/flights.html")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String flightsPageAlias() {
        return "redirect:/api/v1/demo-controller/admin/flights";
    }

    @GetMapping("/admin/flights/new")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String newFlightPage(Model model) {
        if (!model.containsAttribute("stu")) {
            model.addAttribute("stu", new Planedto());
        }
        return "/Admin/newflight";
    }

    @GetMapping("/admin/newflight.html")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String newFlightPageAlias() {
        return "redirect:/api/v1/demo-controller/admin/flights/new";
    }

    @PostMapping("/admin/flights")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createFlight(@ModelAttribute("stu") Planedto planedto, RedirectAttributes redirectAttributes) {
        ResponseEntity<String> response = planeService.createNewPlane(planedto);
        redirectAttributes.addFlashAttribute("message", response.getBody());
        if (response.getStatusCode().is2xxSuccessful()) {
            return "redirect:/api/v1/demo-controller/admin/flights";
        }

        redirectAttributes.addFlashAttribute("stu", planedto);
        return "redirect:/api/v1/demo-controller/admin/flights/new";
    }

    @PostMapping("/admin/flights/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String deleteFlight(@PathVariable int id, RedirectAttributes redirectAttributes) {
        ResponseEntity<String> response = planeService.deletePlaneById(id);
        redirectAttributes.addFlashAttribute("message", response.getBody());
        return "redirect:/api/v1/demo-controller/admin/flights";
    }

    // Admin only can access this method
    @PostMapping("/admin/addplane")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addNewPlane(@RequestBody Planedto planedto) {
        return planeService.createNewPlane(planedto);
    }

    // Admin only can access this method
    @GetMapping("/admin/getPlaneById")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String getPlaneById(@RequestParam("id") int id, Model model) {
        Planedto findPlane = planeService.getPlaneById(id);
        model.addAttribute("Plane", findPlane);
        model.addAttribute("stuList", List.of(findPlane));
        return "/Admin/flights";
    }

    // Admin only can access this method
    @GetMapping("/admin/getAllPlane")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String getAllPlane(Model model) {
        List<Planedto> planes = planeService.getAllPlane();
        model.addAttribute("Planes", planes);
        model.addAttribute("stuList", planes);
        return "/Admin/flights";
    }

    // Admin only can access this method
    @PutMapping("/admin/updateplane")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updatePlane(@RequestBody Planedto planedto) {
        return planeService.updatePlane(planedto);
    }

    // Admin only can access this method
    @DeleteMapping("/admin/deletePlaneById")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deletePlaneById(@RequestParam("id") int id) {
        return planeService.deletePlaneById(id);
    }

    @GetMapping("/admin/users/new")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String newUserPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "/Admin/user";
    }

    @GetMapping("/admin/user.html")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String newUserPageAlias() {
        return "redirect:/api/v1/demo-controller/admin/users/new";
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String createAdminUser(@ModelAttribute RegisterRequest registerRequest, RedirectAttributes redirectAttributes) {
        AuthenticationResponse response = service.registerAdmin(registerRequest);
        if (response == null) {
            redirectAttributes.addFlashAttribute("message", "User already exists");
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:/api/v1/demo-controller/admin/users/new";
        }

        redirectAttributes.addFlashAttribute("message", "User added successfully");
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    // Admin only can access this method
    @GetMapping("/admin/getAllUsers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("Users", users);
        model.addAttribute("user", users);
        return "/Admin/UserDetails";
    }

    @GetMapping("/admin/UserDetails.html")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String getAllUsersAlias() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    @GetMapping({"/admin/notifications", "/admin/panel"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String notificationsPage(Model model) {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    @GetMapping("/admin/bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String bookingsPage(Model model) {
        model.addAttribute("bookings", bookingService.getAllBooking());
        return "/Admin/bookings";
    }

    @GetMapping("/admin/bookings.html")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String bookingsPageAlias() {
        return "redirect:/api/v1/demo-controller/admin/bookings";
    }

    @GetMapping({"/admin/notifications.html", "/admin/panel.html"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String notificationsPageAlias() {
        return "redirect:/api/v1/demo-controller/admin/getAllUsers";
    }

    private void addAdminPanelModel(Model model) {
        List<Planedto> flights = planeService.getAllPlane();
        List<Booking> bookings = bookingService.getAllBooking();
        List<User> users = userService.getAllUsers();

        model.addAttribute("flightCount", flights.size());
        model.addAttribute("bookingCount", bookings.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("passengerCount", users.stream()
                .filter(user -> user.getRole() != null && "ROLE_USER".equals(user.getRole().name()))
                .count());
        model.addAttribute("adminCount", users.stream()
                .filter(user -> user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().name()))
                .count());
        model.addAttribute("recentFlights", flights.stream().limit(3).toList());
        model.addAttribute("recentBookings", bookings.stream().limit(2).toList());
        model.addAttribute("recentUsers", users.stream().limit(4).toList());
    }

    // Admin only can access this method
    @PostMapping("/admin/register")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = service.registerAdmin(request);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    // Admin only can access this method
    @GetMapping("/admin/getAllbooking")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String getAllBooking(Model model) {
        List<Booking> bookings = bookingService.getAllBooking();
        model.addAttribute("bookings", bookings);
        return "/Admin/bookings";
    }

    // User only can access this method
    @PostMapping("/checkPrice")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<Double> checkPrice(@RequestBody BookingDto bookingDto) {
        return bookingService.checkPrice(bookingDto);
    }

    // User only can access this method
    @PostMapping("/checkPrice/addBooking")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<String> addBooking(@RequestBody BookingDto bookingdto, Authentication authentication) {
        return bookingService.addBooking(bookingdto, authentication.getName());
    }
}
