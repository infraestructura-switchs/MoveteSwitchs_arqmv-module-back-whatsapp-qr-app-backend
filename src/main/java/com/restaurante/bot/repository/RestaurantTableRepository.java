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

    @Query(value = "SELECT rt.tableNumber AS mesa, ts.description AS statusMesa, t.transactionTotal AS totalGeneral " +
            "FROM RestaurantTable rt " +
            "JOIN Transaction t ON rt.tableId = t.tableId " +
            "JOIN TransactionStatus ts ON t.status = ts.transactionStatusId "
            , nativeQuery = true)
    List<Object[]> findAllTablesWithTransactionData();


    @Query(value = "SELECT * " +
            "FROM RESTAURANT_TABLE rt " +
            "WHERE rt.COMPANY_ID =:companyId " +
            "ORDER BY rt.TABLE_NUMBER asc "
            , nativeQuery = true)
    List<RestaurantTable> findAllTablesAsc(Long companyId);
}
