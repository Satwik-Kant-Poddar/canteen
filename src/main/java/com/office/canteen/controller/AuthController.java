package com.office.canteen.controller;

import com.office.canteen.domain.Role;
import com.office.canteen.domain.User;
import com.office.canteen.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // If already logged in, redirect to appropriate dashboard
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            return user.getRole() == Role.ADMIN ? "redirect:/admin" : "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> optionalUser = userRepository.findByUsername(username.trim().toLowerCase());

        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            model.addAttribute("lastUsername", username);
            return "login";
        }

        User user = optionalUser.get();
        session.setAttribute("loggedInUser", user);

        return user.getRole() == Role.ADMIN ? "redirect:/admin" : "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
