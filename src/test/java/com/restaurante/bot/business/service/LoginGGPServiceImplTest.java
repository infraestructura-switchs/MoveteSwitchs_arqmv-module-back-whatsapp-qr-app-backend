package com.restaurante.bot.business.service;

import com.restaurante.bot.dto.LoginIn;
import com.restaurante.bot.dto.LoginOut;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.UserRepository;
import com.restaurante.bot.security.SessionRegistryService;
import com.restaurante.bot.util.JwtUtil;
import com.restaurante.bot.util.LoginMode;
import com.restaurante.bot.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginGGPServiceImplTest {

        private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[a-f0-9]{32}$");

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Utils utils;

        @Mock
        private SessionRegistryService sessionRegistryService;

    @InjectMocks
    private LoginGGPServiceImpl loginGGPService;

    @Test
    void loginShouldGenerateSessionIdAndAttachItToToken() {
        User user = User.builder()
                .userId(9L)
                .name("Admin")
                .login("admin")
                .password("hashed-password")
                .email("admin@test.com")
                .rol(Rol.builder().rolId(1L).name("ADMIN").build())
                .area(Area.builder().areaId(2L).description("Area").build())
                .position(Position.builder().positionId(3L).description("Manager").build())
                .company(Company.builder().id(4L).externalCompanyId(273L).name("Company").build())
                .status("ACTIVE")
                .build();

        LoginIn loginIn = LoginIn.builder()
                .username("admin")
                .password("secret")
                .loginMode(LoginMode.GGP_LOGIN)
                .build();

        String fixedSessionId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        when(userRepository.findByLogin("admin")).thenReturn(Optional.of(user));
        when(utils.doPasswordsMatch("secret", "hashed-password")).thenReturn(true);
        when(jwtUtil.generateSessionId()).thenReturn(fixedSessionId);
        when(jwtUtil.generateToken(eq(4L), eq(273L), eq(9L), eq(fixedSessionId)))
                .thenReturn("jwt-" + fixedSessionId);

        LoginOut response = loginGGPService.login(loginIn);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getData().getSessionId());
        assertFalse(response.getData().getSessionId().isBlank());
        assertTrue(SESSION_ID_PATTERN.matcher(response.getData().getSessionId()).matches());
        assertEquals("jwt-" + response.getData().getSessionId(), response.getData().getToken());
        assertEquals("******", response.getData().getPassword());
                verify(sessionRegistryService).registerSession(eq(response.getData().getSessionId()), eq(4L), eq(273L), eq(9L));
    }
}