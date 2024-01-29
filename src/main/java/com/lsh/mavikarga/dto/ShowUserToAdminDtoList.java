package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShowUserToAdminDtoList {
    private List<ShowUserToAdminDto> showUserToAdminDtoList = new ArrayList<>();

    //
    private int totalPages;
}
