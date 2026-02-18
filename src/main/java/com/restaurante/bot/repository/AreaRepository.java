package com.restaurante.bot.repository;

import com.restaurante.bot.model.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {

    Optional<Area> findByDescription(String description);

    @Override
    Page<Area> findAll(Pageable pageable);

    List<Area> findByStatus(String status);

    Page<Area> findByStatus(String status, Pageable pageable);

    @Query("SELECT a FROM Area a " +
           "WHERE (:areaId IS NULL OR a.areaId = :areaId) " +
           "AND (:description IS NULL OR LOWER(a.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<Area> findByIdOrDescriptionContainingIgnoreCaseAndStatus(
            @Param("areaId") Long areaId,
            @Param("description") String description,
            @Param("status") String status,
            Pageable pageable);

}
