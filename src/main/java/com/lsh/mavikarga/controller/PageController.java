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

    @GetMapping("/film")
    public String film(Model model) {
        model.addAttribute("menu", "film");
        return "film";
    }

    @GetMapping("/object")
    public String object(Model model) {
        model.addAttribute("menu", "object");
        return "object";
    }

    @GetMapping("/makingStory/1")
    public String makingStory1(Model model) {
        model.addAttribute("menu", "clothing");
        return "makingStory1";
    }

    @GetMapping("/policy/privacyPolicy")
    public String privacyPolicy() {
        return "policy/privacyPolicy";
    }

    @GetMapping("/policy/termsOfService")
    public String termsOfService() {
        return "policy/termsOfService";
    }

    // USD/KOR 버튼 눌러서 Locale 변환
    @GetMapping("/locale")
    public String changeLocale(HttpServletRequest request, HttpServletResponse response) {

        // 현재 locale 이 KOREA 라면 ENGLISH 로 아니면 반대로 변경
        Locale currentLocale = localeResolver.resolveLocale(request);

        if(currentLocale == Locale.US) {
            localeResolver.setLocale(request, response, Locale.KOREA);
        } else {
            localeResolver.setLocale(request, response, Locale.US);
        }

        return "redirect:/";
    }




}
