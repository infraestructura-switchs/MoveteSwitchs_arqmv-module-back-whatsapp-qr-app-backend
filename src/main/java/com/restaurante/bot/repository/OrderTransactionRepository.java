package com.restaurante.bot.repository;

import com.restaurante.bot.dto.OrderResponseDTO;
import com.restaurante.bot.model.OrderTransaction;
import com.restaurante.bot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    @Query(value = "SELECT " +
                   "SUM(co.total) AS total_order_amount " +
                   "FROM " +
                   "order_transaction ot " +
                   "JOIN " +
                   "customer_order co ON ot.order_id = co.order_id " +
                   "WHERE " +
                   "ot.transaction_id = :transactionId " +
                    "AND ot.COMPANY_ID = :companyId " +
                   "GROUP BY " +
                   "ot.transaction_id " , nativeQuery = true)
    Double getTotalOrderAmount(Long transactionId, Long companyId);

    @Query(value = "SELECT o.orderId AS orderId, op.productId AS productId, op.name AS productName, " +
            "op.quantity AS qty, op.unitePrice AS unitePrice, (op.quantity * op.unitePrice) AS totalPrice, " +
            "o.total AS subTotal " +
            "FROM OrderTransaction ot " +
            "JOIN CustomerOrder o ON ot.orderId = o.orderId " +
            "JOIN OrderProduct op ON o.orderId = op.orderId " +
            "JOIN Transaction t ON ot.transactionId = t.transactionId " +
            "WHERE t.tableId = :tableId", nativeQuery = true)
    List<Object[]> findOrderProductsByTable(@Param("tableId") int tableId);


    @Query(value = "SELECT rt.table_number, " +
            "COALESCE(ts.description, 'Unknown') AS status_description, " +
            "COALESCE(SUM(t.transaction_total), 0) AS total_general, " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "(op.quantity * p.PRICE) AS product_total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE, " +
            "o.status " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE o.status IN (1, 2, 5) " +
            "AND t.status = 1 " +
            "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE, o.status " +
            "ORDER BY rt.table_number, o.order_id ", nativeQuery = true)
    List<Object[]> findAllOrdersWithTableNative();

    @Query(value = "SELECT table_number,  FROM restaurant_table ORDER BY table_number", nativeQuery = true)
    List<Integer> findAllTableNumbers();

    @Query(value = "SELECT rt.table_number, " +
            "ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "(op.quantity * p.PRICE) AS product_total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE o.status = 2 " +
            "AND t.status = 1 " +
            "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE " +
            "ORDER BY rt.table_number, o.order_id ", nativeQuery = true)
    List<Object[]> findAllSentOrdersGroupedByMesaNative();



    @Query(value = "SELECT rt.table_number, " +
            "ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0) AS total_transaction, " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "(op.quantity * p.PRICE) AS product_total, " +
            "o.total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE (o.status = 2 OR o.status = 5) " +
            "AND rt.table_number = :tableNumber " +
            "AND t.status = 1 " +
            "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE " +
            "ORDER BY rt.table_number, o.order_id", nativeQuery = true)
    List<Object[]> findAllOrdersEnviadas(@Param("tableNumber") Long tableNumber);

    @Query(value = "SELECT " +
            "rt.table_number, " +
            "ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "(op.quantity * p.PRICE) AS product_total, " +
            "o.total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "LEFT JOIN customer c ON c.customer_id = o.CUSTOMER_ID " +
            "WHERE o.STATUS = 3 " +
            "AND rt.table_number = :tableNumber " +
            "AND t.STATUS = 1 " +
            "AND c.phone = :phoneNumber " +
            "AND o.company_id  = :companyId " +
            "GROUP BY " +
            "rt.table_number, " +
            "ts.description, " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "o.total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "ORDER BY rt.table_number, o.order_id", nativeQuery = true)
    List<Object[]> findAllOrdersNotConfirm(@Param("tableNumber")Long tableNumber,
                                           @Param("companyId")Long companyId,
                                           @Param("phoneNumber")String phoneNumber);

    @Query(value = "SELECT " +
            "rt.table_number, " +
            "ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "(op.quantity * p.PRICE) AS product_total, " +
            "o.total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN customer c ON c.customer_id = o.CUSTOMER_ID " +
            "LEFT JOIN PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE (o.STATUS = 1 OR o.STATUS = 2) " +
            "AND rt.table_number = :tableNumber " +
            "AND t.STATUS = 1 " +
            "AND c.phone = :phoneNumber " +
            "AND o.company_id  = :companyId " +
            "GROUP BY " +
            "rt.table_number, " +
            "ts.description, " +
            "o.order_id, " +
            "p.product_id, " +
            "p.name, " +
            "op.quantity, " +
            "p.PRICE, " +
            "o.total, " +
            "t.TRANSACTION_ID, " +
            "o.CUSTOMER_ORDER_DATE " +
            "ORDER BY rt.table_number, o.order_id", nativeQuery = true)
    List<Object[]> findAllOrdersConfirm(@Param("tableNumber")Long tableNumber,
                                           @Param("phoneNumber")String phoneNumber,
                                        @Param("companyId")Long companyId);


    OrderTransaction findByOrderId(Long orderId);

    @Query(value = "SELECT " +
            "c.phone, " +
            "op.product_id, " +
            "op.name AS product_name, " +
            "op.quantity AS qty, " +
            "op.unite_price AS price, " +
            "co.total " +
            "FROM  customer c " +
            "JOIN `transaction` t ON c.customer_id = t.customer_id " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "JOIN order_product op ON op.order_id = co.order_id " +
            "WHERE c.phone = :phoneNumber ", nativeQuery = true)
    List<Object[]> getOrderByPhoneNumber(@Param("phoneNumber")String phoneNumber);


    @Query(value = "SELECT " +
            "c.phone, " +
            "op.product_id, " +
            "op.name AS product_name, " +
            "op.quantity AS qty, " +
            "op.unite_price AS price, " +
            "co.total " +
            "FROM  customer c " +
            "JOIN `transaction` t ON c.customer_id = t.customer_id " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "JOIN order_product op ON op.order_id = co.order_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "WHERE  r.table_number = :tableNumber ", nativeQuery = true)
    List<Object[]> getOrderByTableNumber(@Param("tableNumber")Integer tableNumber);


    @Query(value = "SELECT " +
            "    rt.table_number, " +
            "    o.order_id, " +
            "    p.SOFT_RESTAURANT_ID, " +
            "    p.name, " +
            "    op.quantity, " +
            "    p.PRICE, " +
            "    op.COMMENT_PRODUCT, " +
            "    o.CUSTOMER_ORDER_DATE " +
            "FROM " +
            "    restaurant_table rt " +
            "LEFT JOIN " +
            "    transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN " +
            "    transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN " +
            "    order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN " +
            "    customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN " +
            "    order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN " +
            "    PRODUCT p ON op.PRODUCT_ID = p.PRODUCT_ID " +
            "WHERE " +
            "    (o.STATUS = 1 OR o.STATUS = 2) " +
            "    AND t.STATUS = 1 ", nativeQuery = true)
    List<Object[]> findCompanyData(@Param("companyId") Long companyId);

}
