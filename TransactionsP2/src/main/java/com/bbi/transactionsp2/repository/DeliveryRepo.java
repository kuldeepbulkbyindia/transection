package com.bbi.transactionsp2.repository;

import com.bbi.transactionsp2.model.Delivery;
import com.bbi.transactionsp2.model.OrderRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeliveryRepo extends MongoRepository<Delivery, String> {

    Optional<OrderRequest> findOrderByDeliveryId(String deliveryId);
}
