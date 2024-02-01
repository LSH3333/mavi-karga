package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MyPageDtoList {
    private List<MyPageDto> myPageDtoList = new ArrayList<>();
    private int totalPages;
}
