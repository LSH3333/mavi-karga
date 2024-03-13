package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.MyPageDtoList;
import com.lsh.mavikarga.service.OrderService;
import com.lsh.mavikarga.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Slf4j
public class UserController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public UserController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }


    // 마이페이지
    @GetMapping("/users/myPage")
    public String myPageForm(Model model, Principal principal, @RequestParam(defaultValue = "0") int page) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if(user == null) return "error";

        MyPageDtoList myPageDtoList = orderService.createMyPageDtoList(user.getId(), page, 10);

        model.addAttribute("myPageDtoList", myPageDtoList);
        // current page
        model.addAttribute("page", page+1);
        model.addAttribute("menu", "mypage");
        model.addAttribute("username", user.getUsername());

        return "users/myPage";
    }

    // 회원 탈퇴
    @PostMapping("/users/delete")
    public String deleteUser(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if(user != null) {
            userService.deleteUser(user.getId());
            log.info("deleteUser {}", user.getId());
        }

        return "redirect:/logout";
    }

}
