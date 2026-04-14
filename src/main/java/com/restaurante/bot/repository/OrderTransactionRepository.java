package com.restaurante.bot.repository;

import com.restaurante.bot.model.OrderTransaction;
import com.restaurante.bot.util.OrderStatusConstants;
import com.restaurante.bot.util.TransactionStatusConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for OrderTransaction entity
 * Queries are compatible with both MySQL and Oracle databases
 * Complex result mappings moved to service/adapter layer
 */
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

	// Status constants
	int ORDER_STATUS_CONFIRMED = OrderStatusConstants.CONFIRMED;
	int ORDER_STATUS_SENT = OrderStatusConstants.SENT;
	int ORDER_STATUS_PENDING = OrderStatusConstants.PENDING;
	int ORDER_STATUS_CONFIRMED_ARQ = OrderStatusConstants.CONFIRMED_ARQ;
	int TRANSACTION_STATUS_ACTIVE = TransactionStatusConstants.ACTIVE_INT;

	// SQL Condition constants for reusability
	String CONDITION_ORDER_CONFIRMED_OR_SENT =
		"(o.status = " + ORDER_STATUS_CONFIRMED + " OR o.status = " + ORDER_STATUS_SENT + ")";
	String CONDITION_ORDER_SENT_OR_CONFIRMED_ARQ =
		"(o.status = " + ORDER_STATUS_SENT + " OR o.status = " + ORDER_STATUS_CONFIRMED_ARQ + ")";

	// ========== Basic Queries ==========

	/**
	 * Get total amount for a transaction
	 */
	@Query("SELECT SUM(co.total) FROM OrderTransaction ot, CustomerOrder co " +
			"WHERE ot.orderId = co.orderId AND ot.transactionId = :transactionId AND ot.companyId = :companyId " +
			"GROUP BY ot.transactionId")
	Double getTotalOrderAmount(@Param("transactionId") Long transactionId, @Param("companyId") Long companyId);

	/**
	 * Find order by transaction ID
	 */
	Optional<OrderTransaction> findByOrderId(@Param("orderId") Long orderId);

	// ========== Order Product Queries ==========

	/**
	 * Get order products by table ID
	 */
	@Query("SELECT o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), o.total " +
			"FROM OrderTransaction ot JOIN Transaction t ON ot.transactionId = t.transactionId " +
			"JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"JOIN OrderProduct op ON o.orderId = op.orderId " +
			"JOIN Product p ON op.productId = p.productId " +
			"WHERE t.tableId = :tableId")
	List<Object[]> findOrderProductsByTable(@Param("tableId") int tableId);

	/**
	 * Get orders by phone number
	 */
	@Query("SELECT c.phone, p.productId, p.name, op.quantity, p.price, co.total " +
			"FROM Customer c, Transaction t, OrderTransaction ot, CustomerOrder co, OrderProduct op, Product p " +
			"WHERE c.customer_id = t.customerId " +
			"AND ot.transactionId = t.transactionId " +
			"AND co.orderId = ot.orderId " +
			"AND op.orderId = co.orderId " +
			"AND p.productId = op.productId " +
			"AND c.phone = :phoneNumber")
	List<Object[]> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber);

	/**
	 * Get orders by table number
	 */
	@Query("SELECT c.phone, p.productId, p.name, op.quantity, p.price, co.total " +
			"FROM Customer c, Transaction t, OrderTransaction ot, CustomerOrder co, OrderProduct op, Product p, RestaurantTable r " +
			"WHERE c.customer_id = t.customerId " +
			"AND ot.transactionId = t.transactionId " +
			"AND co.orderId = ot.orderId " +
			"AND op.orderId = co.orderId " +
			"AND p.productId = op.productId " +
			"AND r.tableId = t.tableId " +
			"AND r.tableNumber = :tableNumber")
	List<Object[]> getOrderByTableNumber(@Param("tableNumber") Integer tableNumber);

	// ========== Table Summary Queries ==========

	@Query("SELECT rt.tableNumber FROM RestaurantTable rt ORDER BY rt.tableNumber")
	List<Long> findAllTableNumbers();

	/**
	 * Get all orders grouped by table with status and products
	 */
	@Query("SELECT rt.tableNumber, COALESCE(ts.description, 'Unknown'), COALESCE(SUM(t.transactionTotal), 0), " +
			"o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), t.transactionId, o.date, o.status " +
			"FROM RestaurantTable rt LEFT JOIN Transaction t ON rt.tableId = t.tableId " +
			"LEFT JOIN TransactionStatus ts ON t.status = ts.transactionStatusId " +
			"LEFT JOIN OrderTransaction ot ON t.transactionId = ot.transactionId " +
			"LEFT JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"LEFT JOIN OrderProduct op ON o.orderId = op.orderId " +
			"LEFT JOIN Product p ON op.productId = p.productId " +
			"WHERE o.status IN (" + ORDER_STATUS_CONFIRMED + ", " + ORDER_STATUS_SENT + ", " + ORDER_STATUS_CONFIRMED_ARQ + ") " +
			"AND t.status = " + TRANSACTION_STATUS_ACTIVE)
	List<Object[]> findAllOrdersWithTableJPQL();

	/**
	 * Get all sent orders grouped by table
	 */
	@Query("SELECT rt.tableNumber, ts.description, COALESCE(SUM(t.transactionTotal), 0), " +
			"o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), t.transactionId, o.date " +
			"FROM RestaurantTable rt LEFT JOIN Transaction t ON rt.tableId = t.tableId " +
			"LEFT JOIN TransactionStatus ts ON t.status = ts.transactionStatusId " +
			"LEFT JOIN OrderTransaction ot ON t.transactionId = ot.transactionId " +
			"LEFT JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"LEFT JOIN OrderProduct op ON o.orderId = op.orderId " +
			"LEFT JOIN Product p ON op.productId = p.productId " +
			"WHERE o.status = " + ORDER_STATUS_SENT + " AND t.status = " + TRANSACTION_STATUS_ACTIVE)
	List<Object[]> findAllSentOrdersGroupedByMesa();

	/**
	 * Get sent or confirmed orders by table number
	 */
	@Query("SELECT rt.tableNumber, ts.description, COALESCE(SUM(t.transactionTotal), 0), " +
			"o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), o.total, t.transactionId, o.date " +
			"FROM RestaurantTable rt LEFT JOIN Transaction t ON rt.tableId = t.tableId " +
			"LEFT JOIN TransactionStatus ts ON t.status = ts.transactionStatusId " +
			"LEFT JOIN OrderTransaction ot ON t.transactionId = ot.transactionId " +
			"LEFT JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"LEFT JOIN OrderProduct op ON o.orderId = op.orderId " +
			"LEFT JOIN Product p ON op.productId = p.productId " +
			"WHERE (" + CONDITION_ORDER_SENT_OR_CONFIRMED_ARQ + ") AND rt.tableNumber = :tableNumber AND t.status = " + TRANSACTION_STATUS_ACTIVE)
	List<Object[]> findAllOrdersEnviadasJPQL(@Param("tableNumber") Long tableNumber);

	/**
	 * Get pending orders by table, phone and company
	 */
	@Query("SELECT rt.tableNumber, ts.description, COALESCE(SUM(t.transactionTotal), 0), " +
			"o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), o.total, t.transactionId, o.date " +
			"FROM RestaurantTable rt LEFT JOIN Transaction t ON rt.tableId = t.tableId " +
			"LEFT JOIN TransactionStatus ts ON t.status = ts.transactionStatusId " +
			"LEFT JOIN OrderTransaction ot ON t.transactionId = ot.transactionId " +
			"LEFT JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"LEFT JOIN OrderProduct op ON o.orderId = op.orderId " +
			"LEFT JOIN Product p ON op.productId = p.productId " +
			"LEFT JOIN Customer c ON c.customer_id = o.customerId " +
			"WHERE o.status = " + ORDER_STATUS_PENDING + " AND rt.tableNumber = :tableNumber " +
			"AND t.status = " + TRANSACTION_STATUS_ACTIVE + " AND c.phone = :phoneNumber AND o.companyId = :companyId")
	List<Object[]> findAllOrdersNotConfirmJPQLQuery(@Param("tableNumber") Long tableNumber,
			@Param("companyId") Long companyId, @Param("phoneNumber") String phoneNumber);

	/**
	 * Get confirmed orders by table, phone and company
	 */
	@Query("SELECT rt.tableNumber, ts.description, COALESCE(SUM(t.transactionTotal), 0), " +
			"o.orderId, p.productId, p.name, op.quantity, p.price, (op.quantity * p.price), o.total, t.transactionId, o.date " +
			"FROM RestaurantTable rt LEFT JOIN Transaction t ON rt.tableId = t.tableId " +
			"LEFT JOIN TransactionStatus ts ON t.status = ts.transactionStatusId " +
			"LEFT JOIN OrderTransaction ot ON t.transactionId = ot.transactionId " +
			"LEFT JOIN CustomerOrder o ON ot.orderId = o.orderId " +
			"LEFT JOIN OrderProduct op ON o.orderId = op.orderId " +
			"LEFT JOIN Product p ON op.productId = p.productId " +
			"LEFT JOIN Customer c ON c.customer_id = o.customerId " +
			"WHERE " + CONDITION_ORDER_CONFIRMED_OR_SENT + " AND rt.tableNumber = :tableNumber " +
			"AND t.status = " + TRANSACTION_STATUS_ACTIVE + " AND c.phone = :phoneNumber AND o.companyId = :companyId")
	List<Object[]> findAllOrdersConfirmJPQLQuery(@Param("tableNumber") Long tableNumber,
			@Param("phoneNumber") String phoneNumber, @Param("companyId") Long companyId);

	/**
	 * Get company data for orders
	 */
	@Query("SELECT rt.tableNumber, o.orderId, null, p.name, op.quantity, p.price, op.commentProduct, o.date " +
			"FROM RestaurantTable rt, Transaction t, TransactionStatus ts, OrderTransaction ot, CustomerOrder o, OrderProduct op, Product p " +
			"WHERE rt.tableId = t.tableId AND t.status = ts.transactionStatusId " +
			"AND ot.transactionId = t.transactionId AND o.orderId = ot.orderId " +
			"AND op.orderId = o.orderId AND p.productId = op.productId " +
			"AND " + CONDITION_ORDER_CONFIRMED_OR_SENT + " AND t.status = " + TRANSACTION_STATUS_ACTIVE)
	List<Object[]> findCompanyData(@Param("companyId") Long companyId);

	// ========== Default Methods for backward compatibility ==========

	default List<Object[]> findAllOrdersWithTable() {
		return findAllOrdersWithTableJPQL();
	}

	default List<Object[]> findAllOrdersEnviadas(Long tableNumber) {
		return findAllOrdersEnviadasJPQL(tableNumber);
	}

	default List<Object[]> findAllOrdersNotConfirmJPQL(Long tableNumber, Long companyId, String phoneNumber) {
		return findAllOrdersNotConfirmJPQLQuery(tableNumber, companyId, phoneNumber);
	}

	default List<Object[]> findAllOrdersNotConfirm(Long tableNumber, Long companyId, String phoneNumber) {
		return findAllOrdersNotConfirmJPQL(tableNumber, companyId, phoneNumber);
	}

	default List<Object[]> findAllOrdersConfirmJPQL(Long tableNumber, String phoneNumber, Long companyId) {
		return findAllOrdersConfirmJPQLQuery(tableNumber, phoneNumber, companyId);
	}

}
