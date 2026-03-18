package com.restaurante.bot.repository;

import com.restaurante.bot.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

        Optional<Position> findByDescription(String description);

        @Override
        Page<Position> findAll(Pageable pageable);

        List<Position> findByStatus(String status);

        Page<Position> findByStatus(String status, Pageable pageable);

        @Query(value = "SELECT p FROM Position p " +
                        "WHERE (:id IS NULL OR str(p.positionId) LIKE CONCAT(:id, '%')) " +
                        "AND (:description IS NULL OR UPPER(p.description) LIKE UPPER(CONCAT('%', :description, '%'))) " +
                        "AND (:status IS NULL OR UPPER(p.status) = UPPER(:status) OR p.status = 'ACTIVE') " +
                        "ORDER BY p.positionId ASC",
                countQuery = "SELECT COUNT(p) FROM Position p " +
                        "WHERE (:id IS NULL OR str(p.positionId) LIKE CONCAT(:id, '%')) " +
                        "AND (:description IS NULL OR UPPER(p.description) LIKE UPPER(CONCAT('%', :description, '%'))) " +
                        "AND (:status IS NULL OR UPPER(p.status) = UPPER(:status) OR p.status = 'ACTIVE')")
        Page<Position> findByIdOrDescriptionContainingIgnoreCaseAndStatus(
                        @Param("id") String id,
                        @Param("description") String description,
                        @Param("status") String status,
                        Pageable pageable);

}
