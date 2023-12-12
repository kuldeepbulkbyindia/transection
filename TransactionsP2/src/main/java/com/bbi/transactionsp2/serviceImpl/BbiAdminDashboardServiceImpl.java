package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.dto.*;
import com.bbi.transactionsp2.model.Company;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import com.bbi.transactionsp2.model.User;
import com.bbi.transactionsp2.repository.OrdersRepo;
import com.bbi.transactionsp2.repository.TransactionRepo;
import com.bbi.transactionsp2.service.BbiAdminDashboardService;
import com.bbi.transactionsp2.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BbiAdminDashboardServiceImpl implements BbiAdminDashboardService {


    @Autowired
    OrdersRepo ordersRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    OrdersService ordersService;


    private final RestTemplate restTemplate = new RestTemplate();

    @Override  // Need to add filter for giving only last 3 months data and Pagination also,
    @Cacheable(value = "'buyerOrdersWithTransactionCount'")
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
    @Cacheable(value = "'CompanyWiseDetails'")
    public List<CompanyDetailsDTO> getAllCompanyWiseDetails() {
        List<CompanyDetailsDTO> companyDetailsList = new ArrayList<>();

        // Step 1: Fetch Companies
        List<Company> companies = fetchAllCompanies();

        // Step 2: Fetch Transactions and calculate quotes sent and order closure
        for (Company company : companies) {
            Long companyId = company.getCompanyId();
            List<Transaction> transactions = transactionRepo.findByCompanyId(companyId);

            // Calculate Quotes Sent and Order Closure for the current company
            Long quotesSent = calculateQuotesSent(transactions);
            Long orderClosure = calculateOrderClosure(transactions);

            // Fetch Products and Categories for the current company
            List<ProductDetailsDTO> products = fetchProductsByCompanyId(companyId);
            List<String> categoryNames = getCategoryNames(products);

            // Combine Information
            CompanyDetailsDTO companyDetailsDTO = new CompanyDetailsDTO();
            companyDetailsDTO.setCompanyName(company.getCompanyName());
            companyDetailsDTO.setCompanyLogo(company.getCompanyLogo()); //The logo has been added afterward. Logo can give problems somewhere. need to check.
            companyDetailsDTO.setQuotesSent(quotesSent);
            companyDetailsDTO.setOrderClosure(orderClosure);
            companyDetailsDTO.setCategoryName(categoryNames);
            companyDetailsDTO.setProductsCount((long) products.size());

            // Add to the final list
            companyDetailsList.add(companyDetailsDTO);
        }

        return companyDetailsList;
    }


    private List<String> getCategoryNames(List<ProductDetailsDTO> products) {
        if (products.isEmpty()) {
//            System.out.println("No Product Found");
            return new ArrayList<>();
        }

        return products.stream()
                .map(product -> product.getCategoryMaster())
                .filter(Objects::nonNull)
                .map(CategoryMasterDTO::getCategoryName)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Company> fetchAllCompanies() {
        // Use the appropriate URL to fetch companies
        String companyListUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/company/list";
        ResponseEntity<List<Company>> responseEntity = restTemplate.exchange(
                companyListUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Company>>() {
                });

        return responseEntity.getBody();
    }

    private long calculateQuotesSent(List<Transaction> transactions) {
        // Extract all order IDs from transactions and count the unique ones
        return transactions.stream()
                .map(Transaction::getOrderId)
                .distinct()  // Removing duplicate order IDs
                .count();
    }


    private long calculateOrderClosure(List<Transaction> transactions) {
        return transactions.stream().filter(transaction -> "Closed".equalsIgnoreCase(transaction.getStatus())).count();
    }

    private List<ProductDetailsDTO> fetchProductsByCompanyId(Long companyId) {
        try {

            String productsApiUrl = "https://jboi3auod0.execute-api.ap-south-1.amazonaws.com/bulkbuy/productDetails/getDetailsByCompanyId/" + companyId;
            ResponseEntity<List<ProductDetailsDTO>> responseEntity = restTemplate.exchange(
                    productsApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductDetailsDTO>>() {
                    });

            return responseEntity.getBody();
        } catch (Exception e) {

            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
