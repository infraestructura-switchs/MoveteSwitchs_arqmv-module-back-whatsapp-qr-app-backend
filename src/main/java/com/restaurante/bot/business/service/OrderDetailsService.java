package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.OrderInterface;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderDetailsService implements OrderInterface {

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
        newOrder.setStatus(3);
        newOrder.setCustomerId(customer.getCustomer_id());
        newOrder.setCompanyId(companyId);
        return customerOrderRepository.save(newOrder);
    }

    @Transactional
    public RestaurantTable findTableByNumber(Long tableNumber , Long companyId) {

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber,companyId);
        if (table == null) {
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
            orderProduct.setCommentProduct(item.getComment());
            orderProduct.setCompanyId(companyId);

            orderProductRepository.save(orderProduct);
        }
    }

    @Transactional
    public Transaction createTransaction(OrderDetailsDTO orderDetailsDTO, RestaurantTable table, Long companyId) {
        Transaction transaction = transactionRepository.findTransactionByTableId(orderDetailsDTO.getRestaurantTable(),companyId);
        if (transaction == null) {

            transaction = new Transaction();
            //transaction.setCustomerId(customer.getCustomer_id());
            transaction.setTableId(table.getTableId());
            transaction.setStatus(1L);
            transaction.setCompanyId(companyId);
            Transaction newTransaction = transactionRepository.save(transaction);

        }

        return transaction;
    }

    @Transactional
    public Customer findOrCreateCustomer(String phone) {
        Customer customer = customerRepository.findByPhone(phone);
        if (customer == null) {
            customer = new Customer();
            customer.setPhone(phone);
            customerRepository.save(customer);
        }
        return customer;
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
        transaction.setTransactionTotal(orderTransactionRepository.getTotalOrderAmount(transaction.getTransactionId(), companyId));
        transactionRepository.save(transaction);
    }

    @Transactional
    public void saveTransactionHistory(Transaction transaction) {
        History history = new History();
        history.setTransactionId(transaction.getTransactionId());
        history.setDate(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Override
    @Transactional
    public GenericResponse saveOrder(OrderDetailsDTO orderDetailsDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        // Verifica la mesa
        RestaurantTable table = findTableByNumber(orderDetailsDTO.getRestaurantTable(), company.getId());

        // Verifica si la mesa está disponible
        if (table.getStatus() != null && table.getStatus() == 2) {

            // Guarda la orden
            CustomerOrder setOrder = saveCustomerOrder(orderDetailsDTO.getTotal(), orderDetailsDTO.getPhone(), company.getId());

            // Guarda los productos de la orden
            saveOrderProducts(orderDetailsDTO.getItems(), setOrder.getOrderId(), company.getId());

            // Crea o recupera la transacción
            Transaction transaction = createTransaction(orderDetailsDTO, table, company.getId());

            // Crea la relación entre orden y transacción
            OrderTransaction orderTransaction = createOrderTransaction(setOrder.getOrderId(), transaction.getTransactionId(), company.getId());

            // Actualiza el total de la transacción
            updateTransactionTotal(transaction, company.getId());

            // Guarda el historial de la transacción
            saveTransactionHistory(transaction);

            return new GenericResponse("Transacción guardada con éxito", 200L);
        }

        throw new GenericException("Mesa en estado inválido", HttpStatus.BAD_REQUEST);
    }


    @Override
    public List<OrderResponseAdminDTO> getOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        // Obtener todas las mesas
        List<RestaurantTable> allTables = restaurantTableRepository.findAllTablesAsc(company.getId());

        //List<Integer> allTableNumbers = orderTransactionRepository.findAllTableNumbers();

        // Obtener todas las órdenes
        List<Object[]> orderResults = orderTransactionRepository.findAllOrdersWithTableNative();

        // Mapa para almacenar DTOs por mesa
        Map<Integer, OrderResponseAdminDTO> mesaMap = new HashMap<>();

        for (RestaurantTable table : allTables) {
            Integer tableNumber = table.getTableNumber().intValue();
            Integer tableStatus = (table.getStatus() != null) ? table.getStatus().intValue() : 1; // Estado por defecto 1 si es null
            mesaMap.put(tableNumber, new OrderResponseAdminDTO(tableNumber, tableStatus, new ArrayList<>(), new ArrayList<>(), 0.0, null));
        }

        // Procesar las órdenes
        for (Object[] result : orderResults) {
            Integer mesaId = (result[0] != null) ? ((Long) result[0]).intValue() : null;
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown";
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0;
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null;
            Integer productId = (result[4] != null) ? ((BigDecimal) result[4]).intValue() : null;
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product";
            Integer qty = (result[6] != null) ? ((Long) result[6]).intValue() : 0;
            Double unitePrice = (result[7] != null) ? ((BigDecimal) result[7]).doubleValue() : 0.0;
            Double totalPrice = (result[8] != null) ? ((BigDecimal) result[8]).doubleValue() : 0.0;
            Double transactionId = (result[9] != null) ? ((BigDecimal) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;
            Integer orderStatus = (result[11] != null) ? ((Long) result[11]).intValue() : null;

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
                    productName, qty, unitePrice, totalPrice, date);
            if (orderStatus != null && orderStatus == 2) {
                dto.getSentOrders().add(orderDTO);
            } else if (orderStatus != null && (orderStatus == 1 || orderStatus == 5)) {
                dto.getOrders().add(orderDTO);
            }
        }

        return new ArrayList<>(mesaMap.values());
    }


    public void updateTotalTransaction(Long orderId){
        /*

        OrderTransaction orderTransaction = orderTransactionRepository
                .findByOrderId(Long
                        .parseLong(orderId
                                .toString()));

        Transaction transaction = transactionRepository
                .findByTransactionId(orderTransaction
                        .getTransactionId());

        transaction
                .setTransactionTotal(orderTransactionRepository
                        .getTotalOrderAmount(transaction
                                .getTransactionId()));

        transactionRepository.save(transaction);

         */
    }

    @Override
    public GenericResponse sendOrderStatus(OrdersIdsDTO orderIds) {


        for (Long orderId : orderIds.getOrdersIds()){
            CustomerOrder order = customerOrderRepository.findById(orderId).orElseThrow(() -> new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST));
            order.setStatus(2);
            customerOrderRepository.save(order);
        }

        return new GenericResponse("Ordenes cambiadas a estado enviada con exito", 200L);
    }
/*
    @Override
    public List<OrderResponseDTO> getSendOrder(Long tableNumber) {
        List<Object[]> resultList = orderTransactionRepository.findAllOrdersEnviadas(tableNumber);

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? ((Long) result[0]).intValue() : null; // Se realiza el cast a Long antes de convertirlo a Integer
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; // "Unknown" si el estado es nulo
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0; // BigDecimal a Double
            Long orderId = (result[3] != null) ? ((Long) result[3]).longValue() : null; // Aseguramos que orderId es Integer
            Long productId = (result[4] != null) ? ((BigDecimal) result[4]).longValue() : null; // Convertir el tipo Long a Integer
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product"; // Nombre del producto
            Integer qty = (result[6] != null) ? ((Long) result[6]).intValue() : 0; // Convertir Long a Integer para la cantidad
            Double unitePrice = (result[7] != null) ? ((BigDecimal) result[7]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double totalPrice = (result[8] != null) ? ((BigDecimal) result[8]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double transactionId = (result[9] != null) ? ((BigDecimal) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;

            Long transactionIdL = transactionId.longValue();

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral, transactionIdL);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
            dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName, qty, unitePrice, totalPrice, date));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());

    }

 */

    @Override
    public CustomerOrderResponseDTO getOrederByPhoneNumber(String phoneNumber) {

        List<Object[]> rows = orderTransactionRepository.getOrderByPhoneNumber(phoneNumber);



        if (rows.isEmpty()) return null;

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

        if (rows.isEmpty()) return null;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());

        Long transactionId = transactionRepository.getTransactionIdByPhoneNumber(phoneNumber, tableNumber, company.getId());

        List<CustomerOrder> customerOrders = customerOrderRepository.findByTransactionIdAndStatusNoConfirm(transactionId);

        if (customerOrders.isEmpty()) {
            return new GenericResponse("No se encontraron órdenes para esta transacción", 404L);
        }

        for (CustomerOrder order : customerOrders) {
            if (isConfirmed) {
                order.setStatus(1);
            } else {
                order.setStatus(4);
            }

            customerOrderRepository.save(order);
        }
        notificationService.sendNotificationToClient(subscription.getToken(), "Orden confirmada", "Actualizar las ordenes");
        String message = isConfirmed ? "Orden confirmada" : "Orden cancelada";
        return new GenericResponse(message, 200L);
    }

    @Override
    public List<OrderResponseDTO> noConfirmationOrder(Long tableNumber, String phoneNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        List<Object[]> resultList = orderTransactionRepository.findAllOrdersNotConfirm(tableNumber, company.getId(), phoneNumber);

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? ((Long) result[0]).intValue() : null; // Se realiza el cast a Long antes de convertirlo a Integer
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; // "Unknown" si el estado es nulo
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0; // BigDecimal a Double
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null; // Aseguramos que orderId es Integer
            Integer productId = (result[4] != null) ? ((BigDecimal) result[4]).intValue() : null; // Convertir el tipo Long a Integer
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product"; // Nombre del producto
            Integer qty = (result[6] != null) ? ((Long) result[6]).intValue() : 0; // Convertir Long a Integer para la cantidad
            Double unitePrice = (result[7] != null) ? ((BigDecimal) result[7]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double totalPrice = (result[8] != null) ? ((BigDecimal) result[8]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double transactionId = (result[9] != null) ? ((BigDecimal) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;

            Long transactionIdL = transactionId.longValue();

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral, transactionIdL);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
            dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName, qty, unitePrice, totalPrice, date));
        }

        // Devolver el resultado
        return new ArrayList<>(mesaMap.values());
    }

    @Override
    public List<OrderResponseDTO> confirmedOreders(Long tableNumber, String phoneNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        List<Object[]> resultList = orderTransactionRepository.findAllOrdersConfirm(tableNumber, phoneNumber, company.getId());

        Map<Integer, OrderResponseDTO> mesaMap = new HashMap<>();

        for (Object[] result : resultList) {
            // Manejo de posibles nulos
            Integer mesaId = (result[0] != null) ? ((Long) result[0]).intValue() : null; // Se realiza el cast a Long antes de convertirlo a Integer
            String statusMesa = (result[1] != null) ? (String) result[1] : "Unknown"; // "Unknown" si el estado es nulo
            Double totalGeneral = (result[2] != null) ? ((BigDecimal) result[2]).doubleValue() : 0.0; // BigDecimal a Double
            Long orderId = (result[3] != null) ? ((Long) result[3]) : null; // Aseguramos que orderId es Integer
            Integer productId = (result[4] != null) ? ((BigDecimal) result[4]).intValue() : null; // Convertir el tipo Long a Integer
            String productName = (result[5] != null) ? (String) result[5] : "Unknown Product"; // Nombre del producto
            Integer qty = (result[6] != null) ? ((Long) result[6]).intValue() : 0; // Convertir Long a Integer para la cantidad
            Double unitePrice = (result[7] != null) ? ((BigDecimal) result[7]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double totalPrice = (result[8] != null) ? ((BigDecimal) result[8]).doubleValue() : 0.0; // Convertir BigDecimal a Double
            Double transactionId = (result[9] != null) ? ((BigDecimal) result[9]).doubleValue() : 0.0;
            String date = (result[10] != null) ? result[10].toString() : null;

            Long transactionIdL = transactionId.longValue();

            // Verificar si la mesa ya existe en el mapa
            OrderResponseDTO dto = mesaMap.get(mesaId);
            if (dto == null) {
                dto = new OrderResponseDTO(mesaId, statusMesa.equals("2") ? 2 : 1, new ArrayList<>(), totalGeneral, transactionIdL);
                mesaMap.put(mesaId, dto);
            }

            // Agregar la orden a la mesa
            dto.getOrders().add(new OrderDTO(orderId, productId.toString(), productName, qty, unitePrice, totalPrice, date));
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

        CustomerOrder order = customerOrderRepository
                .findByOrderIdAndCompanyId(request.getOrderId(), request.getCompanyId());

        if (order == null) {
            throw new GenericException("Orden no encontrada", HttpStatus.BAD_REQUEST);
        }

        order.setStatus(5);
        customerOrderRepository.save(order);
        return new GenericResponse("Se realizo el cambio de estado de la orden", 200L);
    }
}
