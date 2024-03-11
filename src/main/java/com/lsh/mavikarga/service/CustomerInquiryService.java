package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.CustomerInquiry;
import com.lsh.mavikarga.dto.CustomerInquiryReturnDto;
import com.lsh.mavikarga.repository.CustomerInquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerInquiryService {

    private final CustomerInquiryRepository customerInquiryRepository;

    @Autowired
    public CustomerInquiryService(CustomerInquiryRepository customerInquiryRepository) {
        this.customerInquiryRepository = customerInquiryRepository;
    }

    // 고객 문의사항 (취소/반품/교환) 저장
    public void saveCustomerInquiryReturnForm(CustomerInquiryReturnDto customerInquiryReturnDto) {
        CustomerInquiry customerInquiry = new CustomerInquiry(customerInquiryReturnDto);
        customerInquiryRepository.save(customerInquiry);
    }
}
