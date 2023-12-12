package com.bbi.transactionsp2.serviceImpl;

import com.bbi.transactionsp2.model.Delivery;
import com.bbi.transactionsp2.repository.DeliveryRepo;
import com.bbi.transactionsp2.repository.OrdersRepo;
import com.bbi.transactionsp2.service.DeliveryService;
import com.bbi.transactionsp2.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryRepo deliveryRepo;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersRepo ordersRepo;
    @Override
    public Delivery addDelivery(Delivery delivery) {
        try{
            return deliveryRepo.save(delivery);
        }catch (Exception e){
            throw new RuntimeException("Failed to add Delivery",e);
        }

    }

    @Override
    public Object updateDelivery(Delivery delivery) {
        try{
            Optional<Delivery> delivery1= deliveryRepo.findById(delivery.getDeliveryId());
            if(delivery1.isPresent()){
                Delivery delivery2= delivery1.get();
                delivery2.setDeliveryAddress(delivery.getDeliveryAddress());
                delivery2.setDeliveryCharges(delivery.getDeliveryCharges());
                delivery2.setPersonNameForDelivery(delivery.getPersonNameForDelivery());
                delivery2.setPersonPhoneForDelivery(delivery.getPersonPhoneForDelivery());
                delivery2.setGrossTotal(delivery.getGrossTotal());
                delivery2.setOrderId(delivery.getOrderId());
                delivery2.setTaxes(delivery.getTaxes());
                delivery2.setQuantityUnit(delivery.getQuantityUnit());
                delivery2.setOrderPrice(delivery.getOrderPrice());
                delivery2.setQuantityValue(delivery.getQuantityValue());
                delivery2.setUnitPriceCurrency(delivery.getUnitPriceCurrency());
                delivery2.setTrackingUrl(delivery.getTrackingUrl());
                delivery2.setUploadBill(delivery.getUploadBill());

                return deliveryRepo.save(delivery);
        }else{
                throw new RuntimeException("Failed to update product details");
            }
        }catch(Exception e){
            throw new RuntimeException("Delivery id Not found!, Please fill correct details",e);
        }

    }

    @Override
    public Optional<Delivery> findByDeliveryId(String deliveryId) {
        try{
            return deliveryRepo.findById(deliveryId);
        }catch (Exception e){
            throw new RuntimeException("DeliveryId is invalid or No data available for this delivery id.",e);
        }

    }

    @Override
    public Object findOrderByDeliveryId(String deliveryId) {
        try{
            Optional<Delivery> delivery1= findByDeliveryId(deliveryId);
            if(delivery1.isPresent()){
                return ordersService.findOrderById(delivery1.get().getOrderId());
            }else{
                throw new RuntimeException("Delivery id is invalid! Please give correct deliveryId");
            }
        }catch(Exception e){
            throw new RuntimeException("Failed to find order by delivery id", e);
        }

    }

    @Override
    public void deleteDeliveryById(String deliveryId) {
        try{
            deliveryRepo.deleteById(deliveryId);
        }catch(Exception e){
            throw new RuntimeException("Failed to delete delivery by this delivery id",e);
        }

    }
}
