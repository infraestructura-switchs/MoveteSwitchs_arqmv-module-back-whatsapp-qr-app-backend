package com.restaurante.bot.repository;

import com.restaurante.bot.model.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Rol entity
 * Queries are compatible with both MySQL and Oracle databases
 */
public interface RolRepository extends JpaRepository<Rol, Long> {

	Optional<Rol> findByName(String name);

	@Override
	Page<Rol> findAll(Pageable pageable);

	List<Rol> findByStatus(String status);

	/**
	 * Search Rol with pagination and current status
	 */
	Page<Rol> findByStatus(String status, Pageable pageable);

	/**
	 * Search Rol by ID and/or name with status filter
	 * @param rolId Role ID (null for no filter on ID)
	 * @param name Role name pattern (null for no filter)
	 * @param pageable Pagination and sorting information
	 * @return Page of roles matching the criteria
	 */
	@Query("SELECT r FROM Rol r " +
			"WHERE (:rolId IS NULL OR r.rolId = :rolId) " +
			"AND (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND r.status = 'ACTIVE'")
	Page<Rol> findByIdOrNameContainingIgnoreCaseAndStatus(
			@Param("rolId") Long rolId,
			@Param("name") String name,
			Pageable pageable);

}
