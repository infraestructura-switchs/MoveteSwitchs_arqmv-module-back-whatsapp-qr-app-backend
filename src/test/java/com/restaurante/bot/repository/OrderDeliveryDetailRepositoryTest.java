package com.restaurante.bot.repository;

import com.restaurante.bot.dto.OrderDeliveryResponseDTO;
import com.restaurante.bot.model.OrderDetailDelivery;
import com.restaurante.bot.repository.OrderDeliveryDetailRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest(properties = {
        "spring.jpa.show-sql=true",
        "logging.level.org.hibernate.SQL=DEBUG",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class OrderDeliveryDetailRepositoryTest {

    @Autowired
    private OrderDeliveryDetailRepository repository;

    @Test
    void testFindByStatus() {
        List<OrderDetailDelivery> orders = repository.findByStatus("ACTIVE");
        Assertions.assertNotNull(orders);
    }

    @Test
    void testGetOrderDetail() {
        List<OrderDeliveryResponseDTO> orders = repository.getOrderDetail();
        Assertions.assertNotNull(orders);
    }
}