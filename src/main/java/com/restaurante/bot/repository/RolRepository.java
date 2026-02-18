package com.restaurante.bot.repository;

import com.restaurante.bot.dto.RolGetAllDto;
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


    Page<Rol> findByStatus(String status, Pageable pageable);

    @Query("SELECT new com.restaurante.bot.dto.RolGetAllDto(r.rolId, r.name, r.status) FROM Rol r WHERE r.status = 'ACTIVE' ")
    Page<RolGetAllDto> findAllByStatus(Pageable pageable);


    @Query("SELECT r FROM Rol r WHERE (r.rolId = :id OR r.name LIKE %:name%) AND r.status = 'ACTIVE' ")
    Page<Rol> findByIdOrNameContainingIgnoreCaseAndStatus(Long id, String name, Pageable pageable);



}
