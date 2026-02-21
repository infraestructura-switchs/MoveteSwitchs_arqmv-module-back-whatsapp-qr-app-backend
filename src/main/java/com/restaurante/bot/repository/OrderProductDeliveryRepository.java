package com.restaurante.bot.repository;

import com.restaurante.bot.model.OrderProductDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductDeliveryRepository extends JpaRepository<OrderProductDelivery, Long> {

    List<OrderProductDelivery> findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);

    @Query(value = """
            SELECT
                c.phone,
                otd.order_transaction_delivery_id ,
                opd.product_id,
                p.name AS product_name,
                opd.quantity,
                p.price AS price,
                (opd.quantity * p.price) AS total_price,
                 opd.comment_product
            FROM
                order_product_delivery opd
            JOIN
                order_transaction_delivery otd ON opd.order_transaction_delivery_id = otd.order_transaction_delivery_id
            JOIN
                customer c ON otd.customer_id = c.customer_id
            JOIN
                product p  ON opd.product_id = p.product_id
            WHERE
                c.phone = :phoneNumber AND otd.status_order = 'SIN CONFIRMAR'
            """, nativeQuery = true)
    List<Object[]> getOrderProductDeliveryList(String phoneNumber);

}
