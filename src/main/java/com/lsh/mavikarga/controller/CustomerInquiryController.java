package com.lsh.mavikarga.controller;

import com.lsh.mavikarga.dto.CustomerInquiryReturnDto;
import com.lsh.mavikarga.service.CustomerInquiryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class CustomerInquiryController {

    private final CustomerInquiryService customerInquiryService;

    @Autowired
    public CustomerInquiryController(CustomerInquiryService customerInquiryService) {
        this.customerInquiryService = customerInquiryService;
    }

    // 문의 접수 성공 페이지
    @GetMapping("/customerCenter/customerInquiryReceivedSuccess")
    public String customerInquiryReceivedSuccess() {
        return "customerCenter/customerInquiryReceivedSuccess";
    }

    // 고객센터, 취소/반품/교환
    @GetMapping("/customerCenter/returns")
    public String customerCenterReturnsForm(@ModelAttribute CustomerInquiryReturnDto customerInquiryReturnDto) {
        return "customerCenter/returns";
    }

    @PostMapping("/customerCenter/returns")
    public String customerCenterReturns(@ModelAttribute CustomerInquiryReturnDto customerInquiryReturnDto) {
        log.info("customerCenterReturns = {}", customerInquiryReturnDto);

        customerInquiryService.saveCustomerInquiryReturnForm(customerInquiryReturnDto);
        return "redirect:/customerCenter/customerInquiryReceivedSuccess";
    }
}
