package com.bbi.transactionsp2.controller;

import com.bbi.transactionsp2.dto.CompanyDetailsDTO;
import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.ProductDetailsDTO;
import com.bbi.transactionsp2.dto.SellerDetailsDTO;
import com.bbi.transactionsp2.service.BbiAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bbiAdmin")
public class BbiAdminDashboardController {

    @Autowired
    BbiAdminDashboardService bbiAdminDashboardService;

    @GetMapping("/buyerOrderList")
    public ResponseEntity<List<OrderWithTransactionCountDTO>> getBuyerOrderListWithTransactionCount() {
        try {
            List<OrderWithTransactionCountDTO> buyerOrderList = bbiAdminDashboardService.getBuyerOrderListWithTransactionCount();
            return new ResponseEntity<>(buyerOrderList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sellerDetails")
    public ResponseEntity<List<CompanyDetailsDTO>> getAllSellerDetails() {
        try {
            List<CompanyDetailsDTO> sellerDetails = bbiAdminDashboardService.getAllCompanyWiseDetails();
            return new ResponseEntity<>(sellerDetails, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
