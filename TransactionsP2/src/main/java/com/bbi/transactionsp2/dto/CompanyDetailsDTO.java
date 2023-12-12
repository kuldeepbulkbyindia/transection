package com.bbi.transactionsp2.dto;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDetailsDTO {
    private String companyName;
    private String companyLogo;
    private Long quotesSent;
    private Long orderClosure;
//    private String categoryName;
private List<String> categoryName;
    private Long productsCount;

}
