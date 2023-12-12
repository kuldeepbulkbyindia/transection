package com.bbi.transactionsp2.dto;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

@Data
public class OrderWithTransactionCountDTO {
    private OrderRequest orderRequest;
    private User user;
    private Long transactionCount;

    public OrderWithTransactionCountDTO(OrderRequest orderRequest, User user, Long transactionCount) {
        this.orderRequest = orderRequest;
        this.user = user;
        this.transactionCount = transactionCount;
    }
}
