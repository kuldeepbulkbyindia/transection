package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.dto.*;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.model.User;
import com.bbi.transactionsp2.repository.OrdersRepo;
import com.bbi.transactionsp2.repository.TransactionRepo;
import com.bbi.transactionsp2.service.OrdersService;
import com.bbi.transactionsp2.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@CacheConfig(cacheNames = "transactions")
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    OrdersRepo ordersRepo;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersServiceImpl ordersServiceImpl;
    private final RestTemplate restTemplate = new RestTemplate();



    @CacheEvict(value = "transactions", allEntries = true)
    public void clearAllCache(){
        clearCacheAfterRefreshing("allOrdersWithUsersAndTransactions");
    }
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "'allOrdersWithUsersAndTransactions'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'#userId + '-' + #orderId'", allEntries = true),
                    @CacheEvict(value = "'viewAllTransactions'", allEntries = true),
                    @CacheEvict(value = "'#id'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'sellerLeadsList'", allEntries = true),
            }
    )
    public Transaction addTransaction(Transaction transaction) {
        clearAllCache();
        return transactionRepo.save(transaction);
    }

    @Override
    @Cacheable(value ="'viewAllTransactions'")
    public List<Transaction> viewAllTransaction() {
        return transactionRepo.findAll();
    }

    @Override  //This method is used to find transaction by transactionId.
    @Cacheable(value = "'#id'")
    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepo.findById(id);
    }




    //The below methods are for getting data of 3 tables (order,user and transaction)

    @Override
    @Cacheable(value = "'allOrdersWithUsersAndTransactions'")
    public List<TransactionWithDetailsDTO> viewAllUserOrderTransactions() throws OrderNotFoundException {
        List<Transaction> transactions = transactionRepo.findAll();
        Map<String, TransactionWithDetailsDTO> transactionMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String orderId = transaction.getOrderId();

            // Fetch the complete OrderRequest based on orderId
            Optional<OrderRequest> orderRequestOptional = ordersService.findOrderById(orderId);

            if (orderRequestOptional.isPresent()) {
                OrderRequest orderRequest = orderRequestOptional.get();
                User user = ordersService.fetchUserDetails(transaction.getUserId());

                // Create or get the TransactionWithDetailsDTO for this orderId
                TransactionWithDetailsDTO transactionDTO = transactionMap.getOrDefault(orderId, new TransactionWithDetailsDTO());

                // Set the user details and order details if not already set
                if (transactionDTO.getUser() == null) {
                    transactionDTO.setUser(user);
                }
                if (transactionDTO.getOrderRequest() == null) {
                    transactionDTO.setOrderRequest(orderRequest);
                }

                // Add the current transaction to the list of transactions
                List<Transaction> transactionList = transactionDTO.getTransactions();
                if (transactionList == null) {
                    transactionList = new ArrayList<>();
                }
                transactionList.add(transaction);
                transactionDTO.setTransactions(transactionList);

                // Update the TransactionWithDetailsDTO in the map
                transactionMap.put(orderId, transactionDTO);
            } else {
//                throw new OrderNotFoundException(orderId);
                continue;
            }
        }
        clearAllCache();
        return new ArrayList<>(transactionMap.values());
    }

    //This method is getting data of transactions by userId and orderId.

    @Override
    @Cacheable(value = "'#userId + '-' + #orderId'")
    public TransactionWithDetailsDTO getUserOrderTransactionsByUserIdAndOrderId(Long userId, String orderId) {
        // Fetch user details based on userId
        User user = ordersService.fetchUserDetails(userId);

        // Fetch order details (orderRequest) based on orderId
        Optional<OrderRequest> orderRequestOptional = ordersService.findOrderById(orderId);

        if (orderRequestOptional.isPresent()) {
            OrderRequest orderRequest = orderRequestOptional.get();

            // Fetch all transactions related to the given orderId
            Optional<List<Transaction>> transactionsOptional = transactionRepo.findByOrderId(orderId);

            if (transactionsOptional.isPresent()) {
                List<Transaction> transactions = transactionsOptional.get();

                // Create a UserOrderTransactionDetails object
                TransactionWithDetailsDTO details = new TransactionWithDetailsDTO();
                details.setUser(user);
                details.setOrderRequest(orderRequest);
                details.setTransactions(transactions);

                return details;
            } else {
                return null;
//                return "No transactions found for orderId: " + orderId;
            }
        } else {
            return null;
        }
    }

    //The below method is for dashboard, getAllCompaniesDetailsByOrderId.

    @Override
    public OrderDetailsDTO getOrderDetailsByOrderId(String orderId) {
        try {
            // Fetch order details based on orderId
            Optional<OrderRequest> orderOptional = ordersRepo.findById(orderId);
            if (!orderOptional.isPresent()) {
                return null; // or throw an exception, depending on your requirements
            }
            OrderRequest order = orderOptional.get();

            // Fetch user details based on userId from the order
            User user = ordersService.fetchUserDetails(order.getUserId());

            // Fetch transactions related to the given orderId
            Optional<List<Transaction>> transactionsOptional = transactionRepo.findByOrderId(orderId);
            if (!transactionsOptional.isPresent() || transactionsOptional.get().isEmpty()) {
                // Return an empty OrderDetailsDTO or throw an exception, depending on your requirements
                return new OrderDetailsDTO();
            }

            // Create a set to store unique company IDs
            Set<Long> uniqueCompanyIds = new HashSet<>();

            // Create a list to store the final result
            List<OrderTransactionDTO> transactionDTOs = new ArrayList<>();

            // Populate the result list with the required information
            List<Transaction> transactions = transactionsOptional.get();
            for (Transaction transaction : transactions) {
                // Skip transactions related to the ordering company
                if (transaction.getCompanyId().equals(order.getCompanyId())) {
                    continue;
                }

                // Skip transactions for the same seller company
                if (!uniqueCompanyIds.add(transaction.getCompanyId())) {
                    continue;
                }

                // Fetch company details using a separate method
                CompanyDetailsDTO companyDetails = fetchCompanyDetailsById(transaction.getCompanyId());

                // Create an OrderTransactionDTO for the company
                OrderTransactionDTO dto = new OrderTransactionDTO();
                dto.setCompanyName(companyDetails.getCompanyName());
                dto.setCompanyLogo(companyDetails.getCompanyLogo());
                dto.setTransactionDate(transaction.getTransactionTime());
                dto.setStatus(transaction.getStatus());

                // Add the OrderTransactionDTO to the result list
                transactionDTOs.add(dto);
            }

            // Create and return the OrderDetailsDTO
            OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
            orderDetailsDTO.setOrderRequest(order);
            orderDetailsDTO.setUser(user);
            orderDetailsDTO.setTransactions(transactionDTOs);

            return orderDetailsDTO;
        } catch (Exception e) {
            // Handle exceptions accordingly
            return new OrderDetailsDTO(); // or throw an exception, depending on your requirements
        }
    }



    private CompanyDetailsDTO fetchCompanyDetailsById(Long companyId) {
        try {
            // Replace the URL with your actual endpoint
            String companyApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/company/getCompanyDetailsWithoutKeywords/" + companyId;

            ResponseEntity<CompanyDetailsDTO> responseEntity = restTemplate.getForEntity(companyApiUrl, CompanyDetailsDTO.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                // Handle non-success status codes if needed
                System.out.println("Failed to fetch company details. Status code: " + responseEntity.getStatusCodeValue());
                return new CompanyDetailsDTO();
            }
        } catch (Exception e) {
            // Handle exceptions accordingly
            e.printStackTrace();
            return new CompanyDetailsDTO();
        }
    }




    //The below method is used to clear the cache for transactions.
    @Autowired
    private CacheManager cacheManager;

    public void clearCacheAfterRefreshing(String allOrdersWithUsersAndTransactions) {
        Cache cache = cacheManager.getCache(allOrdersWithUsersAndTransactions);
        System.out.println("Cache Manager loaded...");
        if (cache != null) {
            cache.clear();
        }
    }
  
    
    
}
