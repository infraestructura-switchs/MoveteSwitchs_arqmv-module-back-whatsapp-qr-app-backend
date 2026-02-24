package com.restaurante.bot.repository;

import com.restaurante.bot.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@EntityScan("com.restaurante.bot.model")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Product p1 = new Product();
        p1.setName("Arroz con Pollo");
        p1.setDescription("Delicioso arroz con pollo tradicional");
        p1.setPrice(15000.0);
        p1.setCategoryId(1L);
        p1.setCompanyId(1L);
        p1.setStatus("ACTIVO");
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Sopa de Lentejas");
        p2.setDescription("Sopa casera con lentejas");
        p2.setPrice(10000.0);
        p2.setCategoryId(2L);
        p2.setCompanyId(1L);
        p2.setStatus("ACTIVO");
        productRepository.save(p2);

        Product p3 = new Product();
        p3.setName("Arroz Chaufa");
        p3.setDescription("Arroz estilo oriental");
        p3.setPrice(18000.0);
        p3.setCategoryId(1L);
        p3.setCompanyId(1L);
        p3.setStatus("INACTIVO");
        productRepository.save(p3);

        productRepository.flush();
    }

    @Test
    void search_ByNamePrefix_ShouldReturnArrozConPollo() {
        List<Product> results = productRepository.search(1L, "Arroz", null);
        assertFalse(results.isEmpty(), "Should find at least one product starting with 'Arroz'");
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("Arroz con Pollo")));
        assertFalse(results.stream().anyMatch(p -> p.getName().equals("Sopa de Lentejas")));
    }

    @Test
    void search_ByDescription_ShouldReturnProducts() {
        List<Product> results = productRepository.search(1L, "con", null);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("Arroz con Pollo")));
        // "Sopa de Lentejas" shouldn't be here if searching for "con" in name/desc
        // unless its desc has it
        // p2.setDescription("Sopa casera con lentejas"); -> has "con"
        assertTrue(results.stream().anyMatch(p -> p.getName().equals("Sopa de Lentejas")));
    }

    @Test
    void findAllByCompanyAndCategoryAndNameOrderByPrice_ShouldWork() {
        List<Product> results = productRepository.findAllByCompanyAndCategoryAndNameOrderByPrice(1L, 1L, null, "ASC");
        assertFalse(results.isEmpty());
        assertEquals("Arroz con Pollo", results.get(0).getName());
    }
}
