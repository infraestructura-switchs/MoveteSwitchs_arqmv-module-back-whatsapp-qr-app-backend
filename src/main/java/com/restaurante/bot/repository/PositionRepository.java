package com.restaurante.bot.repository;

import com.restaurante.bot.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Position entity
 * Queries are compatible with both MySQL and Oracle databases
 */
public interface PositionRepository extends JpaRepository<Position, Long> {

	Optional<Position> findByDescription(String description);

	@Override
	Page<Position> findAll(Pageable pageable);

	List<Position> findByStatus(String status);

	Page<Position> findByStatus(String status, Pageable pageable);

	/**
	 * Search Position by ID, description and status
	 * @param id Position ID (null for no filter on ID)
	 * @param description Position description pattern (null for no filter)
	 * @param status Position status (null for no filter)
	 * @param pageable Pagination and sorting information
	 * @return Page of positions matching the criteria
	 */
	@Query(value = "SELECT p FROM Position p " +
			"WHERE (:id IS NULL OR p.positionId = CAST(:id AS LONG)) " +
			"AND (:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
			"AND (:status IS NULL OR p.status = :status)",
		countQuery = "SELECT COUNT(p) FROM Position p " +
			"WHERE (:id IS NULL OR p.positionId = CAST(:id AS LONG)) " +
			"AND (:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) " +
			"AND (:status IS NULL OR p.status = :status)")
	Page<Position> findByIdOrDescriptionContainingIgnoreCaseAndStatus(
			@Param("id") String id,
			@Param("description") String description,
			@Param("status") String status,
			Pageable pageable);

}
