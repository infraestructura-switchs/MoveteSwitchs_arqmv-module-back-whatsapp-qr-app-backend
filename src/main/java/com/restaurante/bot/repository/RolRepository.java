package com.restaurante.bot.repository;

import com.restaurante.bot.model.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface RolRepository extends JpaRepository<Rol, Long> {


    Optional<Rol> findByName(String name);
    @Override
    Page<Rol> findAll(Pageable pageable);

    List<Rol> findByStatus(String status);


    // Replaced select-new DTO projection with entity query; mapping to DTO happens in the service layer
    Page<Rol> findByStatus(String status, Pageable pageable);


    @Query("SELECT r FROM Rol r WHERE (r.rolId = :id OR r.name LIKE %:name%) AND r.status = 'ACTIVE' ")
    Page<Rol> findByIdOrNameContainingIgnoreCaseAndStatus(Long id, String name, Pageable pageable);



}
