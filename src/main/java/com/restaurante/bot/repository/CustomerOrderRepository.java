package com.restaurante.bot.repository;

import com.restaurante.bot.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Query(value = "SELECT co.* " +
            "FROM customer_order co " +
            "JOIN order_transaction ot ON co.order_id = ot.order_id " +
            "WHERE ot.transaction_id = :transactionId AND co.status = 3", nativeQuery = true)
    List<CustomerOrder> findByTransactionIdAndStatusNoConfirm(Long transactionId);


    CustomerOrder findByOrderIdAndCompanyId(Long orderId, Long companyId);

}
