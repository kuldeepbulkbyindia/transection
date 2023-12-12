package com.bbi.transactionsp2.dto;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.model.User;
import lombok.Data;

import java.util.List;

@Data
public class TransactionWithDetailsDTO {
    private OrderRequest orderRequest;
    private User user;
    List<Transaction> transactions;


}
