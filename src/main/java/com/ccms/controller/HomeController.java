package com.ccms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Simple redirect for home to the memebers page
    @GetMapping("/")
    public String home() {
        return "redirect:/members";
    }
}