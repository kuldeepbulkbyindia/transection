package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.dto.*;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.Company;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.model.User;
import com.bbi.transactionsp2.repository.OrdersRepo;
import com.bbi.transactionsp2.repository.TransactionRepo;
import com.bbi.transactionsp2.service.EmailService;
import com.bbi.transactionsp2.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "email-page")
public class EmailServiceImpl implements EmailService {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    OrdersRepo ordersRepo;

    @Autowired
    OrdersService ordersService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private OrdersServiceImpl ordersServiceImpl;
    private final KeywordsService keywordsService;

    private List<CompanyKeywordsDTO> companyList;
    @Autowired
    public EmailServiceImpl(KeywordsService keywordsService) {
        this.keywordsService = keywordsService;
    }
    private final String keywordsApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/keywords/{companyId}";
    @Override
    @Cacheable(value = "'sellersRepliesList'") //This method is showing data to buyer's Leads screen
    public List<TransactionWithDetailsDTO> sellersRepliesList(long companyId) throws OrderNotFoundException {
        // Step 1: Retrieve all orders with the given companyId
        List<OrderRequest> orders = ordersRepo.findByCompanyId(companyId);
//        System.out.println(orders);
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No orders found with companyId: " + companyId);
        }

        // Step 2: Retrieve all transactions associated with these orders
        List<TransactionWithDetailsDTO> result = new ArrayList<>();

        for (OrderRequest order : orders) {
            String orderId = order.getOrderId();

            // Fetch all transactions associated with this orderId
            Optional<List<Transaction>> transactionsOptional = transactionRepo.findByOrderId(orderId);

            if (transactionsOptional.isPresent() && !transactionsOptional.get().isEmpty()) {
                User user = ordersService.fetchUserDetails(transactionsOptional.get().get(0).getUserId());

                // Create TransactionWithDetailsDTO for this orderId
                TransactionWithDetailsDTO transactionDTO = new TransactionWithDetailsDTO();
                transactionDTO.setUser(user);
                transactionDTO.setOrderRequest(order);
                transactionDTO.setTransactions(transactionsOptional.get());

                result.add(transactionDTO);
            }
        }

        return result;
    }

    @Override
    @Cacheable(value = "'sellerLeadsList'")
    public List<OrderWithUser> viewSellerOrdersByKeywords(long companyId) throws OrderNotFoundException {
        try {
            List<OrderRequest> orders = ordersRepo.findAll();
            System.out.println(orders);
            List<String> sellerKeywords = keywordsService.getKeywordsByCompanyId(companyId);
            System.out.println(sellerKeywords);
            // Retrieve all orders (without fetching transactions)

            List<OrderWithUser> result = new ArrayList<>();

            for (OrderRequest order : orders) {
                // Check if the order's product name matches any of the seller's keywords
                if (sellerKeywords.contains(order.getProductName())) {
                    // Fetch the user details for the order
                    User user = ordersService.fetchUserDetails(order.getUserId());

                    // Create OrderWithUser for this order (without fetching transactions)
                    OrderWithUser orderWithUser = new OrderWithUser(order, user);

                    result.add(orderWithUser);
                }
            }

            return result;
        } catch (Exception e) {
            System.out.println ("An error occurred while processing orders");
            throw new RuntimeException("An error occurred while processing orders", e);
        }


    }

    //The below method is used for Leads Page which is used by seller Screen.
    @Override
    @Cacheable(value = "'#orderId + '-' + #companyId'")
    public TransactionWithDetailsDTO getUserOrderTransactionsByOrderIdAndCompanyId(String orderId, long companyId) {
        //This method is retrieving transactions between two companyId by sending orderId and seller's companyId
        // Fetch order details (orderRequest) based on orderId
        Optional<OrderRequest> orderRequestOptional = ordersService.findOrderById(orderId);

        if (orderRequestOptional.isPresent()) {
            OrderRequest orderRequest = orderRequestOptional.get();

            // Fetch user details based on userId from order details
            User user = ordersServiceImpl.fetchUserDetails(orderRequest.getUserId());

            // Fetch all transactions related to the given orderId, companyId, or repliedTo
            List<Transaction> transactions = transactionRepo.findByOrderIdAndCompanyIdOrRepliedTo(orderId, companyId, companyId);

            // Create a UserOrderTransactionDetails object
            TransactionWithDetailsDTO details = new TransactionWithDetailsDTO();
            details.setUser(user);
            details.setOrderRequest(orderRequest);
            details.setTransactions(transactions);

            return details;
        } else {
            return null; // or handle accordingly
        }
    }




}

