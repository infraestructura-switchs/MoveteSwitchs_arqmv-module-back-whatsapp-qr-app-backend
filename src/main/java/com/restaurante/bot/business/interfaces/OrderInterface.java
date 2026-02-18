package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.*;
import com.restaurante.bot.model.GenericResponse;

import java.util.List;

public interface OrderInterface {

    GenericResponse saveOrder (OrderDetailsDTO orderDetailsDTO);

    List<OrderResponseAdminDTO> getOrders();

    GenericResponse sendOrderStatus(OrdersIdsDTO orderIds);

   // List<OrderResponseDTO> getSendOrder(Long tableNumber);

    CustomerOrderResponseDTO getOrederByPhoneNumber(String phoneNumber);

    CustomerOrderResponseDTO getOrederByTableNumber(Integer tableNumber);

    GenericResponse confirmationOrder(String phoneNumber, Boolean isConfirmed, Long tableNumber);

    List<OrderResponseDTO> noConfirmationOrder(Long tableNumber, String phoneNumber);

    List<OrderResponseDTO> confirmedOreders(Long tableNumber, String phoneNumber);

    List<CompanyArqDTO> getOrdersArq(Long companyId);

    GenericResponse confirmOrdersArq(ConfirmOrderArq request);
}
