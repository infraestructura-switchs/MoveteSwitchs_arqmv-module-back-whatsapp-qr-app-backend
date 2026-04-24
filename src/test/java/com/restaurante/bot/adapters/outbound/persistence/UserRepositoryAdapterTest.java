package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.model.User;
import com.restaurante.bot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Test
    void findAndSaveAndExists() {
        User u = new User();

        when(userRepository.findById(11L)).thenReturn(Optional.of(u));
        when(userRepository.save(u)).thenReturn(u);
        when(userRepository.existsById(11L)).thenReturn(true);

        assertTrue(adapter.findById(11L).isPresent());
        assertEquals(u, adapter.save(u));
        assertTrue(adapter.existsById(11L));
    }

    @Test
    void findByStatus_and_customSearch_and_pagedMethods() {
        User u = new User();
        Page<User> page = new PageImpl<>(List.of(u));

        when(userRepository.findByStatusAndStatusNot("ACTIVE", "DELETED", Pageable.unpaged())).thenReturn(page);
        when(userRepository.findByStatusAndStatusNot("ACTIVE", "DELETED")).thenReturn(List.of(u));
        when(userRepository.findByUserIdOrNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLoginContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCaseOrPosition_DescriptionContainingIgnoreCaseOrArea_DescriptionContainingIgnoreCaseOrAndStatus(
                any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(Pageable.class)
        )).thenReturn(page);

        assertEquals(1, adapter.findByStatus("ACTIVE", Pageable.unpaged()).getTotalElements());
        assertEquals(1, adapter.findByStatus("ACTIVE").size());

        Page<User> custom = adapter.customSearch(Map.of("name", "x"), Pageable.unpaged());
        assertEquals(1, custom.getTotalElements());
    }
}
