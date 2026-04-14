package com.restaurante.bot.util;

public class Constants {

    // Backwards-compatible facade pointing to grouped constants
    public static final String ACTIVE_STATUS = StatusConstants.ACTIVE;
    public static final String INACTIVE_STATUS = StatusConstants.INACTIVE;

    public static final String FORMAT_DATE_DDMMYYYY = DateConstants.FORMAT_DATE_DDMMYYYY;
    public static final String FORMAT_DATE_YYYYMMDD = DateConstants.FORMAT_DATE_YYYYMMDD;
    public static final String FORMAT_DATE_DDMMYYYYHHMMSS = DateConstants.FORMAT_DATE_DDMMYYYYHHMMSS;

    public static final String DEFAULT_PASSWORD = SecurityConstants.DEFAULT_PASSWORD;
    public static final String PASSWORD_DEFUULT_PREFIX = SecurityConstants.PASSWORD_DEFAULT_PREFIX;

    public static final Long ROLD_ID_TECNICO = RoleConstants.ROLD_ID_TECNICO;
    public static final Long ROLD_ID_ADMINISTRADOR = RoleConstants.ROLD_ID_ADMINISTRADOR;

    public static final String COMPANY = CompanyConstants.COMPANY;

    public static final String ENVIRONTMENT_NAME = EnvConstants.ENVIRONMENT_NAME;

    // Authentication Messages
    public static final String INVALID_CREDENTIALS = AuthenticationMessages.INVALID_CREDENTIALS;
    public static final String AUTHENTICATION_FAILED = AuthenticationMessages.AUTHENTICATION_FAILED;
    public static final String LOGIN_SUCCESS = AuthenticationMessages.LOGIN_SUCCESS;
}
