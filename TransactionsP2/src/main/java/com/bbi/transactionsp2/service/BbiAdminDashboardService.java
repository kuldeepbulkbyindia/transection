package com.bbi.transactionsp2.service;

import com.bbi.transactionsp2.dto.CompanyDetailsDTO;
import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.SellerDetailsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BbiAdminDashboardService {
    public List<OrderWithTransactionCountDTO> getBuyerOrderListWithTransactionCount();
    public List<CompanyDetailsDTO> getAllCompanyWiseDetails();
}
