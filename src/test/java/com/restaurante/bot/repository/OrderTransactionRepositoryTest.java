package com.restaurante.bot.repository;

import com.restaurante.bot.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrderTransactionRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderTransactionRepository repo;

    @Test
    public void testGetTotalOrderAmount() {
        Transaction t = new Transaction();
        t.setTableId(1);
        t.setTransactionTotal(0.0);
        t.setStatus(1L);
        t.setCompanyId(1L);
        Transaction persistedT = em.merge(t);

        CustomerOrder co = new CustomerOrder();
        co.setStatus(1);
        co.setDate(LocalDateTime.now());
        co.setCompanyId(1L);
        co.setTotal(100.0);
        CustomerOrder persistedCo = em.merge(co);

        OrderTransaction ot = new OrderTransaction();
        ot.setCompanyId(1L);
        ot.setOrderId(persistedCo.getOrderId());
        ot.setTransactionId(persistedT.getTransactionId());
        em.merge(ot);

        em.flush();

        Double total = repo.getTotalOrderAmount(persistedT.getTransactionId(), 1L);
        System.out.println("DEBUG total=" + total);
        assertThat(total).isEqualTo(100.0);
    }

    @Test
    public void testFindOrderProductsByTable() {
        // Transaction with tableId = 5
        Transaction t = new Transaction();
        t.setTableId(5);
        t.setTransactionTotal(20.0);
        t.setStatus(1L);
        t.setCompanyId(1L);
        Transaction persistedT = em.merge(t);

        // Product
        Product p = new Product();
        p.setName("Pizza");
        p.setPrice(20.0);
        p.setCompanyId(1L);
        Product persistedP = em.merge(p);

        // CustomerOrder
        CustomerOrder co = new CustomerOrder();
        co.setStatus(1);
        co.setDate(LocalDateTime.now());
        co.setCompanyId(1L);
        co.setTotal(20.0);
        CustomerOrder persistedCo = em.merge(co);

        // OrderProduct
        OrderProduct op = new OrderProduct();
        op.setOrderId(persistedCo.getOrderId());
        op.setProductId(persistedP.getProductId());
        op.setQuantity(1);
        op.setUnitPrice(persistedP.getPrice());
        op.setCompanyId(1L);
        em.merge(op);

        // OrderTransaction linking
        OrderTransaction ot = new OrderTransaction();
        ot.setOrderId(persistedCo.getOrderId());
        ot.setTransactionId(persistedT.getTransactionId());
        ot.setCompanyId(1L);
        em.merge(ot);

        em.flush();

        List<Object[]> rows = repo.findOrderProductsByTable(persistedT.getTableId());
        System.out.println("DEBUG rows.size=" + (rows == null ? 0 : rows.size()));
        assertThat(rows).isNotEmpty();
        Object[] r = rows.get(0);
        // SQL projection: o.order_id, p.product_id, p.name, op.quantity, p.price, (op.quantity * p.price), o.total
        assertThat(((Number) r[0]).longValue()).isEqualTo(persistedCo.getOrderId());
        assertThat(String.valueOf(r[2])).isEqualTo(persistedP.getName());
        assertThat(((Number) r[3]).intValue()).isEqualTo(op.getQuantity());
    }

}
