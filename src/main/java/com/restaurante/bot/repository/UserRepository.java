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

/**
 * Repository for User entity
 * Queries are compatible with both MySQL and Oracle databases
 */
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByEmailOrLogin(String email, String login);

	@Query("from User where (email=:email or login=:login) and userId not in (:userIds)")
	Page<User> findByEmailOrLoginAndIdNotIn(@Param("email") String email, @Param("login") String login,
			@Param("userIds") Collection<Long> userIds, Pageable pageable);

	Optional<User> findByLogin(String login);

	@Override
	Page<User> findAll(Pageable pageable);

	Page<User> findByStatus(String status, Pageable pageable);

	Page<User> findByStatusAndStatusNot(String status, String excludedStatus, Pageable pageable);

	List<User> findByStatus(String status);

	List<User> findByStatusAndStatusNot(String status, String excludedStatus);

	@Query("SELECT u FROM User u WHERE u.company.id = :companyId")
	User findUserByCompany(@Param("companyId") Long companyId);

	/**
	 * Custom search for User entity with multiple filter criteria
	 * @param userId User ID (null for no filter)
	 * @param name User name pattern (null for no filter)
	 * @param email Email pattern (null for no filter)
	 * @param login Login pattern (null for no filter)
	 * @param companyName Company name pattern (null for no filter)
	 * @param positionDescription Position description pattern (null for no filter)
	 * @param areaDescription Area description pattern (null for no filter)
	 * @param status User status (null for no filter)
	 * @param pageable Pagination and sorting information
	 * @return Page of users matching the criteria
	 */
	@Query("SELECT u FROM User u " +
			"LEFT JOIN u.company c " +
			"LEFT JOIN u.position p " +
			"LEFT JOIN u.area a " +
			"WHERE (:userId IS NULL OR u.userId = :userId) " +
			"AND (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
			"AND (:login IS NULL OR LOWER(u.login) LIKE LOWER(CONCAT('%', :login, '%'))) " +
			"AND (:companyName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :companyName, '%'))) " +
			"AND (:positionDescription IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :positionDescription, '%'))) " +
			"AND (:areaDescription IS NULL OR LOWER(a.description) LIKE LOWER(CONCAT('%', :areaDescription, '%'))) " +
			"AND (:status IS NULL OR u.status = :status) " +
			"AND (u.status IS NULL OR UPPER(u.status) <> 'DELETED')")
	Page<User> searchUsers(
			@Param("userId") Long userId,
			@Param("name") String name,
			@Param("email") String email,
			@Param("login") String login,
			@Param("companyName") String companyName,
			@Param("positionDescription") String positionDescription,
			@Param("areaDescription") String areaDescription,
			@Param("status") String status,
			Pageable pageable);

	/**
	 * Deprecated method - use searchUsers() instead
	 * Kept for backward compatibility
	 */
	@Deprecated(since = "1.1.0", forRemoval = true)
	default Page<User> findByUserIdOrNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLoginContainingIgnoreCaseOrCompany_CompanyNameContainingIgnoreCaseOrPosition_DescriptionContainingIgnoreCaseOrArea_DescriptionContainingIgnoreCaseOrAndStatus(
			Long userId, String name, String email, String login, String companyName,
			String positionDescription, String areaDescription, String status, Pageable pageable) {
		return searchUsers(userId, name, email, login, companyName, positionDescription, areaDescription, status, pageable);
	}

}
