package com.bbi.transactionsp2.controller;
import com.bbi.transactionsp2.dto.OrderDetailsDTO;
import com.bbi.transactionsp2.dto.OrderTransactionDTO;
import com.bbi.transactionsp2.dto.TransactionWithDetailsDTO;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/addTransaction")
    public Transaction addTransaction(@RequestBody Transaction transaction){
        try {
            return transactionService.addTransaction(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Transaction not added:- " + e.getMessage());
            }
        }

    @GetMapping("/viewAllTransactions")
    public List<Transaction> viewAllTransaction(){
        try{
            return transactionService.viewAllTransaction();
        }catch (Exception e){
            throw new RuntimeException("Transaction's loading error" + e.getMessage());
        }
    }

    @GetMapping("/getTransactionById/{id}")
    public Optional<Transaction> getTransactionById(@PathVariable String id){
        try{
            Optional<Transaction> transaction1=transactionService.getTransactionById(id);
                return transaction1;
        }catch(Exception e){
            throw new RuntimeException("TransactionIds not found " + e.getMessage());
        }
    }

  
    //Below all the methods fetching data from both databases.

    @GetMapping("/viewUserOrderTransactions")
    public List<TransactionWithDetailsDTO> viewUserOrderTransactions() throws OrderNotFoundException {
        //This method will return all the user details along with order details and transactions.
        // It will return the users details as a map type.
            return transactionService.viewAllUserOrderTransactions();
//        }catch (Exception e){
//            throw new RuntimeException("Transaction not loaded, data fetching problem occurred! " + e.getMessage());
//        }
            

    }

    //The below method is used for Leads Page which is used by seller Screen.
    @GetMapping("/viewUserOrderTransactionsById/{userId}/order/{orderId}")
    public TransactionWithDetailsDTO getUserOrderTransactionDetailsByUserIdAndOrderId(@PathVariable Long userId, @PathVariable String orderId) throws OrderNotFoundException {
        return transactionService.getUserOrderTransactionsByUserIdAndOrderId(userId, orderId);
    }

    @GetMapping("/getAllCompaniesDetailsByOrderId/{orderId}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetailsById(@PathVariable String orderId) {
        OrderDetailsDTO orderDetailsDTO = transactionService.getOrderDetailsByOrderId(orderId);

        if (orderDetailsDTO != null) {
            return new ResponseEntity<>(orderDetailsDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
