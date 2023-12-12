package com.bbi.transactionsp2.dto;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailsDTO {
    private OrderRequest orderRequest;
    private User user;
    private List<OrderTransactionDTO> transactions;
}
