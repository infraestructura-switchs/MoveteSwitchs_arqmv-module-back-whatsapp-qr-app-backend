package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.model.OrderProduct;
import java.util.List;

public interface OrderProductUseCase {
    List<OrderProduct> ListarOrderProduct();
    OrderProduct guardarOrderProduct(OrderProduct orderProduct);
}