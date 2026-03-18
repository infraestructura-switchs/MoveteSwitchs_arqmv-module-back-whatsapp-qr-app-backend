package com.restaurante.bot.repository;

import com.restaurante.bot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        @Query("SELECT t FROM Transaction t, RestaurantTable r " +
                        "WHERE t.tableId = r.tableId AND r.tableNumber = :tableNumber AND t.status = 1 " +
                        "AND t.companyId = :companyId")
        Transaction findTransactionByTableId(@Param("tableNumber") Long tableNumber, @Param("companyId") Long companyId);

        Transaction findByTransactionId(Long id);

        @Query("SELECT DISTINCT t FROM OrderTransaction ot, Transaction t, RestaurantTable r, CustomerOrder co " +
                        "WHERE ot.transactionId = t.transactionId AND ot.orderId = co.orderId AND r.tableId = t.tableId " +
                        "AND r.tableNumber = :tableNumber AND co.status = 1 AND t.companyId = :companyId")
        Transaction getTransactionByTableAndStatus(@Param("tableNumber") Integer tableNumber,
                        @Param("companyId") Long companyId);

        @Query("SELECT DISTINCT t FROM Transaction t, OrderTransaction ot, RestaurantTable r " +
                        "WHERE ot.transactionId = t.transactionId AND r.tableId = t.tableId " +
                        "AND r.tableNumber = :tableNumber AND t.status = 1 AND t.companyId = :companyId")
        Transaction getTransactionByTableAndStatusSend(@Param("tableNumber") Integer tableNumber,
                        @Param("companyId") Long companyId);

        @Query("SELECT DISTINCT t.transactionId FROM Transaction t, OrderTransaction ot, CustomerOrder co, Customer c, RestaurantTable rt " +
                        "WHERE ot.transactionId = t.transactionId AND ot.orderId = co.orderId AND co.customerId = c.customer_id " +
                        "AND rt.tableId = t.tableId AND c.phone = :phoneNumber AND t.status = 1 " +
                        "AND rt.tableNumber = :tableNumber AND t.companyId = :companyId")
        Long getTransactionIdByPhoneNumber(@Param("phoneNumber") String phoneNumber,
                        @Param("tableNumber") Long tableNumber,
                        @Param("companyId") Long companyId);
}
