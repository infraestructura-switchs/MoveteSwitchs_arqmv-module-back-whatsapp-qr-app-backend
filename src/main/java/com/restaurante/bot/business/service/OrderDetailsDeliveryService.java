package com.restaurante.bot.business.service;

import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.application.ports.incoming.OrderDetailsDeliveryUseCase;
import com.restaurante.bot.business.interfaces.IOrderDetailBusiness;
import com.restaurante.bot.dto.*;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDetailsDeliveryService implements IOrderDetailBusiness, OrderDetailsDeliveryUseCase {

    private final OrderDeliveryDetailRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductDeliveryRepository orderProductRepository;
    private final CallServiceHttp callServiceHttp;
    private final CustomerOrderRepository customerOrderRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;


    @Override
    public OrderDetailDelivery saveOrder(OrderDetailsDeliveryDTO orderDetailsDTO) {
        Optional<Customer> existingCustomerOptional = Optional.ofNullable(customerRepository.findByPhone(orderDetailsDTO.getPhone()));
        Customer customer;

        if (existingCustomerOptional.isPresent()) {
            customer = existingCustomerOptional.get();
            customer.setName(orderDetailsDTO.getNameClient());
            customer.setEmail(orderDetailsDTO.getMail());
            customer.setAddress(orderDetailsDTO.getAddress());
            customer.setNumerIdentification(orderDetailsDTO.getNameIdentification());
            customer.setTypeIdentificationId(orderDetailsDTO.getTypeIdentificationId());
            customer = customerRepository.save(customer);
        } else {
            customer = Customer.builder()
                    .name(orderDetailsDTO.getNameClient())
                    .phone(orderDetailsDTO.getPhone())
                    .email(orderDetailsDTO.getMail())
                    .address(orderDetailsDTO.getAddress())
                    .numerIdentification(orderDetailsDTO.getNameIdentification())
                    .typeIdentificationId(orderDetailsDTO.getTypeIdentificationId())
                    .build();
            customer = customerRepository.save(customer);
        }

        OrderDetailDelivery orderDetail = new OrderDetailDelivery();
        orderDetail.setTotal(orderDetailsDTO.getTotal());
        orderDetail.setStatusOrder("SIN CONFIRMAR");
        orderDetail.setMethod(orderDetailsDTO.getMethod());
        orderDetail.setStatus("ACTIVE");
        orderDetail.setPaymentId(orderDetailsDTO.getPaymentId());
        orderDetail.setCustomerId(customer.getCustomer_id());

        orderDetail = orderRepository.save(orderDetail);

        Long companyId = 238L;
        if (companyId == null || companyId <= 0) {
            throw new IllegalArgumentException("Invalid company ID");
        }

        List<SellProducts> productsList = orderDetailsDTO.getItems();

        for (SellProducts productDTO : productsList) {

            OrderProductDelivery orderProduct = new OrderProductDelivery();
            orderProduct.setOrderTransactionDeliveryId(orderDetail.getOrderTransactionDeliveryId());
            orderProduct.setProductId(productDTO.getProductId());
            orderProduct.setQuantity(productDTO.getQty());
            orderProduct.setCommentProduct(productDTO.getComment());
            orderProductRepository.save(orderProduct);
        }

        return orderDetail;
    }
    @Override
    public List<OrderDeliveryResponseDTO> getOrderDetails() {
        List<OrderDetailDelivery> orderDetails = orderRepository.findByStatusAndStatusOrder("ACTIVE", "PENDIENTE");

        List<Long> orderIds = orderDetails.stream()
                .map(OrderDetailDelivery::getOrderTransactionDeliveryId)
                .collect(Collectors.toList());

        List<OrderProductDelivery> allProducts = orderProductRepository.findByOrderTransactionDeliveryIdIn(orderIds);

        Map<Long, List<SellProducts>> productsGroupedByOrder = allProducts.stream()
                .collect(Collectors.groupingBy(
                        OrderProductDelivery::getOrderTransactionDeliveryId,
                        Collectors.mapping(op -> {
                            SellProducts product = new SellProducts();
                            product.setProductId(op.getProductId());
                            product.setProductName(op.getName());
                            product.setQty(op.getQuantity());
                            product.setUnitePrice(op.getUnitPrice());
                            return product;
                        }, Collectors.toList())
                ));

        List<OrderDeliveryResponseDTO> responses = new ArrayList<>();

        for (OrderDetailDelivery od : orderDetails) {
            OrderDeliveryResponseDTO dto = new OrderDeliveryResponseDTO();
            dto.setPhone(customerRepository.findById(od.getCustomerId()).map(com.restaurante.bot.model.Customer::getPhone).orElse(null));
            dto.setOrderTransactionDeliveryId(od.getOrderTransactionDeliveryId());
            dto.setTotal(od.getTotal());
            dto.setPaymentId(od.getPaymentId());
            dto.setMethod(od.getMethod());
            dto.setStatus(od.getStatus());
            dto.setStatusOrder(od.getStatusOrder());

            com.restaurante.bot.model.Customer customer = customerRepository.findById(od.getCustomerId()).orElse(null);
            if (customer != null) {
                dto.setNameClient(customer.getName());
                dto.setAddress(customer.getAddress());
                dto.setPhone(customer.getPhone());
                dto.setMail(customer.getEmail());
                dto.setNumerIdentification(customer.getNumerIdentification());
            }

            dto.setProducts(productsGroupedByOrder.getOrDefault(od.getOrderTransactionDeliveryId(), List.of()));
            responses.add(dto);
        }

        return responses;
    }

    @Override
    public OrderDetailDelivery updateOrderStatus(Long orderTransactionDeliveryId, OrderStatusDTO updateOrderStatusDTO) {
        OrderDetailDelivery orderDetail = orderRepository.findById(orderTransactionDeliveryId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderDetail.setStatusOrder(updateOrderStatusDTO.getOrderStatus());

        return orderRepository.save(orderDetail);
    }

    @Override
    public GenericResponse updateOrder(OrderDetailsDeliveryDTO orderDetailsDeliveryDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());

        OrderDetailDelivery orderDetailDelivery = orderRepository.findByOrderTransactionDeliveryId(orderDetailsDeliveryDTO.getOrderTransactionDeliveryId());
        orderDetailDelivery.setMethod(orderDetailsDeliveryDTO.getMethod());
        orderDetailDelivery.setPaymentId(orderDetailsDeliveryDTO.getPaymentId());
        orderDetailDelivery.setTotal(orderDetailsDeliveryDTO.getTotal());
        orderDetailDelivery.setStatusOrder("PENDIENTE");

        orderRepository.save(orderDetailDelivery);

        Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());
        customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
        customer.setNumerIdentification(orderDetailsDeliveryDTO.getNameIdentification());
        customer.setName(orderDetailsDeliveryDTO.getNameClient());
        customer.setAddress(orderDetailsDeliveryDTO.getAddress());
        customer.setPhone(orderDetailsDeliveryDTO.getPhone());
        customer.setEmail(orderDetailsDeliveryDTO.getMail());

        customerRepository.save(customer);

        String title = "Pedido delivery actualizado";
        String body = "Orden de delivery para " + orderDetailsDeliveryDTO.getNameClient() + " (tel: " + orderDetailsDeliveryDTO.getPhone() + ") ha sido actualizada. Revisa los pedidos de delivery.";
        notificationService.sendNotificationToClient(subscription.getToken(), title, body);

        return new GenericResponse("Orden actualizada con exito", 200L);
    }

    @Override
    public OrderDeliveryProducts getOrdersConfirmation(String phoneNumber) {

        List<Object[]> results = orderProductRepository.getOrderProductDeliveryList(phoneNumber);

        // Inicializamos el DTO
        OrderDeliveryProducts orderDeliveryProducts = new OrderDeliveryProducts();
        orderDeliveryProducts.setPhone(phoneNumber);
        orderDeliveryProducts.setItems(new ArrayList<>());
        orderDeliveryProducts.setTotal(0.0);

        // Mapear los resultados
        for (Object[] result : results) {
            String phone = (String) result[0];
            Long orderTransactionDEliveryId = ((BigDecimal) result[1]).longValue();
            Long productId = ((BigDecimal) result[2]).longValue();
            String productName = (String) result[3];
            Long quantity = (Long) result[4];
            Double price = ((BigDecimal) result[5]).doubleValue();
            Double totalPrice = ((BigDecimal) result[6]).doubleValue();
            String comment = (String) result[7];

            // Crear el producto
            SellProducts sellProducts = new SellProducts(productId, productName, quantity, price, comment);

            // Añadir el producto a la lista de productos
            orderDeliveryProducts.getItems().add(sellProducts);

            orderDeliveryProducts.setOrderTransactionDeliveryId(orderTransactionDEliveryId);

            // Sumar el total
            orderDeliveryProducts.setTotal(orderDeliveryProducts.getTotal() + totalPrice);
        }

        return orderDeliveryProducts;
    }

    @Override
    public Boolean delete(Long id) {
        if (orderRepository.existsById(id)) {
            OrderDetailDelivery orderDetail = orderRepository.findById(id).get();
            orderDetail.setStatus("INACTIVE");
            orderRepository.save(orderDetail);
            return true;
        } else {
            throw new RuntimeException("El pedido no fue encontrado por el id " + id);
        }
    }
}
