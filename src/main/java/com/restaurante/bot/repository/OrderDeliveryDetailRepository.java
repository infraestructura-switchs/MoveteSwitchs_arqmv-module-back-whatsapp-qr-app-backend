package com.restaurante.bot.repository;

import com.restaurante.bot.model.OrderDetailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDeliveryDetailRepository extends JpaRepository<OrderDetailDelivery, Long> {

    List<OrderDetailDelivery> findByStatus(String status);

    List<OrderDetailDelivery> findByStatusAndStatusOrder(String status, String statusOrder);

    OrderDetailDelivery findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);

    // Product-level queries are handled in OrderProductDeliveryRepository


}
