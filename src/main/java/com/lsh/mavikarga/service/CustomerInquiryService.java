package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.CustomerInquiry;
import com.lsh.mavikarga.dto.CustomerInquiryReturnDto;
import com.lsh.mavikarga.dto.CustomerInquiryReturnDtoList;
import com.lsh.mavikarga.repository.CustomerInquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    // 환불 문의 내역 객체 CustomerInquiryReturnDto 담은
    public CustomerInquiryReturnDtoList makeCustomerInquiryReturnDtoList(int page, int size) {
        CustomerInquiryReturnDtoList customerInquiryReturnDtoList = new CustomerInquiryReturnDtoList();
        Page<CustomerInquiry> customerInquiryPages = customerInquiryRepository.findAll(PageRequest.of(page, size, Sort.by("createdTime").descending()));

        // 전체 페이지 수
        customerInquiryReturnDtoList.setTotalPages(customerInquiryPages.getTotalPages());

        for (CustomerInquiry customerInquiryPage : customerInquiryPages) {
            CustomerInquiryReturnDto customerInquiryReturnDto = new CustomerInquiryReturnDto(customerInquiryPage.getName(), customerInquiryPage.getPhone(),
                    customerInquiryPage.getOrderLookUpNumber(), customerInquiryPage.getReason(), customerInquiryPage.getCreatedTime());
            customerInquiryReturnDtoList.getCustomerInquiryReturnDtoList().add(customerInquiryReturnDto);
        }

        return customerInquiryReturnDtoList;
    }

}
