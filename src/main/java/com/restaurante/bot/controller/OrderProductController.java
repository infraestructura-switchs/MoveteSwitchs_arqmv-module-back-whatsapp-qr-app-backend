package com.restaurante.bot.controller;

import com.restaurante.bot.model.OrderProduct;
import com.restaurante.bot.business.service.OrderProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/order_product")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class OrderProductController {

    private final OrderProductService orderProductService;

    public OrderProductController(OrderProductService orderProductService) {
        this.orderProductService = orderProductService;
    }

    @GetMapping
    public List<OrderProduct> listarOrderProducts() {
        return orderProductService.ListarOrderProduct();
    }

    @PostMapping
    public OrderProduct guardarOrderProduct(@RequestBody OrderProduct orderProduct) {
        return orderProductService.guardarOrderProduct(orderProduct);
    }
}
