package com.bbi.transactionsp2.controller;

import com.bbi.transactionsp2.dto.OrderWithUser;
import com.bbi.transactionsp2.dto.TransactionWithDetailsDTO;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emailReplies")
public class EmailServiceController {

    @Autowired
    EmailService emailService;

    @GetMapping("/sellerReplies/{companyId}")
    public ResponseEntity<List<TransactionWithDetailsDTO>> getSellersReplies(@PathVariable long companyId) throws OrderNotFoundException {

            List<TransactionWithDetailsDTO> sellersReplies = emailService.sellersRepliesList(companyId);
            if (sellersReplies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//                throw new OrderNotFoundException("No orders found with companyId: " + companyId);
            }
            return new ResponseEntity<>(sellersReplies, HttpStatus.OK);
    }

    @GetMapping("/BuyerReplies/{companyId}")
    public ResponseEntity<List<OrderWithUser>> getBuyerReplies(@PathVariable long companyId) {
        try {
            List<OrderWithUser> sellersReplies = emailService.viewSellerOrdersByKeywords(companyId);
            return new ResponseEntity<>(sellersReplies, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            // Handle the OrderNotFoundException specifically, if needed
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/viewTransactionsByOrderIdAndCompanyId/{orderId}/company/{companyId}")
    public TransactionWithDetailsDTO getUserOrderTransactionDetailsByOrderIdAndCompanyId(
            @PathVariable String orderId,
            @PathVariable long companyId) throws OrderNotFoundException {
        return emailService.getUserOrderTransactionsByOrderIdAndCompanyId(orderId, companyId);
//        return transactionService.getUserOrderTransactionsByOrderIdAndCompanyId(orderId, companyId);
        //This method is retrieving transactions between two companyId by sending orderId and seller's companyId
    }

}
