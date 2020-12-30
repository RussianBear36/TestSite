package ru.koldaev.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.koldaev.entity.User;

@Controller
public class MainController {
    @GetMapping("/")
    public String getIndex(
            @AuthenticationPrincipal User user,
            Model model
            )
    {
        model.addAttribute("username", user.getFirstname());
        return "index";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
}
