package com.bbi.transactionsp2.dto;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

@Data
public class OrderWithUser {
    private OrderRequest orderRequest;
    private User user;

    public OrderWithUser(OrderRequest orderRequest, User user) {
        this.orderRequest = orderRequest;
        this.user = user;
    }


    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public OrderWithUser() {

    }

}
