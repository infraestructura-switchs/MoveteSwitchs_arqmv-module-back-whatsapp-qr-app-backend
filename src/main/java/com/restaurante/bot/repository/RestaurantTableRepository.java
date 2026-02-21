package com.restaurante.bot.repository;

import com.restaurante.bot.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

        RestaurantTable findByTableNumberAndCompanyId(Long tableNumber, Long companyId);

        Optional<RestaurantTable> findByTableId(Integer tableId);

        @Query(value = "SELECT MAX(table_number) AS highest_table_number " +
                        "FROM restaurant_table ", nativeQuery = true)
        Long findHighestTableNumber();

        Boolean existsByTableNumberAndCompanyId(Long tableNumber, Long companyId);

        @Query(value = "SELECT rt.table_number AS mesa, ts.description AS statusMesa, t.transaction_total AS totalGeneral "
                        +
                        "FROM restaurant_table rt " +
                        "JOIN transaction t ON rt.table_id = t.table_id " +
                        "JOIN transaction_status ts ON t.status = ts.transaction_status_id ", nativeQuery = true)
        List<Object[]> findAllTablesWithTransactionData();

        @Query(value = "SELECT * " +
                        "FROM restaurant_table rt " +
                        "WHERE rt.company_id =:companyId " +
                        "ORDER BY rt.table_number asc ", nativeQuery = true)
        List<RestaurantTable> findAllTablesAsc(Long companyId);
}
