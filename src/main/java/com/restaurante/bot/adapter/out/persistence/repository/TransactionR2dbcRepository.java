package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionR2dbcRepository extends ReactiveCrudRepository<TransactionEntity, Long> {

    Mono<TransactionEntity> findByTransactionId(Long id);

    @Query("""
            SELECT t.* FROM transaction t
            JOIN restaurant_table r ON t.table_id = r.table_id
            WHERE r.table_number = :tableNumber AND t.status = 1
            AND t.company_id = :companyId LIMIT 1
            """)
    Mono<TransactionEntity> findTransactionByTableId(Long tableNumber, Long companyId);

    @Query("""
            SELECT DISTINCT t.* FROM order_transaction ot
            JOIN transaction t ON ot.transaction_id = t.transaction_id
            JOIN restaurant_table r ON r.table_id = t.table_id
            JOIN customer_order co ON co.order_id = ot.order_id
            WHERE r.table_number = :tableNumber AND co.status = 1 AND t.company_id = :companyId
            LIMIT 1
            """)
    Mono<TransactionEntity> getTransactionByTableAndStatus(Integer tableNumber, Long companyId);

    @Query("""
            SELECT DISTINCT t.* FROM transaction t
            JOIN order_transaction ot ON ot.transaction_id = t.transaction_id
            JOIN restaurant_table r ON r.table_id = t.table_id
            WHERE r.table_number = :tableNumber AND t.status = 1 AND t.company_id = :companyId
            LIMIT 1
            """)
    Mono<TransactionEntity> getTransactionByTableAndStatusSend(Integer tableNumber, Long companyId);

    @Query("""
            SELECT DISTINCT t.transaction_id FROM transaction t
            JOIN order_transaction ot ON ot.transaction_id = t.transaction_id
            JOIN customer_order co ON ot.order_id = co.order_id
            JOIN customer c ON co.customer_id = c.customer_id
            JOIN restaurant_table rt ON t.table_id = rt.table_id
            WHERE c.phone = :phoneNumber AND t.status = 1
            AND rt.table_number = :tableNumber AND t.company_id = :companyId
            LIMIT 1
            """)
    Mono<Long> getTransactionIdByPhoneNumber(String phoneNumber, Long tableNumber, Long companyId);
}
