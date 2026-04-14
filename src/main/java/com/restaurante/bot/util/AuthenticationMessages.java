package com.restaurante.bot.util;

/**
 * Mensajes de autenticación y autorización
 * Centralizador de mensajes de error para login sin revelar detalles de la BD
 */
public final class AuthenticationMessages {
    private AuthenticationMessages() {}

    // Mensajes de Login
    public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String AUTHENTICATION_FAILED = "No se pudo completar la autenticación";
    
    // Mensajes de sesión
    public static final String LOGIN_SUCCESS = "success";
    
    // Mensajes de autorización
    public static final String UNAUTHORIZED_ACCESS = "Acceso no autorizado";
    public static final String TOKEN_EXPIRED = "Token expirado";
    public static final String TOKEN_INVALID = "Token inválido";
}
