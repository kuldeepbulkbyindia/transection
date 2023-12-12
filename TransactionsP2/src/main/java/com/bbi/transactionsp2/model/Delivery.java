package com.bbi.transactionsp2.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "delivery")
public class Delivery {
    @Id
    private String deliveryId;
    private String orderId;
    private String deliveryAddress;
    private String personNameForDelivery;
    private String personPhoneForDelivery;
    private Double quantityValue;
    private String quantityUnit;
    private String unitPriceCurrency;
    private Double orderPrice;
    private Instant transactionTime = Instant.now();
    private Long deliveryCharges;
    private Long taxes;
    private Long grossTotal;
    private String trackingUrl;
    private String uploadBill;
}
