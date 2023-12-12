package com.bbi.transactionsp2.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "orderRequest")
public class OrderRequest {
    @Id
    private String orderId; //Auto Generated
    private Long userId;
    private Long companyId;
    private String productName;
    private String tradingConditions;
    private Double quantityValue;
    private String quantityUnit;
    private Double targetUnitPrice;
    private Double maxBudget;
    private String unitPriceCurrency;
    private String description;
    private String status = "Open";

    private Instant transactionTime = Instant.now();

    public OrderRequest() {
        this.orderId = OrderIdGenerator.generateOrderId(); // Generating the orderId
        // This constructor I am using to customize the orderID from "98766R54666DF4" to "BBI-9087321671".
    }



}


