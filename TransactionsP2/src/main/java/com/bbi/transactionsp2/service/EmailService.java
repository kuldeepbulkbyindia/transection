package com.bbi.transactionsp2.service;

import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.OrderWithUser;
import com.bbi.transactionsp2.dto.TransactionWithDetailsDTO;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    public List<TransactionWithDetailsDTO> sellersRepliesList(long companyId) throws OrderNotFoundException; //This method is showing data to buyer's screen

    public List<OrderWithUser> viewSellerOrdersByKeywords(long companyId) throws OrderNotFoundException;
    public TransactionWithDetailsDTO getUserOrderTransactionsByOrderIdAndCompanyId(String orderId, long companyId);
}
