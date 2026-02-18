package com.restaurante.bot.repository;

import com.restaurante.bot.dto.OrderDeliveryResponseDTO;
import com.restaurante.bot.dto.OrderProductDeliveryResponseDTO;
import com.restaurante.bot.model.OrderDetailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDeliveryDetailRepository extends JpaRepository<OrderDetailDelivery, Long> {

    List<OrderDetailDelivery> findByStatus(String status);

    @Query("""
    SELECT new com.restaurante.bot.dto.OrderDeliveryResponseDTO(
        c.phone,
        o.orderTransactionDeliveryId,
        NULL,
        o.total,
        p.id,
        p.namePayment,
        ti.id,
        ti.name,
        o.method,
        c.name,
        c.address,
        c.phone,
        c.email,
        c.numerIdentification,
        o.status,
        o.statusOrder
    )
    FROM OrderDetailDelivery o
    LEFT JOIN Customer c ON o.customerId = c.customer_id
    LEFT JOIN TypeIdentification ti ON c.typeIdentificationId = ti.id
    LEFT JOIN Payment p ON o.paymentId = p.id
    WHERE o.status = 'ACTIVE' AND o.statusOrder = 'PENDIENTE'
""")
    List<OrderDeliveryResponseDTO> getOrderDetail();


    @Query("""
    SELECT new com.restaurante.bot.dto.OrderProductDeliveryResponseDTO(
        opd.orderTransactionDeliveryId,
        opd.productId,
        opd.name,
        opd.quantity,
        opd.unitPrice
    )
    FROM OrderProductDelivery opd
    WHERE opd.orderTransactionDeliveryId IN :orderIds
    """)
    List<OrderProductDeliveryResponseDTO> getOrderDetailProduct(@Param("orderIds") List<Long> orderIds);

    OrderDetailDelivery findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);


}
