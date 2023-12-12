package com.bbi.transactionsp2.controller;

import com.bbi.transactionsp2.model.Delivery;
import com.bbi.transactionsp2.model.OrderRequest;
import com.bbi.transactionsp2.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @PostMapping("/addDelivery")
    public Delivery addDelivery(@RequestBody Delivery delivery){
        return deliveryService.addDelivery(delivery);
    }

    @PutMapping("/updateDeliveryDetails")
    public Object updateDelivery(@RequestBody Delivery delivery){
        return deliveryService.updateDelivery(delivery);
    }

    @GetMapping("/getDeliveryByDeliveryId/{deliveryId}")
    public Optional<Delivery> findByDeliveryId(@PathVariable String deliveryId){
        return deliveryService.findByDeliveryId(deliveryId);
    }

    @GetMapping("/getOrderDetailsByDeliveryId/{deliveryId}")
    public Object findOrderByDeliveryId(@PathVariable String deliveryId){
        return deliveryService.findOrderByDeliveryId(deliveryId);
    }

    @DeleteMapping("/deleteDeliveryByDeliveryId/{deliveryId}")
    public void deleteDeliveryById(@PathVariable String deliveryId){
         deliveryService.deleteDeliveryById(deliveryId);
    }
}
