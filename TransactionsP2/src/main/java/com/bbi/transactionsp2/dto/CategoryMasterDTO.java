package com.bbi.transactionsp2.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryMasterDTO {
    private Long categoryId;
    private String categoryName;
    private String slug;
    private String categoryDescription;
    private Boolean activeInactive;

}

