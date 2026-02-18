package com.restaurante.bot.repository;
import com.restaurante.bot.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    Optional<OrderProduct> findByOrderId(Long orderId);

    @Query(value = "SELECT " +
                   "SUM(quantity * unite_price) AS total_amount " +
                   "FROM " +
                   "order_product " +
                   "WHERE " +
                   "order_id = :orderId " +
                   "GROUP BY " +
                   "order_id", nativeQuery = true)
    Double findTotalAmountByOrderId(Integer orderId);
}
