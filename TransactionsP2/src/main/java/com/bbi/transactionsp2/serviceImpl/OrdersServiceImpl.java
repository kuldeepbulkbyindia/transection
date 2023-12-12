package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.dto.OrderWithTransactionCountDTO;
import com.bbi.transactionsp2.dto.OrderWithUser;
import com.bbi.transactionsp2.exceptions.OrderNotFoundException;
import com.bbi.transactionsp2.exceptions.UserNotFoundException;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.model.User;
import com.bbi.transactionsp2.repository.OrdersRepo;
import com.bbi.transactionsp2.repository.TransactionRepo;
import com.bbi.transactionsp2.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "orderRequest")
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    OrdersRepo ordersRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RestTemplate restTemplate;

    private final String userApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/user/{userId}";
    private final String productsApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/productDetails/getDetailsByCompanyId/{companyId}";



//    @CacheEvict(value = "orderRequest", allEntries = true)
//    public void clearAllCache(){
//        clearCacheAfterOneMinute("allOrdersWithUsers");
//    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "'allOrders'", allEntries = true),
                    @CacheEvict(value = "'allOrdersWithUsers'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'#userId + '-' + #orderId'", allEntries = true),
                    @CacheEvict(value = "'#orderId'", allEntries = true),
                    @CacheEvict(value = "'sellerOrdersWithTransactionCount'", allEntries = true),
                    @CacheEvict(value = "'buyerOrdersWithTransactionCount'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'sellerLeadsList'", allEntries = true),
            }
    )
    public OrderRequest addOrder(OrderRequest orders) {
        return ordersRepo.save(orders);
    }

    @Override
    @Cacheable(value = "'allOrders'")
    public List<OrderRequest> viewAllOrders() {
        return ordersRepo.findAll();
    }

    @Override
    @Cacheable(value = "'#orderId'")
    public Optional<OrderRequest> findOrderById(String orderId) {
        return ordersRepo.findById(orderId);
    }

    @Override
//    @CachePut(value = "all-orders-with-users")
    @Caching(
            evict = {
                    @CacheEvict(value = "'allOrders'", allEntries = true),
                    @CacheEvict(value = "'allOrdersWithUsers'", allEntries = true),
                    @CacheEvict(value = "'#userId + '-' + #orderId'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'#orderId'", allEntries = true),
                    @CacheEvict(value = "'sellerOrdersWithTransactionCount'", allEntries = true),
                    @CacheEvict(value = "'buyerOrdersWithTransactionCount'", allEntries = true),
                    @CacheEvict(value = "'sellersRepliesList'", allEntries = true),
                    @CacheEvict(value = "'sellerLeadsList'", allEntries = true),
            }
    )
    public Object updateOrder(OrderRequest orders) {
        Optional<OrderRequest> orderRequest1= ordersRepo.findById(orders.getOrderId());

        if(orderRequest1.isPresent()){
            OrderRequest orderRequest = orderRequest1.get();
            orderRequest.setUserId(orders.getUserId());
            orderRequest.setProductName(orders.getProductName());
            orderRequest.setTradingConditions(orders.getTradingConditions());
            orderRequest.setQuantityValue(orders.getQuantityValue());
            orderRequest.setQuantityUnit(orders.getQuantityUnit());
            orderRequest.setTargetUnitPrice(orders.getTargetUnitPrice());
            orderRequest.setMaxBudget(orders.getMaxBudget());
            orderRequest.setUnitPriceCurrency(orders.getUnitPriceCurrency());
            orderRequest.setDescription(orders.getDescription());
            orderRequest.setStatus(orders.getStatus());

            return ordersRepo.save(orderRequest); //It is updating value.
        }else{
            return "OrderID not found! Please try with different orderID";
        }

    }

    //The below methods are fetching data of orders and user both.

    @Override
    @Cacheable(value = "'allOrdersWithUsers'")
    public List<OrderWithUser> getAllOrdersWithUsers() {
        List<OrderWithUser> ordersWithUsers = new ArrayList<>();
        try {
            List<OrderRequest> orderRequests = ordersRepo.findAll();

            for (OrderRequest orderRequest : orderRequests) {
                User user = fetchUserDetails(orderRequest.getUserId());

                OrderWithUser orderWithUser = new OrderWithUser(orderRequest, user);

                ordersWithUsers.add(orderWithUser);
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }

        return ordersWithUsers;
    }


    @Override
    @Cacheable(value = "'#userId + '-' + #orderId'")
    public OrderWithUser getUserOrderDetailsByOrderIdAndUserId(Long userId, String orderId) throws OrderNotFoundException {
        try {
            // Fetch order details based on orderId
            OrderRequest orderRequest = ordersRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));

            // Fetch user details based on userId
            User user = fetchUserDetails(userId);

            // Create a UserOrderDetails object
            OrderWithUser userOrderDetails = new OrderWithUser();
            userOrderDetails.setUser(user);
            userOrderDetails.setOrderRequest(orderRequest);

            return userOrderDetails;
        } catch (OrderNotFoundException e) {
            throw new OrderNotFoundException("Order not found", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }


    @Override
    public User fetchUserDetails(Long userId) {
        try {
            ResponseEntity<User> responseEntity = restTemplate.getForEntity(userApiUrl, User.class, userId);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else {
                throw new UserNotFoundException("Error: User Not found related to this order");
            }
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (RestClientException e) {
            return null;
        }
    }


    @Override  // Need to add filter for giving only last 3 months data and Pagination also,
    @Cacheable(value = "'buyerOrdersWithTransactionCount'") // This is the data for BBI super admin dashboard.
    public List<OrderWithTransactionCountDTO> getBuyerOrderListWithTransactionCount() {
        List<OrderWithTransactionCountDTO> buyerOrdersWithTransactionCount = new ArrayList<>();

        // Fetch all transactions
        List<Transaction> buyerTransactions = transactionRepo.findAll();
        System.out.println("Transactions have been fetched successfully");

        // Get distinct order IDs
        Set<String> distinctOrderIds = buyerTransactions.stream()
                .map(Transaction::getOrderId)
                .collect(Collectors.toSet());

        // Fetch order details for each distinct orderId
        distinctOrderIds.forEach(orderId -> {
            // Fetch the complete OrderRequest based on orderId
            Optional<OrderRequest> orderRequestOptional = ordersRepo.findById(orderId);
            if (orderRequestOptional.isPresent()) {
                OrderRequest orderRequest = orderRequestOptional.get();

                // Fetch the user details for the order
                User user = ordersService.fetchUserDetails(orderRequest.getUserId());

                // Filter based on userType
                if ("Buyer".equalsIgnoreCase(user.getUserType()) || "Both".equalsIgnoreCase(user.getUserType())) {
                    // Filter transactions by orderId
                    List<Transaction> orderTransactions = buyerTransactions.stream()
                            .filter(transaction -> orderId.equals(transaction.getOrderId()))
                            .toList();

                    // Exclude the buyer's company
                    Set<Long> uniqueCompanies = orderTransactions.stream()
                            .filter(transaction -> !transaction.getUserId().equals(orderRequest.getUserId()))
                            .map(Transaction::getCompanyId)
                            .collect(Collectors.toSet());

                    // Subtract 1 from the transaction count for the buyer's company
                    long transactionCount = Math.max(0, uniqueCompanies.size() - 1);

                    OrderWithTransactionCountDTO orderWithTransactionCountDTO =
                            new OrderWithTransactionCountDTO(orderRequest, user, transactionCount);
                    buyerOrdersWithTransactionCount.add(orderWithTransactionCountDTO);
                }
            } else {
                System.out.println("OrderId not found: " + orderId);
            }
        });

        return buyerOrdersWithTransactionCount;
    }



    @Override
    @Cacheable(value = "'sellerOrdersWithTransactionCount'")
    public List<OrderWithTransactionCountDTO> getSellerOrderListWithTransactionCount(Long userId) {
        List<OrderWithTransactionCountDTO> sellerOrdersWithTransactionCount = new ArrayList<>();

        // Fetch all orders where the given userId is the seller
        List<OrderRequest> sellerOrders = ordersRepo.findByUserId(userId);

        // Group orders by orderId
        Map<String, Set<Long>> orderCountMap = sellerOrders.stream()
                .collect(Collectors.toMap(OrderRequest::getOrderId, order -> new HashSet<>()));

        // Fetch all transactions related to the seller's orders
        List<Transaction> sellerTransactions = transactionRepo.findByOrderIdIn(new ArrayList<>(orderCountMap.keySet()));

        // Group transactions by orderId and unique companyId
        Map<String, Set<Long>> transactionCountMap = new HashMap<>();
        sellerTransactions.forEach(transaction -> {
            String orderId = transaction.getOrderId();
            Long companyId = transaction.getCompanyId();

            // Exclude transactions with the seller's own company
            if (!companyId.equals(userId)) {
                // Count only unique companies for each order
                transactionCountMap.computeIfAbsent(orderId, k -> new HashSet<>()).add(companyId);
            }
        });

        // Fetch order details for each orderId
        transactionCountMap.forEach((orderId, uniqueCompanies) -> {
            // Fetch the complete OrderRequest based on orderId
            Optional<OrderRequest> orderRequestOptional = ordersService.findOrderById(orderId);

            orderRequestOptional.ifPresent(orderRequest -> {
                User user = ordersService.fetchUserDetails(orderRequest.getUserId());

                // Calculate the transaction count for the seller's company
                long transactionCount = uniqueCompanies.size();

                OrderWithTransactionCountDTO orderWithTransactionCountDTO =
                        new OrderWithTransactionCountDTO(orderRequest, user, transactionCount);
                sellerOrdersWithTransactionCount.add(orderWithTransactionCountDTO);
            });
        });

        return sellerOrdersWithTransactionCount;
    }

}
