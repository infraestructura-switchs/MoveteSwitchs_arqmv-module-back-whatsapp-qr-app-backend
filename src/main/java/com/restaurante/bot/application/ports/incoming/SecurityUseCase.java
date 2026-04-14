package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.business.interfaces.SecurityInterface;

/**
 * Security UseCase - defines the incoming port for security operations.
 * This interface extends SecurityInterface to make it available as a UseCase.
 * 
 * In Hexagonal Architecture:
 * - This is an "incoming port" that external adapters (like REST controllers) invoke
 * - The implementation (SecurityApplicationService) contains the business logic
 * - Controllers should be injected with this interface, not the implementation
 */
public interface SecurityUseCase extends SecurityInterface {
    // inherits security operations from SecurityInterface
}
