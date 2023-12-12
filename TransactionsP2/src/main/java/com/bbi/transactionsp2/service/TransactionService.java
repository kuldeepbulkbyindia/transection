package com.bbi.transactionsp2.service;

import com.bbi.transactionsp2.dto.OrderDetailsDTO;
import com.bbi.transactionsp2.dto.OrderTransactionDTO;
import com.bbi.transactionsp2.dto.TransactionWithDetailsDTO;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TransactionService {

    public Transaction addTransaction(Transaction transaction);
    public List<Transaction> viewAllTransaction();

    public Optional<Transaction> getTransactionById(String id);
    public List<TransactionWithDetailsDTO> viewAllUserOrderTransactions() throws OrderNotFoundException;
    public TransactionWithDetailsDTO getUserOrderTransactionsByUserIdAndOrderId(Long userId, String orderId) throws OrderNotFoundException;

    public OrderDetailsDTO getOrderDetailsByOrderId(String orderId);
}
