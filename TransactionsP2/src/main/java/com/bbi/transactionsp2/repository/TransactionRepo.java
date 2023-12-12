package com.bbi.transactionsp2.repository;

import com.bbi.transactionsp2.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {
    Optional<List<Transaction>> findByOrderId(String orderId);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByOrderIdIn(List<String> orderIds);

    List<Transaction> findByCompanyId(Long companyId);

    List<Transaction> findByOrderIdAndCompanyIdOrRepliedTo(String orderId, Long companyId, Long repliedTo);


}
