package com.bbi.transactionsp2.service;

import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.OrderWithUser;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface OrdersService {
    public OrderRequest addOrder(OrderRequest orders);
    public List<OrderRequest> viewAllOrders();
    public Optional<OrderRequest> findOrderById(String orderId);
    public Object updateOrder(OrderRequest orders);

//    The below orders are fetching data of orders and users both
    public List<OrderWithUser> getAllOrdersWithUsers();
    public OrderWithUser getUserOrderDetailsByOrderIdAndUserId(Long userId, String orderId) throws OrderNotFoundException;
    public User fetchUserDetails(Long userId);
    public List<OrderWithTransactionCountDTO> getBuyerOrderListWithTransactionCount();
    public List<OrderWithTransactionCountDTO> getSellerOrderListWithTransactionCount(Long userId);
}
