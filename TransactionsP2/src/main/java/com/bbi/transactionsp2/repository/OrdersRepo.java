package com.bbi.transactionsp2.repository;

import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepo extends MongoRepository<OrderRequest, String> {
    List<OrderRequest> findByUserId(Long userId);

    List<OrderRequest> findByCompanyId(long companyId);

}
