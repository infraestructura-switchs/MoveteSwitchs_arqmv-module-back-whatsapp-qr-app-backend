package com.restaurante.bot.repository;

import com.restaurante.bot.model.OrderProductDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductDeliveryRepository extends JpaRepository<OrderProductDelivery, Long> {

    List<OrderProductDelivery> findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);

    List<OrderProductDelivery> findByOrderTransactionDeliveryIdIn(List<Long> orderIds);

    @Query("SELECT c.phone, otd.orderTransactionDeliveryId, opd.productId, p.name, opd.quantity, p.price, (opd.quantity * p.price) AS totalPrice, opd.commentProduct " +
            "FROM OrderProductDelivery opd, OrderDetailDelivery otd, Customer c, Product p " +
            "WHERE opd.orderTransactionDeliveryId = otd.orderTransactionDeliveryId " +
            "AND otd.customerId = c.customer_id " +
            "AND opd.productId = p.productId " +
            "AND c.phone = :phoneNumber " +
            "AND otd.statusOrder = 'SIN CONFIRMAR'")
    List<Object[]> getOrderProductDeliveryList(String phoneNumber);

}
