package com.restaurante.bot.repository;

// Projection removed: map to DTOs in service layer instead
import com.restaurante.bot.model.Company;
import com.restaurante.bot.model.Rol;
import com.restaurante.bot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

        // Use existing findByStatus methods; mapping to DTO is handled in service

        Page<User> findByStatus(String status, Pageable pageable);

        List<User> findByStatus(String status);

        @Query("SELECT u FROM User u WHERE u.company.id = :companyId")
        User findUserByCompany(@Param("companyId")  Long companyId);

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
                @Param("userId") Long userId,  @Param("name") String name,  @Param("email") String email,
                @Param("login") String login,  @Param("companyName") String companyName,
                @Param("positionDescription") String positionDescription,  @Param("areaDescription") String areaDescription,
                @Param("status") String status, Pageable pageable);

}
