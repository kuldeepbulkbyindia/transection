package com.bbi.transactionsp2.service;

import com.bbi.transactionsp2.model.Delivery;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface DeliveryService {
    public Delivery addDelivery(Delivery delivery);
    public Object updateDelivery(Delivery delivery);

    public Optional<Delivery> findByDeliveryId(String deliveryId);
    public Object findOrderByDeliveryId(String deliveryId);
    public void deleteDeliveryById(String deliveryId);

}
