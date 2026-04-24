package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.UserRepositoryPort;
import com.restaurante.bot.model.User;
import com.restaurante.bot.repository.UserRepository;
import com.restaurante.bot.util.StatusConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByEmailOrLogin(String email, String login) {
        return userRepository.findByEmailOrLogin(email, login);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Page<User> findByStatus(String status, Pageable pageable) {
        return userRepository.findByStatusAndStatusNot(status, StatusConstants.DELETED_STATUS, pageable);
    }

    @Override
    public Page<User> findByStatusAndStatusNot(String status, String excludedStatus, Pageable pageable) {
        return userRepository.findByStatusAndStatusNot(status, excludedStatus, pageable);
    }

    @Override
    public List<User> findByStatus(String status) {
        return userRepository.findByStatusAndStatusNot(status, StatusConstants.DELETED_STATUS);
    }

    @Override
    public List<User> findByStatusAndStatusNot(String status, String excludedStatus) {
        return userRepository.findByStatusAndStatusNot(status, excludedStatus);
    }

    @Override
    public Page<User> customSearch(Map<String, String> filters, Pageable pageable) {
        // delegate to repository query method or implement dynamic criteria
        String orders = filters.getOrDefault("orders", com.restaurante.bot.util.SortConstants.ASC);
        String sortBy = filters.getOrDefault("sortBy", "userId");
        // for simplicity reusing existing repository method via criteria-building
        return userRepository.findByUserIdOrNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLoginContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCaseOrPosition_DescriptionContainingIgnoreCaseOrArea_DescriptionContainingIgnoreCaseOrAndStatus(
                // convert parameters
                filters.containsKey("userId") ? Long.valueOf(filters.get("userId")) : null,
                filters.getOrDefault("name", ""),
                filters.getOrDefault("email", ""),
                filters.getOrDefault("login", ""),
                filters.getOrDefault("companyName", ""),
                filters.getOrDefault("positionDescription", ""),
                filters.getOrDefault("areaDescription", ""),
                filters.getOrDefault("status", ""),
                pageable);
    }

    @Override
    public Page<User> findByEmailOrLoginAndIdNotIn(String email, String login, Collection<Long> excludedIds,
                                                    Pageable pageable) {
        return userRepository.findByEmailOrLoginAndIdNotIn(email, login, excludedIds, pageable);
    }
}
