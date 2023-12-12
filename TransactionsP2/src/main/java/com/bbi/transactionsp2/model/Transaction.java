package com.bbi.transactionsp2.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document( collection="transactions")
public class Transaction {
    @Id
    private String transactionId;
    private String orderId;
    private Long companyId;
    private Long userId;
    private String emailContent;
    private Boolean replied;
    private Instant transactionTime = Instant.now();
    private String status;
    private String companyName;
    private Long repliedTo; // This field has been added to filter that this transaction(conversation) has done between which companyId to which companyId.

}

