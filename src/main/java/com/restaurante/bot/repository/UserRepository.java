package com.restaurante.bot.repository;

import com.restaurante.bot.dto.GgpUserGetAllDto;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.Rol;
import com.restaurante.bot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

        public Optional<User> findByEmail(String email);

        public Optional<User> findByEmailOrLogin(String email, String login);

        @Query("from User where (email=:email or login=:login) and userId not in (:userIds)")
        public Page<User> findByEmailOrLoginAndIdNotIn(String email, String login, Collection<Long> userIds,
                        Pageable pageable);

        public Optional<User> findByLogin(String login);

        @Override
        Page<User> findAll(Pageable pageable);

        @Query(value = "SELECT new com.restaurante.bot.dto.GgpUserGetAllDto(u.userId, u.name, u.login,u.password, u.email, r.rolId, r.name, u.status) "
                        +
                        "FROM User u " +
                        "INNER JOIN u.rol r " +
                        "WHERE u.status = 'ACTIVO'", countQuery = "SELECT COUNT(u) " +
                                        "FROM User u " +
                                        "INNER JOIN u.rol r " +
                                        "WHERE u.status = 'ACTIVO'")
        Page<GgpUserGetAllDto> getStatus(Pageable pageable);

        Page<User> findByStatus(String status, Pageable pageable);

        List<User> findByStatus(String status);

        @Query(value = """
                        SELECT *
                        FROM user_app ua WHERE ua.company_id = :company
                        """, nativeQuery = true)
        User findUserByCompany(Long company);

        @Query("SELECT u FROM User u " +
                        "LEFT JOIN u.company c " +
                        "LEFT JOIN u.position p " +
                        "LEFT JOIN u.area a " +
                        "WHERE (u.userId = :userId OR u.name LIKE %:name% OR u.email LIKE %:email% OR u.login LIKE %:login% "
                        +
                        "OR c.name LIKE %:companyName% OR p.description LIKE %:positionDescription% " +
                        "OR a.description LIKE %:areaDescription%) " +
                        "AND u.status LIKE %:status%")
        Page<User> findByUserIdOrNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLoginContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCaseOrPosition_DescriptionContainingIgnoreCaseOrArea_DescriptionContainingIgnoreCaseOrAndStatus(
                        Long userId, String name, String email, String login, String companyName,
                        String positionDescription, String areaDescription, String status, Pageable pageable);

}
