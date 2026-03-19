package com.restaurante.bot.repository;
import com.restaurante.bot.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    Optional<OrderProduct> findByOrderId(Long orderId);

    @Query("SELECT SUM(op.quantity * op.unitPrice) FROM OrderProduct op WHERE op.orderId = :orderId GROUP BY op.orderId")
    Double findTotalAmountByOrderId(Long orderId);
}
