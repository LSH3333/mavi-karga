package com.lsh.mavikarga.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("menu", "home");
        return "index";
    }

    @GetMapping("/clothing")
    public String clothing(Model model) {
        model.addAttribute("menu", "clothing");
        return "clothing";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("menu", "info");
        return "info";
    }

    @GetMapping("/payTest")
    public String payTest() {
        return "payments/payTest";
    }
}
