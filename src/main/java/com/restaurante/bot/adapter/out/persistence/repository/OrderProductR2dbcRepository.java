package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.OrderProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderProductR2dbcRepository extends ReactiveCrudRepository<OrderProductEntity, Long> {

    Flux<OrderProductEntity> findByOrderId(Long orderId);

    @Query("""
            SELECT SUM(quantity * unite_price) AS total_amount
            FROM order_product
            WHERE order_id = :orderId
            GROUP BY order_id
            """)
    Mono<Double> findTotalAmountByOrderId(Integer orderId);
}
