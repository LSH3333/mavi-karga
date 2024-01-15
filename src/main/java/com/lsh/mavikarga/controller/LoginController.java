package com.lsh.mavikarga.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String loginForm(Model model, HttpServletRequest request) {
        model.addAttribute("menu", "login");

        String prevPage = request.getHeader("Referer");
        log.info("loginForm prevPage = {}", prevPage);
        if(prevPage != null && !prevPage.contains("/login")) {
            request.getSession().setAttribute("prevPage", prevPage);
        }

        return "login";
    }

}
