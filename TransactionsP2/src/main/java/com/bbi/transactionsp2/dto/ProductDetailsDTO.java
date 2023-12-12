package com.bbi.transactionsp2.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailsDTO {
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Long productQuantity;
    private CategoryMasterDTO categoryMaster;

}


