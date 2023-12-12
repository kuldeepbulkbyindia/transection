package com.bbi.transactionsp2.dto;

import lombok.Data;

import java.util.List;

@Data
public class SellerDetailsDTO {
    private String companyName;
    private String companyEmail;
    private List<String> categories;
    private Long productsCount;
    private Long quotesSent;
    private Long orderClosure;
    private Double efficiency;
    private List<ProductDetailsDTO> products;
}


