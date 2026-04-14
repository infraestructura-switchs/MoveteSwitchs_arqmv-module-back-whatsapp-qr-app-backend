package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.OrderDetailsDeliveryUseCase;
import com.restaurante.bot.dto.OrderDeliveryProducts;
import com.restaurante.bot.dto.OrderDeliveryResponseDTO;
import com.restaurante.bot.dto.OrderDetailsDeliveryDTO;
import com.restaurante.bot.dto.OrderStatusDTO;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.OrderDetailDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order Delivery REST Controller - handles delivery order management endpoints.
 * 
 * REFACTORED - Clean Architecture Pattern:
 * This controller is a THIN ADAPTER that:
 * - Maps HTTP requests to DTOs
 * - Delegates to OrderDetailsDeliveryUseCase (business logic)
 * - Maps responses back to HTTP
 * - NO try-catch blocks (exceptions handled by GlobalExceptionHandler)
 * - NO business logic
 * - NO direct exception handling
 * 
 * Exception Handling:
 * All exceptions are now handled centrally by GlobalExceptionHandler (@ControllerAdvice)
 * - DomainException → meaningful HTTP status codes
 * - Validation errors → 400 Bad Request
 * - Unexpected errors → 500 Internal Server Error
 * 
 * Benefits:
 * - Consistent error responses across all endpoints
 * - Controllers remain focused on HTTP concerns
 * - Easier to maintain and test
 * - Exceptions propagate to GlobalExceptionHandler
 */
@RestController
@RequestMapping("/${app.request.mapping}/order-delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Slf4j
public class OrderDetailsDeliveryController {
    private final OrderDetailsDeliveryUseCase orderService;

    /**
     * Create new delivery order
     * HTTP: POST /order-delivery/saveOrder
     * Delegates to OrderDetailsDeliveryUseCase.saveOrder()
     * Any exceptions are handled by GlobalExceptionHandler
     */
    @PostMapping("/saveOrder")
    public ResponseEntity<OrderDetailDelivery> createOrder(@RequestBody OrderDetailsDeliveryDTO orderDetailsDTO) {
        log.info("Creating new delivery order");
        OrderDetailDelivery createdOrder = orderService.saveOrder(orderDetailsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Get all active delivery orders
     * HTTP: GET /order-delivery/get-all-orders
     * Delegates to OrderDetailsDeliveryUseCase.getOrderDetails()
     */
    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderDeliveryResponseDTO>> getOrders() {
        log.info("Retrieving all active delivery orders");
        List<OrderDeliveryResponseDTO> activeOrders = orderService.getOrderDetails();
        return ResponseEntity.ok(activeOrders);
    }

    /**
     * Update delivery order status
     * HTTP: PUT /order-delivery/updateStatus/{orderTransactionDeliveryId}
     * Delegates to OrderDetailsDeliveryUseCase.updateOrderStatus()
     */
    @PutMapping("/updateStatus/{orderTransactionDeliveryId}")
    public ResponseEntity<OrderDetailDelivery> updateOrderStatus(
            @PathVariable Long orderTransactionDeliveryId,
            @RequestBody OrderStatusDTO updateOrderStatusDTO) {
        log.info("Updating order status for order ID: {}", orderTransactionDeliveryId);
        OrderDetailDelivery updatedOrder = orderService.updateOrderStatus(orderTransactionDeliveryId, updateOrderStatusDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Delete delivery order
     * HTTP: DELETE /order-delivery/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Deleting delivery order with ID: {}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update entire delivery order
     * HTTP: POST /order-delivery/update-order
     */
    @PostMapping("/update-order")
    public ResponseEntity<GenericResponse> updateOrder(@RequestBody OrderDetailsDeliveryDTO orderDetailsDeliveryDTO) {
        log.info("Updating delivery order");
        GenericResponse response = orderService.updateOrder(orderDetailsDeliveryDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get unconfirmed delivery orders by phone number
     * HTTP: GET /order-delivery/get-no-confirm?phoneNumber={phoneNumber}
     */
    @GetMapping("/get-no-confirm")
    public ResponseEntity<OrderDeliveryProducts> getOrdersConfirmation(@RequestParam String phoneNumber) {
        log.info("Retrieving unconfirmed delivery orders for phone: {}", phoneNumber);
        OrderDeliveryProducts result = orderService.getOrdersConfirmation(phoneNumber);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
