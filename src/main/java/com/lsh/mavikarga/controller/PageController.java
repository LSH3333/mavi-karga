package com.lsh.mavikarga.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Controller
@Slf4j
public class PageController {

    private final LocaleResolver localeResolver;

    @Autowired
    public PageController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("menu", "home");
        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("menu", "info");
        return "info";
    }

    @GetMapping("/locale")
    public String changeLocale(HttpServletRequest request, HttpServletResponse response) {

        // 현재 locale 이 KOREA 라면 ENGLISH 로 아니면 반대로 변경
        Locale currentLocale = localeResolver.resolveLocale(request);
        log.info("currentLocale = {}", currentLocale);

        if(currentLocale == Locale.US) {
            localeResolver.setLocale(request, response, Locale.KOREA);
        } else {
            localeResolver.setLocale(request, response, Locale.US);
        }

        return "redirect:/";
    }

    /////// TEST
    @GetMapping("/sliderTest")
    public String sliderTest() {
        return "test/sliderTest";
    }



}
