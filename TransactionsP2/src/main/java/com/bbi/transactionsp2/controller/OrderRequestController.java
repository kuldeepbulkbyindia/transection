package com.bbi.transactionsp2.controller;

import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.OrderWithUser;
import com.bbi.transactionsp2.dto.TransactionWithDetailsDTO;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.service.EmailService;
import com.bbi.transactionsp2.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/orderRequest")
@CacheConfig(cacheNames = "orderRequest")
public class OrderRequestController {

    @Autowired
    OrdersService ordersService;


    @PostMapping("/addOrderRequest")
    public OrderRequest addOrder(@RequestBody OrderRequest orders){
        return ordersService.addOrder(orders);
    }
    @GetMapping("/viewAllOrder")
    public List<OrderRequest> viewAllOrders(){
        return ordersService.viewAllOrders();
    }
    @GetMapping("/findOrderById/{orderId}")
    public Optional<OrderRequest> findOrderById(@PathVariable String orderId){
        return ordersService.findOrderById(orderId);
    }

    @PutMapping("/updateOrderById")
    public Object updateOrder(@RequestBody OrderRequest orders){
        return ordersService.updateOrder(orders);
    }

    //The below methods are fetching data from both databases.
    @GetMapping("/getAllUserWithOrders")
    public List<OrderWithUser> getAllOrdersWithUsers() {
        return ordersService.getAllOrdersWithUsers();
    }

    @GetMapping("/fetchDataById/{userId}/order/{orderId}")
    public OrderWithUser getUserOrderDetailsByOrderIdAndUserId(@PathVariable Long userId, @PathVariable String orderId) throws OrderNotFoundException {
        return ordersService.getUserOrderDetailsByOrderIdAndUserId(userId, orderId);
    }

    //BBI admin controller method below.
    @GetMapping("/buyerOrderList")
    public ResponseEntity<List<OrderWithTransactionCountDTO>> getBuyerOrderListWithTransactionCount() {
        try {
            List<OrderWithTransactionCountDTO> buyerOrderList = ordersService.getBuyerOrderListWithTransactionCount();
            return new ResponseEntity<>(buyerOrderList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sellerOrderList/{userId}")
    public ResponseEntity<List<OrderWithTransactionCountDTO>> getSellerOrderListWithTransactionCount(@PathVariable Long userId) {
        try {
            List<OrderWithTransactionCountDTO> sellerOrderList = ordersService.getSellerOrderListWithTransactionCount(userId);
            return new ResponseEntity<>(sellerOrderList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
