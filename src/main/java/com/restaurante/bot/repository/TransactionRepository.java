package com.restaurante.bot.repository;

import com.restaurante.bot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT t.* " +
            "FROM transaction t " +
            "JOIN restaurant_table r ON t.table_id = r.table_id " +
            "WHERE r.table_number = :tableNumber AND t.status = 1 " +
            "AND t.company_id = :companyId ", nativeQuery = true)
    Transaction findTransactionByTableId(Long tableNumber, Long companyId);

    Transaction findByTransactionId(Long id);

    @Query(value = "SELECT DISTINCT t.* " +
            "FROM order_transaction ot " +
            "JOIN transaction t ON ot.transaction_id = t.transaction_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "WHERE r.table_number = :tableNumber AND co.status = 1 AND t.company_id = :companyId", nativeQuery = true)
    Transaction getTransactionByTableAndStatus(@Param("tableNumber")Integer tableNumber, @Param("companyId") Long companyId);

    @Query(value = "SELECT DISTINCT t.* FROM transaction t " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "WHERE r.table_number = :tableNumber AND t.STATUS = 1 AND t.company_id = :companyId ", nativeQuery = true)
    Transaction getTransactionByTableAndStatusSend(@Param("tableNumber") Integer tableNumber, @Param("companyId") Long companyId);

    @Query(value = "SELECT DISTINCT  t.transaction_id " +
            "FROM transaction t " +
            "JOIN ORDER_TRANSACTION ot ON ot.TRANSACTION_ID = t.TRANSACTION_ID " +
            "JOIN CUSTOMER_ORDER co ON ot.ORDER_ID  = co.ORDER_ID " +
            "JOIN customer c ON co.CUSTOMER_ID  = c.CUSTOMER_ID " +
            "JOIN RESTAURANT_TABLE rt ON t.TABLE_ID = rt.TABLE_ID " +
            "WHERE c.phone = :phoneNumber AND t.status = 1 AND rt.TABLE_NUMBER = :tableNumber AND t.COMPANY_ID = :companyId ", nativeQuery = true)
    Long getTransactionIdByPhoneNumber(@Param("phoneNumber")String phoneNumber,
                                       @Param("tableNumber")Long tableNumber,
                                       @Param("companyId")Long companyId);
}
