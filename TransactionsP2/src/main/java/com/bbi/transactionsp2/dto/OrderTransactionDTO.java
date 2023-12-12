package com.bbi.transactionsp2.dto;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

import java.time.Instant;

@Data
public class OrderTransactionDTO {
    private String companyName;
    private String companyLogo;
    private Instant transactionDate;
    private String status;
}

