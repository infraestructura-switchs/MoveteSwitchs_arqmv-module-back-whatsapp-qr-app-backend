package com.restaurante.bot.repository;

import com.restaurante.bot.dto.OrderResponseDTO;
import com.restaurante.bot.dto.OrderDTO;
import com.restaurante.bot.model.OrderTransaction;
import com.restaurante.bot.model.Transaction;
import com.restaurante.bot.util.OrderStatusConstants;
import com.restaurante.bot.util.TransactionStatusConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

        int ORDER_STATUS_CONFIRMED = OrderStatusConstants.CONFIRMED;
        int ORDER_STATUS_SENT = OrderStatusConstants.SENT;
        int ORDER_STATUS_PENDING = OrderStatusConstants.PENDING;
        int ORDER_STATUS_CONFIRMED_ARQ = OrderStatusConstants.CONFIRMED_ARQ;
        int TRANSACTION_STATUS_ACTIVE = TransactionStatusConstants.ACTIVE_INT;

        String CONDITION_ORDER_STATUS_CONFIRMED_OR_SENT =
                        "(o.status = " + ORDER_STATUS_CONFIRMED + " OR o.status = " + ORDER_STATUS_SENT + ")";
        String CONDITION_ORDER_STATUS_SENT_OR_CONFIRMED_ARQ =
                        "(o.status = " + ORDER_STATUS_SENT + " OR o.status = " + ORDER_STATUS_CONFIRMED_ARQ + ")";

        @Query("SELECT SUM(co.total) FROM OrderTransaction ot, CustomerOrder co " +
                        "WHERE ot.orderId = co.orderId AND ot.transactionId = :transactionId AND ot.companyId = :companyId " +
                        "GROUP BY ot.transactionId")
        Double getTotalOrderAmount(Long transactionId, Long companyId);

        @Query(value = "SELECT o.order_id, p.product_id, p.name, op.quantity, p.price, (op.quantity * p.price), o.total " +
                        "FROM order_transaction ot " +
                        "JOIN customer_order o ON ot.order_id = o.order_id " +
                        "JOIN order_product op ON o.order_id = op.order_id " +
                        "JOIN transaction t ON ot.transaction_id = t.transaction_id " +
                        "JOIN product p ON op.product_id = p.product_id " +
                        "WHERE t.table_id = :tableId", nativeQuery = true)
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
                        "LEFT JOIN product p ON op.product_id = p.product_id " +
                        "WHERE o.status IN (" + ORDER_STATUS_CONFIRMED + ", " + ORDER_STATUS_SENT + ", " + ORDER_STATUS_CONFIRMED_ARQ + ") " +
                        "AND t.status = " + TRANSACTION_STATUS_ACTIVE + " " +
                        "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE, o.status "
                        +
                        "ORDER BY rt.table_number, o.order_id ", nativeQuery = true)
        List<Object[]> findAllOrdersWithTableNative();

        @Query("SELECT rt.tableNumber FROM RestaurantTable rt ORDER BY rt.tableNumber")
        List<Long> findAllTableNumbers();

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
                        "LEFT JOIN product p ON op.product_id = p.product_id " +
                        "WHERE o.status = " + ORDER_STATUS_SENT + " " +
                        "AND t.status = " + TRANSACTION_STATUS_ACTIVE + " " +
                        "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE "
                        +
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
                        "LEFT JOIN product p ON op.product_id = p.product_id " +
                        "WHERE " + CONDITION_ORDER_STATUS_SENT_OR_CONFIRMED_ARQ + " " +
                        "AND rt.table_number = :tableNumber " +
                        "AND t.status = " + TRANSACTION_STATUS_ACTIVE + " " +
                        "GROUP BY rt.table_number, ts.description, o.order_id, p.product_id, p.name, op.quantity, p.PRICE, o.total, t.TRANSACTION_ID, o.CUSTOMER_ORDER_DATE "
                        +
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
                        "LEFT JOIN product p ON op.product_id = p.product_id " +
                        "LEFT JOIN customer c ON c.customer_id = o.customer_id " +
                        "WHERE o.status = " + ORDER_STATUS_PENDING + " " +
                        "AND rt.table_number = :tableNumber " +
                        "AND t.status = " + TRANSACTION_STATUS_ACTIVE + " " +
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
        List<Object[]> findAllOrdersNotConfirm(@Param("tableNumber") Long tableNumber,
                        @Param("companyId") Long companyId,
                        @Param("phoneNumber") String phoneNumber);

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
                        "LEFT JOIN customer c ON c.customer_id = o.customer_id " +
                        "LEFT JOIN product p ON op.product_id = p.product_id " +
                        "WHERE " + CONDITION_ORDER_STATUS_CONFIRMED_OR_SENT + " " +
                        "AND rt.table_number = :tableNumber " +
                        "AND t.status = " + TRANSACTION_STATUS_ACTIVE + " " +
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
        List<Object[]> findAllOrdersConfirm(@Param("tableNumber") Long tableNumber,
                        @Param("phoneNumber") String phoneNumber,
                        @Param("companyId") Long companyId);

        OrderTransaction findByOrderId(Long orderId);

        @Query(value = "SELECT c.phone, p.product_id, p.name, op.quantity, p.price, co.total " +
                        "FROM customer c " +
                        "JOIN `transaction` t ON c.customer_id = t.customer_id " +
                        "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
                        "JOIN customer_order co ON co.order_id = ot.order_id " +
                        "JOIN order_product op ON op.order_id = co.order_id " +
                        "JOIN product p ON op.product_id = p.product_id " +
                        "WHERE c.phone = :phoneNumber", nativeQuery = true)
        List<Object[]> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber);

        @Query(value = "SELECT c.phone, p.product_id, p.name, op.quantity, p.price, co.total " +
                        "FROM customer c " +
                        "JOIN `transaction` t ON c.customer_id = t.customer_id " +
                        "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
                        "JOIN customer_order co ON co.order_id = ot.order_id " +
                        "JOIN order_product op ON op.order_id = co.order_id " +
                        "JOIN product p ON op.product_id = p.product_id " +
                        "JOIN restaurant_table r ON r.table_id = t.table_id " +
                        "WHERE r.table_number = :tableNumber", nativeQuery = true)
        List<Object[]> getOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

        @Query("SELECT rt.tableNumber, o.orderId, null, p.name, op.quantity, p.price, op.commentProduct, o.date " +
                        "FROM RestaurantTable rt, Transaction t, TransactionStatus ts, OrderTransaction ot, CustomerOrder o, OrderProduct op, Product p " +
                        "WHERE rt.tableId = t.tableId AND t.status = ts.transactionStatusId " +
                        "AND ot.transactionId = t.transactionId AND o.orderId = ot.orderId " +
                        "AND op.orderId = o.orderId AND str(p.productId) = op.productId " +
                        "AND " + CONDITION_ORDER_STATUS_CONFIRMED_OR_SENT + " AND t.status = " + TRANSACTION_STATUS_ACTIVE)
        List<Object[]> findCompanyData(@Param("companyId") Long companyId);

        default List<OrderResponseDTO> findAllOrdersWithTableDTO() {
                List<Object[]> rows = findAllOrdersWithTableNative();
                Map<Long, OrderResponseDTO> grouped = new LinkedHashMap<>();

                if (rows == null) return new ArrayList<>();

                for (Object[] r : rows) {
                        if (r == null) continue;

                        Integer mesa = r[0] != null ? ((Number) r[0]).intValue() : 0;
                        // r[1] => status_description (String)
                        Double totalGeneral = r[2] != null ? ((Number) r[2]).doubleValue() : 0.0;
                        Long orderId = r[3] != null ? ((Number) r[3]).longValue() : null;
                        String productId = r[4] != null ? String.valueOf(r[4]) : null;
                        String productName = r[5] != null ? String.valueOf(r[5]) : null;
                        int qty = r[6] != null ? ((Number) r[6]).intValue() : 0;
                        Double unitPrice = r[7] != null ? ((Number) r[7]).doubleValue() : 0.0;
                        Double totalPrice = r[8] != null ? ((Number) r[8]).doubleValue() : 0.0;
                        Long transactionId = r[9] != null ? ((Number) r[9]).longValue() : null;
                        String date = r.length > 10 && r[10] != null ? String.valueOf(r[10]) : null;
                        Integer statusMesa = r.length > 11 && r[11] != null ? ((Number) r[11]).intValue() : null;

                        OrderDTO orderItem = new OrderDTO(orderId, productId, productName, qty, unitPrice, totalPrice, date);

                        Long key = orderId != null ? orderId : (transactionId != null ? transactionId : Long.valueOf(grouped.size()+1));

                        OrderResponseDTO resp = grouped.get(key);
                        if (resp == null) {
                                resp = new OrderResponseDTO();
                                resp.setMesa(mesa);
                                resp.setStatusMesa(statusMesa != null ? statusMesa : 0);
                                resp.setTotalGeneral(totalGeneral);
                                resp.setTransactionId(transactionId);
                                resp.setOrders(new ArrayList<>());
                                grouped.put(key, resp);
                        }

                        resp.getOrders().add(orderItem);
                }

                return new ArrayList<>(grouped.values());
        }

}
