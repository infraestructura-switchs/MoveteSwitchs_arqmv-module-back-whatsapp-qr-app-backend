package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.*;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.OrderDetailDelivery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IOrderDetailBusiness {


    @Transactional
    OrderDetailDelivery saveOrder(OrderDetailsDeliveryDTO orderDetailsDTO);

    List<OrderDeliveryResponseDTO> getOrderDetails();

    Boolean delete(Long id);

    OrderDetailDelivery updateOrderStatus(Long orderTransactionDeliveryId, OrderStatusDTO updateOrderStatusDTO);

    GenericResponse updateOrder(OrderDetailsDeliveryDTO orderDetailsDeliveryDTO);

    OrderDeliveryProducts getOrdersConfirmation(String phoneNumber);
}
