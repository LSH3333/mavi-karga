package com.lsh.mavikarga.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// ClothingDto 리스트 DTO
@Data
public class ClothingDtoList {
    List<ClothingDto> clothingDtoList = new ArrayList<>();
}
