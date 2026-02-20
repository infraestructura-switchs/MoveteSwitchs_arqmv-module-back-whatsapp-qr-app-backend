package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserR2dbcRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByEmail(String email);

    Mono<UserEntity> findByLogin(String login);

    @Query("SELECT * FROM user_app WHERE email = :email OR login = :login")
    Mono<UserEntity> findByEmailOrLogin(String email, String login);

    Flux<UserEntity> findByStatus(String status);

    @Query("SELECT * FROM user_app WHERE company_id = :companyId LIMIT 1")
    Mono<UserEntity> findUserByCompany(Long companyId);

    @Query("""
            SELECT u.* FROM user_app u
            LEFT JOIN company c ON u.company_id = c.company_id
            LEFT JOIN position p ON u.position_id = p.position_id
            LEFT JOIN area a ON u.area_id = a.area_id
            WHERE (u.user_id = :userId OR u.name_ LIKE CONCAT('%', :name, '%')
               OR u.email LIKE CONCAT('%', :email, '%') OR u.login LIKE CONCAT('%', :login, '%')
               OR c.name LIKE CONCAT('%', :companyName, '%') OR p.description LIKE CONCAT('%', :positionDescription, '%')
               OR a.description LIKE CONCAT('%', :areaDescription, '%'))
            AND u.status LIKE CONCAT('%', :status, '%')
            LIMIT :limit OFFSET :offset
            """)
    Flux<UserEntity> searchUsers(Long userId, String name, String email, String login,
            String companyName, String positionDescription,
            String areaDescription, String status,
            int limit, long offset);

    @Query("SELECT COUNT(*) FROM user_app u WHERE u.status LIKE CONCAT('%', :status, '%')")
    Mono<Long> countByStatusContaining(String status);
}
