package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrLogin(String email, String login);
    Optional<User> findByLogin(String login);

    User save(User user);
    boolean existsById(Long id);
    Page<User> findByStatus(String status, Pageable pageable);
    List<User> findByStatus(String status);

    Page<User> customSearch(Map<String,String> filters, Pageable pageable);
    Page<User> findByEmailOrLoginAndIdNotIn(String email, String login, Collection<Long> excludedIds,
                                            Pageable pageable);
    // add other methods used by the service
}