package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.CustomerOrderRepository;
import com.restaurante.bot.repository.CustomerRepository;
import com.restaurante.bot.repository.HistoryRepository;
import com.restaurante.bot.repository.OrderProductRepository;
import com.restaurante.bot.repository.OrderTransactionRepository;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.SubscriptionRepository;
import com.restaurante.bot.repository.TransactionClientRespository;
import com.restaurante.bot.repository.TransactionRepository;
import com.restaurante.bot.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderDetailsServiceTest {

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderTransactionRepository orderTransactionRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private TransactionClientRespository transactionClientRespository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderDetailsService orderDetailsService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveOrderShouldReturnUnauthorizedWhenAuthenticationIsMissing() {
        SecurityContextHolder.clearContext();

        GenericException exception = assertThrows(
                GenericException.class,
                () -> orderDetailsService.saveOrder(new OrderDetailsDTO()));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("No autenticado", exception.getMessage());
    }
}