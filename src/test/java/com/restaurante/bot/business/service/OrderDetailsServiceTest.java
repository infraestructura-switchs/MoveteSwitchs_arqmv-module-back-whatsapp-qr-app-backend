package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.User;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void confirmationOrderShouldSkipNotificationWhenSubscriptionIsMissing() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(326L, null, List.of()));

        Company company = new Company();
        company.setId(45L);

        User user = new User();
        user.setUserId(100L);

        CustomerOrder pendingOrder = new CustomerOrder();
        pendingOrder.setOrderId(1L);
        pendingOrder.setStatus(3);

        when(companyRepository.existsByExternalCompanyId(326L)).thenReturn(true);
        when(companyRepository.findByExternalCompanyId(326L)).thenReturn(company);
        when(userRepository.findUserByCompany(45L)).thenReturn(user);
        when(subscriptionRepository.findByUserId(100L)).thenReturn(null);
        when(transactionRepository.getTransactionIdsByPhoneNumber("3001234567", 1L, 45L)).thenReturn(List.of(77L));
        when(customerOrderRepository.findByTransactionIdsAndStatusNoConfirm(List.of(77L))).thenReturn(List.of(pendingOrder));

        GenericResponse response = orderDetailsService.confirmationOrder("3001234567", true, 1L);

        assertEquals("Orden confirmada", response.getMessage());
        assertEquals(200L, response.getCode());
        assertEquals(1, pendingOrder.getStatus());
        verify(customerOrderRepository).save(pendingOrder);
        verify(notificationService, never()).sendNotificationToClient(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void findOrCreateCustomerShouldRejectJwtValueInPhone() {
        GenericException exception = assertThrows(
                GenericException.class,
                () -> orderDetailsService.findOrCreateCustomer("eyJhbGciOiJIUzUxMiJ9.abc.def"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("El campo phone debe contener un numero de telefono valido", exception.getMessage());
        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any(Customer.class));
    }

    @Test
    void findOrCreateCustomerShouldNormalizePhoneBeforeLookupAndSave() {
        when(customerRepository.findByPhone("+573001234567")).thenReturn(null);

        Customer saved = new Customer();
        saved.setPhone("+573001234567");
        when(customerRepository.save(org.mockito.ArgumentMatchers.any(Customer.class))).thenReturn(saved);

        Customer result = orderDetailsService.findOrCreateCustomer(" +57 300-123-4567 ");

        assertEquals("+573001234567", result.getPhone());
        verify(customerRepository).findByPhone("+573001234567");
        verify(customerRepository).save(org.mockito.ArgumentMatchers.any(Customer.class));
    }
}