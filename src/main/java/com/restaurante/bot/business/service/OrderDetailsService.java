package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.OrderUseCase;
import com.restaurante.bot.business.interfaces.OrderInterface;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailsService implements OrderInterface, OrderUseCase {

    private static final long RESPONSE_OK = 200L;
    private static final long RESPONSE_NOT_FOUND = 404L;

    private static final long TRANSACTION_STATUS_ACTIVE = 1L;
    private static final long TRANSACTION_STATUS_CLOSED = 2L;
    private static final int TABLE_STATUS_AVAILABLE = 2;
    private static final int TABLE_STATUS_DEFAULT = 1;

    private static final int ORDER_STATUS_PENDING = 3;
    private static final int ORDER_STATUS_CONFIRMED = 1;
    private static final int ORDER_STATUS_SENT = 2;
    private static final int ORDER_STATUS_CANCELLED = 4;
    private static final int ORDER_STATUS_CONFIRMED_ARQ = 5;

    private static final int MAX_PHONE_LENGTH = 20;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{7,20}$");

    private final RestaurantTableRepository restaurantTableRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final HistoryRepository historyRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final TransactionClientRespository transactionClientRespository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    @Transactional
    public CustomerOrder saveCustomerOrder(Double total, String phone, Long companyId) {
        Customer customer = findOrCreateCustomer(phone);
        CustomerOrder newOrder = new CustomerOrder();
        newOrder.setDate(LocalDateTime.now());
        newOrder.setTotal(total);
        newOrder.setStatus(ORDER_STATUS_PENDING);
        newOrder.setCustomerId(customer.getCustomer_id());
        newOrder.setCompanyId(companyId);
        return customerOrderRepository.save(newOrder);
    }

    @Transactional
    public RestaurantTable findTableByNumber(Long tableNumber, Long companyId) {

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber, companyId);
        if (table == null) {
            log.warn("findTableByNumber - mesa no encontrada, tableNumber={}, companyId={}", tableNumber, companyId);
            throw new GenericException("Mesa no encontrada", HttpStatus.BAD_REQUEST);
        }
        return table;
    }

    @Transactional
    public void saveOrderProducts(List<ItemRequest> items, Long orderId, Long companyId) {
        for (ItemRequest item : items) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderId(orderId);
            orderProduct.setProductId(item.getProductId());
            orderProduct.setQuantity(item.getQty());
            orderProduct.setUnitPrice(item.getUnitPrice());
            orderProduct.setCommentProduct(item.getComment());
            orderProduct.setCompanyId(companyId);

            orderProductRepository.save(orderProduct);
        }
    }

    private void validateOrderRequest(OrderDetailsDTO orderDetailsDTO) {
        if (orderDetailsDTO == null) {
            log.warn("validateOrderRequest - request body es nulo");
            throw new GenericException("Request body is missing", HttpStatus.BAD_REQUEST);
        }

        List<ItemRequest> items = orderDetailsDTO.getItems();
        if (items == null || items.isEmpty()) {
            log.warn("validateOrderRequest - la orden no contiene items, phone={}", orderDetailsDTO.getPhone());
            throw new GenericException("La orden debe contener al menos un item", HttpStatus.BAD_REQUEST);
        }

        List<String> missing = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ItemRequest it = items.get(i);
            String base = "items[" + i + "]";
            if (it.getProductId() == null || it.getProductId().isEmpty()) missing.add(base + ".productId");
            if (it.getQty() == null) missing.add(base + ".qty");
            if (it.getUnitPrice() == null) missing.add(base + ".unitPrice");
        }

        if (!missing.isEmpty()) {
            log.warn("validateOrderRequest - campos obligatorios faltantes: {}", String.join(", ", missing));
            throw new GenericException("Campos obligatorios faltantes: " + String.join(", ", missing), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public Transaction createTransaction(OrderDetailsDTO orderDetailsDTO, RestaurantTable table, Long companyId) {
        Transaction transaction = transactionRepository.findTransactionByTableId(orderDetailsDTO.getRestaurantTable(),
                companyId);
        if (transaction == null) {

            transaction = new Transaction();
            // transaction.setCustomerId(customer.getCustomer_id());
            transaction.setTableId(table.getTableId());
            transaction.setStatus(TRANSACTION_STATUS_ACTIVE);
            transaction.setCompanyId(companyId);
            Transaction newTransaction = transactionRepository.save(transaction);

        }

        return transaction;
    }

    @Transactional
    public Customer findOrCreateCustomer(String phone) {
        String normalizedPhone = normalizeAndValidatePhone(phone);
        Customer customer = customerRepository.findByPhone(normalizedPhone);
        if (customer == null) {
            log.info("findOrCreateCustomer - cliente no encontrado, creando nuevo registro para phone='{}'", normalizedPhone);
            customer = new Customer();
            customer.setPhone(normalizedPhone);
            customerRepository.save(customer);
            log.info("findOrCreateCustomer - nuevo cliente creado, customerId={}", customer.getCustomer_id());
        } else {
            log.debug("findOrCreateCustomer - cliente existente encontrado, customerId={}", customer.getCustomer_id());
        }
        return customer;
    }

    private String normalizeAndValidatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            log.warn("normalizeAndValidatePhone - phone nulo o vacío");
            throw new GenericException("El campo phone es obligatorio", HttpStatus.BAD_REQUEST);
        }

        String normalizedPhone = phone.trim().replace(" ", "").replace("-", "");
        if (normalizedPhone.length() > MAX_PHONE_LENGTH || !PHONE_PATTERN.matcher(normalizedPhone).matches()) {
            log.warn("normalizeAndValidatePhone - formato inválido, phone='{}', longitud={}",
                    phone.length() > 30 ? phone.substring(0, 30) + "[truncado]" : phone, normalizedPhone.length());
            throw new GenericException("El campo phone debe contener un numero de telefono valido", HttpStatus.BAD_REQUEST);
        }

        log.debug("normalizeAndValidatePhone - phone normalizado='{}'", normalizedPhone);
        return normalizedPhone;
    }

    @Transactional
    public OrderTransaction createOrderTransaction(Long orderId, Long transactionId, Long companyId) {
        OrderTransaction orderTransaction = new OrderTransaction();
        orderTransaction.setOrderId(orderId);
        orderTransaction.setTransactionId(transactionId);
        orderTransaction.setCompanyId(companyId);

        orderTransactionRepository.save(orderTransaction);
        return orderTransaction;
    }

    @Transactional
    public void updateTransactionTotal(Transaction transaction, Long companyId) {
        transaction.setTransactionTotal(
                orderTransactionRepository.getTotalOrderAmount(transaction.getTransactionId(), companyId));
        transactionRepository.save(transaction);
    }

    @Transactional
    public void saveTransactionHistory(Transaction transaction) {
        History history = new History();
        history.setTransactionId(transaction.getTransactionId());
        history.setDate(LocalDateTime.now());
        historyRepository.save(history);
    }

    private Long getAuthenticatedCompanyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long tokenCompanyId)) {
            log.warn("getAuthenticatedCompanyId - autenticación ausente o principal inválido, principal={}",
                    authentication != null ? authentication.getPrincipal() : "null");
            throw new GenericException("No autenticado", HttpStatus.UNAUTHORIZED);
        }
        return tokenCompanyId;
    }

    @Override
    @Transactional
    public GenericResponse saveOrder(OrderDetailsDTO orderDetailsDTO) {

        log.info("saveOrder - start, externalCompanyId(body)={}, phone={}, itemsCount={}, total={}, table={}",
                orderDetailsDTO != null ? orderDetailsDTO.getExternalCompanyId() : null,
                orderDetailsDTO != null ? orderDetailsDTO.getPhone() : null,
                orderDetailsDTO != null && orderDetailsDTO.getItems() != null ? orderDetailsDTO.getItems().size() : null,
                orderDetailsDTO != null ? orderDetailsDTO.getTotal() : null,
                orderDetailsDTO != null ? orderDetailsDTO.getRestaurantTable() : null);

        Long tokenCompanyId = getAuthenticatedCompanyId();
        log.info("saveOrder - authenticated externalCompanyId from token={}", tokenCompanyId);

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            log.warn("saveOrder - company not found by externalCompanyId={}", tokenCompanyId);
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        log.info("saveOrder - resolved internal companyId={} for externalCompanyId={}", company.getId(), tokenCompanyId);
        // Verifica la validez del request (items, campos requeridos)
        validateOrderRequest(orderDetailsDTO);
        log.debug("saveOrder - request validation passed");

        // Verifica la mesa
        RestaurantTable table = findTableByNumber(orderDetailsDTO.getRestaurantTable(), company.getId());
        log.info("saveOrder - table found, tableId={}, status={}", table.getTableId(), table.getStatus());

        // Verifica si la mesa está disponible
        if (table.getStatus() != null && table.getStatus() == TABLE_STATUS_AVAILABLE) {
            log.info("saveOrder - table is available, proceeding with order creation");

            // Guarda la orden
            CustomerOrder setOrder = saveCustomerOrder(orderDetailsDTO.getTotal(), orderDetailsDTO.getPhone(),
                    company.getId());
            log.info("saveOrder - customer order created, orderId={}", setOrder.getOrderId());

            // Guarda los productos de la orden
            saveOrderProducts(orderDetailsDTO.getItems(), setOrder.getOrderId(), company.getId());
            log.info("saveOrder - order products saved, orderId={}, itemsCount={}",
                    setOrder.getOrderId(), orderDetailsDTO.getItems().size());

            // Crea o recupera la transacción
            Transaction transaction = createTransaction(orderDetailsDTO, table, company.getId());
            log.info("saveOrder - transaction resolved, transactionId={}", transaction.getTransactionId());

            // Crea la relación entre orden y transacción
            OrderTransaction orderTransaction = createOrderTransaction(setOrder.getOrderId(),
                    transaction.getTransactionId(), company.getId());
                log.debug("saveOrder - orderTransaction created, orderTransactionId={}", orderTransaction.getOrderTransactionId());

            // Actualiza el total de la transacción
            updateTransactionTotal(transaction, company.getId());
            log.debug("saveOrder - transaction total updated, transactionId={}", transaction.getTransactionId());

            // Guarda el historial de la transacción
            saveTransactionHistory(transaction);
            log.debug("saveOrder - transaction history saved, transactionId={}", transaction.getTransactionId());

            log.info("saveOrder - success, orderId={}, transactionId={}",
                    setOrder.getOrderId(), transaction.getTransactionId());

            return new GenericResponse("Transacción guardada con éxito", RESPONSE_OK);
        }

        log.warn("saveOrder - invalid table status for tableId={}, status={}", table.getTableId(), table.getStatus());
        throw new GenericException("Mesa en estado inválido", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<OrderResponseAdminDTO> getOrders() {

        Long tokenCompanyId = getAuthenticatedCompanyId();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        // Obtener todas las mesas
        List<RestaurantTable> allTables = restaurantTableRepository.findAllTablesAsc(company.getId());

        // List<Integer> allTableNumbers =
        // orderTransactionRepository.findAllTableNumbers();

        // Obtener todas las órdenes
        List<Object[]> orderResults = orderTransactionRepository.findAllOrdersWithTableNative();

        // Mapa para almacenar DTOs por mesa
        Map<Integer, OrderResponseAdminDTO> mesaMap = new HashMap<>();

        for (RestaurantTable table : allTables) {
            Integer tableNumber = table.getTableNumber().intValue();
            Integer tableStatus = (table.getStatus() != null) ? table.getStatus().intValue() : TABLE_STATUS_DEFAULT; // Estado por defecto
                                                                                                  // 1 si es null
            mesaMap.put(tableNumber, new OrderResponseAdminDTO(tableNumber, tableStatus, new ArrayList<>(),
                    new ArrayList<>(), 0.0, null));
        }

        // Procesar las órdenes
        for (Object[] result : orderResults) {
            Integer mesaId = (result[0] != null) ? ((Number) result[0]).intValue() : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown";
            Double totalGeneral = (result[2] != null) ? ((Number) result[2]).doubleValue() : 0.0;
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null;
            Integer productId = (result[4] != null) ? ((Number) result[4]).intValue() : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? ((Number) result[6]).intValue() : 0;
            Double unitPrice = (result[7] != null) ? ((Number) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((Number) result[8]).doubleValue() : 0.0;
            Double transactionId = (result[9] != null) ? ((Number) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;
            Integer orderStatus = (result[11] != null) ? ((Number) result[11]).intValue() : null;

            Long transactionIdL = transactionId.longValue();

            // Obtener o crear DTO para esta mesa
            OrderResponseAdminDTO dto = mesaMap.computeIfAbsent(mesaId,
                    k -> new OrderResponseAdminDTO(mesaId, Integer.parseInt(statusMesa),
                            new ArrayList<>(), new ArrayList<>(), totalGeneral, transactionIdL));

            // Actualizar totalGeneral y transactionId si no están establecidos
            if (dto.getTotalGeneral() == 0.0) {
                dto.setTotalGeneral(totalGeneral);
            }
            if (dto.getTransactionId() == null) {
                dto.setTransactionId(transactionIdL);
            }

            // Agregar a la lista correspondiente según el estado de la orden
                OrderDTO orderDTO = new OrderDTO(orderId, productId != null ? productId.toString() : null,
                    productName, qty, unitPrice, totalPrice, date);
            if (orderStatus != null && orderStatus == ORDER_STATUS_SENT) {
                dto.getSentOrders().add(orderDTO);
            } else if (orderStatus != null && (orderStatus == ORDER_STATUS_CONFIRMED || orderStatus == ORDER_STATUS_CONFIRMED_ARQ)) {
                dto.getOrders().add(orderDTO);
            }
        }

        return new ArrayList<>(mesaMap.values());
    }

    public void updateTotalTransaction(Long orderId) {
        /*
         * 
         * OrderTransaction orderTransaction = orderTransactionRepository
         * .findByOrderId(Long
         * .parseLong(orderId
         * .toString()));
         * 
         * Transaction transaction = transactionRepository
         * .findByTransactionId(orderTransaction
         * .getTransactionId());
         * 
         * transaction
         * .setTransactionTotal(orderTransactionRepository
         * .getTotalOrderAmount(transaction
         * .getTransactionId()));
         * 
         * transactionRepository.save(transaction);
         * 
         */
    }

    @Override
    public GenericResponse sendOrderStatus(OrdersIdsDTO orderIds) {
        log.info("sendOrderStatus - start, ordersCount={}", orderIds.getOrdersIds().size());

        for (Long orderId : orderIds.getOrdersIds()) {
            CustomerOrder order = customerOrderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.warn("sendOrderStatus - orden no encontrada, orderId={}", orderId);
                        return new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST);
                    });
            order.setStatus(ORDER_STATUS_SENT);
            customerOrderRepository.save(order);
            log.debug("sendOrderStatus - orden actualizada a enviada, orderId={}", orderId);
        }

        log.info("sendOrderStatus - success, {} ordenes actualizadas", orderIds.getOrdersIds().size());
        return new GenericResponse("Ordenes cambiadas a estado enviada con exito", RESPONSE_OK);
    }
    /*
     * @Override
     * public List<OrderResponseDTO> getSendOrder(Long tableNumber) {
     * List<Object[]> resultList =
     * orderTransactionRepository.findAllOrdersEnviadas(tableNumber);
     * 
     * Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();
     * 
     * for (Object[] result : resultList) {
     * // Manejo de posibles nulos
     * Integer mesaId = (result[0] != null) ? ((Long) result[0]).intValue() : null;
     * // Se realiza el cast a Long antes de convertirlo a Integer
     * String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; //
     * "Unknown" si el estado es nulo
     * Double totalGeneral = (result[2] != null) ? ((BigDecimal)
     * result[2]).doubleValue() : 0.0; // BigDecimal a Double
     * Long orderId = (result[3] != null) ? ((Long) result[3]).longValue() : null;
     * // Aseguramos que orderId es Integer
     * Long productId = (result[4] != null) ? ((BigDecimal) result[4]).longValue() :
     * null; // Convertir el tipo Long a Integer
     * String productName = (result[5] != null) ? (String) result[5] :
     * "Unknown Product"; // Nombre del producto
     * Integer qty = (result[6] != null) ? ((Long) result[6]).intValue() : 0; //
     * Convertir Long a Integer para la cantidad
     * Double unitePrice = (result[7] != null) ? ((BigDecimal)
     * result[7]).doubleValue() : 0.0; // Convertir BigDecimal a Double
     * Double totalPrice = (result[8] != null) ? ((BigDecimal)
     * result[8]).doubleValue() : 0.0; // Convertir BigDecimal a Double
     * Double transactionId = (result[9] != null) ? ((BigDecimal)
     * result[9]).doubleValue() : 0.0;
     * String date = (result[10] != null) ? result[10].toString() : null;
     * 
     * Long transactionIdL = transactionId.longValue();
     * 
     * // Verificar si la mesa ya existe en el mapa
     * OrderResponseDTO dto = mesaMap.get(mesaId);
     * if (dto == null) {
     * dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new
     * ArrayList<>(), totalGeneral, transactionIdL);
     * mesaMap.put(mesaId, dto);
     * }
     * 
     * // Agregar la orden a la mesa
     * dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName,
     * qty, unitePrice, totalPrice, date));
     * }
     * 
     * // Devolver el resultado
     * return new ArrayList<>(mesaMap.values());
     * 
     * }
     * 
     */

    @Override
    public CustomerOrderResponseDTO getOrederByPhoneNumber(String phoneNumber) {

        List<Object[]> rows = orderTransactionRepository.getOrderByPhoneNumber(phoneNumber);

        if (rows.isEmpty())
            return null;

        List<OrderItemDTO> items = new ArrayList<>();
        String customerPhone = null;
        Double total = null;

        for (Object[] row : rows) {

            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(((Number) row[1]).longValue());
            item.setName((String) row[2]);
            item.setQty(((Number) row[3]).intValue());
            item.setPrice(((Number) row[4]).doubleValue());
            items.add(item);

            customerPhone = (String) row[0];
            total = ((Number) row[5]).doubleValue();
        }
        CustomerOrderResponseDTO response = new CustomerOrderResponseDTO();
        response.setPhone(customerPhone);
        response.setItems(items);
        response.setTotal(total);

        return response;
    }

    @Override
    public CustomerOrderResponseDTO getOrederByTableNumber(Integer tableNumber) {
        List<Object[]> rows = orderTransactionRepository.getOrderByTableNumber(tableNumber);

        if (rows.isEmpty())
            return null;

        List<OrderItemDTO> items = new ArrayList<>();
        String customerPhone = null;
        Double total = null;

        for (Object[] row : rows) {

            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(((Number) row[1]).longValue());
            item.setName((String) row[2]);
            item.setQty(((Number) row[3]).intValue());
            item.setPrice(((Number) row[4]).doubleValue());
            items.add(item);

            customerPhone = (String) row[0];
            total = ((Number) row[5]).doubleValue();
        }
        CustomerOrderResponseDTO response = new CustomerOrderResponseDTO();
        response.setPhone(customerPhone);
        response.setItems(items);
        response.setTotal(total);

        return response;
    }

    @Override
    public GenericResponse confirmationOrder(String phoneNumber, Boolean isConfirmed, Long tableNumber) {
        log.info("confirmationOrder - start, phone={}, tableNumber={}, isConfirmed={}", phoneNumber, tableNumber, isConfirmed);

        Long tokenCompanyId = getAuthenticatedCompanyId();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            log.warn("confirmationOrder - compañia no encontrada, externalCompanyId={}", tokenCompanyId);
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        log.debug("confirmationOrder - companyId={} resuelto para externalCompanyId={}", company.getId(), tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());
        if (user == null) {
            log.warn("confirmationOrder - usuario no encontrado para companyId={}", company.getId());
        }
        Subscription subscription = null;
        if (user != null) {
            subscription = subscriptionRepository.findByUserId(user.getUserId());
            if (subscription == null) {
                log.warn("confirmationOrder - suscripción no encontrada para userId={}", user.getUserId());
            }
        }

        List<Long> transactionIds = transactionRepository.getTransactionIdsByPhoneNumber(phoneNumber, tableNumber,
                company.getId());
        log.debug("confirmationOrder - transactionIds encontrados={}", transactionIds);

        if (transactionIds == null || transactionIds.isEmpty()) {
            log.warn("confirmationOrder - no se encontraron transacciones para phone={}, tableNumber={}, companyId={}",
                    phoneNumber, tableNumber, company.getId());
            return new GenericResponse("No se encontraron órdenes para esta transacción", RESPONSE_NOT_FOUND);
        }

        List<CustomerOrder> customerOrders = customerOrderRepository
            .findByTransactionIdsAndStatusNoConfirm(transactionIds);
        log.debug("confirmationOrder - órdenes pendientes encontradas={}", customerOrders.size());

        if (customerOrders.isEmpty()) {
            log.warn("confirmationOrder - no hay órdenes pendientes para transactionIds={}", transactionIds);
            return new GenericResponse("No se encontraron órdenes para esta transacción", RESPONSE_NOT_FOUND);
        }

        for (CustomerOrder order : customerOrders) {
            if (isConfirmed) {
                order.setStatus(ORDER_STATUS_CONFIRMED);
            } else {
                order.setStatus(ORDER_STATUS_CANCELLED);
            }

            customerOrderRepository.save(order);
        }

        if (!isConfirmed) {
            Set<Long> uniqueTransactionIds = new HashSet<>(transactionIds);
            for (Long transactionId : uniqueTransactionIds) {
                Transaction transaction = transactionRepository.findByTransactionId(transactionId);
                if (transaction == null) {
                    log.warn("confirmationOrder - transacción no encontrada para cierre, transactionId={}",
                            transactionId);
                    continue;
                }
                transaction.setStatus(TRANSACTION_STATUS_CLOSED);
                transactionRepository.save(transaction);
                log.info("confirmationOrder - transacción cerrada por cancelación, transactionId={}, status={}",
                        transactionId, TRANSACTION_STATUS_CLOSED);
            }
        } else {
            log.debug("confirmationOrder - transacciones se mantienen activas por confirmación, transactionIds={}",
                    transactionIds);
        }

        String notifTitle = isConfirmed ? "Orden confirmada - Mesa " + tableNumber : "Orden cancelada - Mesa " + tableNumber;
        String notifBody = isConfirmed ? "La orden en la mesa " + tableNumber + " ha sido confirmada. Revisa las órdenes pendientes." :
            "La orden en la mesa " + tableNumber + " ha sido cancelada. Revisa las órdenes pendientes.";

        if (subscription != null && subscription.getToken() != null && !subscription.getToken().isBlank()) {
            notificationService.sendNotificationToClient(subscription.getToken(), notifTitle, notifBody);
        } else {
            log.warn("confirmationOrder - notificacion omitida: suscripcion/token no encontrado para companyId={}",
                    company.getId());
        }

        String message = isConfirmed ? "Orden confirmada" : "Orden cancelada";
        log.info("confirmationOrder - success, {} ordenes actualizadas, isConfirmed={}, tableNumber={}, phone={}",
                customerOrders.size(), isConfirmed, tableNumber, phoneNumber);
        return new GenericResponse(message, RESPONSE_OK);
    }

    @Override
    public List<OrderResponseDTO> noConfirmationOrder(Long tableNumber, String phoneNumber) {

        Long tokenCompanyId = getAuthenticatedCompanyId();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            log.warn("noConfirmationOrder - compañia no encontrada, externalCompanyId={}", tokenCompanyId);
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        log.info("noConfirmationOrder - start, tableNumber={}, phone={}, companyId={}", tableNumber, phoneNumber, company.getId());

        List<Object[]> resultList = orderTransactionRepository.findAllOrdersNotConfirm(tableNumber, company.getId(),
                phoneNumber);

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? ((Number) result[0]).intValue() : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown";
            Double totalGeneral = (result[2] != null) ? ((Number) result[2]).doubleValue() : 0.0;
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null;
            Integer productId = (result[4] != null) ? ((Number) result[4]).intValue() : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? ((Number) result[6]).intValue() : 0;
            Double unitPrice = (result[7] != null) ? ((Number) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((Number) result[8]).doubleValue() : 0.0;
            Double transactionId = (result[9] != null) ? ((Number) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;

            Long transactionIdL = transactionId.longValue();

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral,
                        transactionIdL);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
                dto.getOrders()
                    .add(new OrderDTO(orderId, productId.toString(), productName, qty, unitPrice, totalPrice, date));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());
    }

    @Override
    public List<OrderResponseDTO> confirmedOreders(Long tableNumber, String phoneNumber) {
        Long tokenCompanyId = getAuthenticatedCompanyId();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            log.warn("confirmedOreders - compañia no encontrada, externalCompanyId={}", tokenCompanyId);
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        log.info("confirmedOreders - start, tableNumber={}, phone={}, companyId={}", tableNumber, phoneNumber, company.getId());

        List<Object[]> resultList = orderTransactionRepository.findAllOrdersConfirm(tableNumber, phoneNumber,
                company.getId());

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? ((Number) result[0]).intValue() : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown";
            Double totalGeneral = (result[2] != null) ? ((Number) result[2]).doubleValue() : 0.0;
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null;
            Integer productId = (result[4] != null) ? ((Number) result[4]).intValue() : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? ((Number) result[6]).intValue() : 0;
            Double unitPrice = (result[7] != null) ? ((Number) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((Number) result[8]).doubleValue() : 0.0;
            Double transactionId = (result[9] != null) ? ((Number) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;

            Long transactionIdL = transactionId.longValue();

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral,
                        transactionIdL);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
                dto.getOrders()
                    .add(new OrderDTO(orderId, productId.toString(), productName, qty, unitPrice, totalPrice, date));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());
    }

    @Override
    public List<CompanyArqDTO> getOrdersArq(Long companyId) {
        List<Object[]> results = orderTransactionRepository.findCompanyData(companyId);

        // Agrupar los resultados por table_number y mapear a ArticuloDTO
        Map<String, List<ArticuloDTO>> articulosPorMesa = results.stream()
                .map(row -> new ArticuloDTO(
                        row[0] != null ? row[0].toString() : null, // table_number (idcuenta)
                        row[1] != null ? row[1].toString() : null, // order_id
                        row[2] != null ? row[2].toString() : null, // product_id
                        row[3] != null ? row[3].toString() : null, // name (desc)
                        row[4] != null ? row[4].toString() : null, // quantity (cantidad)
                        row[5] != null ? row[5].toString() : null, // price (precio)
                        row[6] != null ? row[6].toString() : null, // comment_product (comentario)
                        row[7] != null ? row[7].toString() : null, // customer_order_date (hora)
                        null // descuento
                ))
                .collect(Collectors.groupingBy(ArticuloDTO::getIdcuenta));

        // Crear una lista de CompanyArqDTO, una por cada table_number
        List<CompanyArqDTO> companyArqDTOs = articulosPorMesa.entrySet().stream()
                .map(entry -> new CompanyArqDTO(
                        companyId.intValue(), // companyId
                        0.0, // descuentoGeneral
                        entry.getValue() // Lista de ArticuloDTO para este table_number
                ))
                .collect(Collectors.toList());

        return companyArqDTOs;
    }

    @Override
    public GenericResponse confirmOrdersArq(ConfirmOrderArq request) {
        log.info("confirmOrdersArq - start, orderId={}, companyId={}", request.getOrderId(), request.getCompanyId());

        CustomerOrder order = customerOrderRepository
                .findByOrderIdAndCompanyId(request.getOrderId(), request.getCompanyId());

        if (order == null) {
            log.warn("confirmOrdersArq - orden no encontrada, orderId={}, companyId={}",
                    request.getOrderId(), request.getCompanyId());
            throw new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(ORDER_STATUS_CONFIRMED_ARQ);
        customerOrderRepository.save(order);
        log.info("confirmOrdersArq - success, orderId={} actualizada a status={}",
                order.getOrderId(), ORDER_STATUS_CONFIRMED_ARQ);
        return new GenericResponse("Se realizo el cambio de estado de la orden", RESPONSE_OK);
    }
}
