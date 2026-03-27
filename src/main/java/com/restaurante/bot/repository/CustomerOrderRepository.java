package com.restaurante.bot.repository;

import com.restaurante.bot.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

        @Query("SELECT co FROM CustomerOrder co, OrderTransaction ot " +
            "WHERE co.orderId = ot.orderId AND ot.transactionId = :transactionId AND co.status = 3")
        List<CustomerOrder> findByTransactionIdAndStatusNoConfirm(@org.springframework.data.repository.query.Param("transactionId") Long transactionId);

            @Query("SELECT DISTINCT co FROM CustomerOrder co, OrderTransaction ot " +
                "WHERE co.orderId = ot.orderId AND ot.transactionId IN :transactionIds AND co.status = 3")
            List<CustomerOrder> findByTransactionIdsAndStatusNoConfirm(@org.springframework.data.repository.query.Param("transactionIds") List<Long> transactionIds);


    CustomerOrder findByOrderIdAndCompanyId(Long orderId, Long companyId);

}
