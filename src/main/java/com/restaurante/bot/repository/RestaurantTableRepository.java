package com.restaurante.bot.repository;

import com.restaurante.bot.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

        RestaurantTable findByTableNumberAndCompanyId(Long tableNumber, Long companyId);

        Optional<RestaurantTable> findByTableId(Integer tableId);

        @Query("SELECT MAX(rt.tableNumber) FROM RestaurantTable rt")
        Long findHighestTableNumber();

        Boolean existsByTableNumberAndCompanyId(Long tableNumber, Long companyId);

        @Query("SELECT rt.tableNumber AS mesa, ts.description AS statusMesa, t.transactionTotal AS totalGeneral " +
                        "FROM RestaurantTable rt, Transaction t, TransactionStatus ts " +
                        "WHERE rt.tableId = t.tableId AND t.status = ts.transactionStatusId")
        List<Object[]> findAllTablesWithTransactionData();

        @Query("SELECT rt FROM RestaurantTable rt WHERE rt.companyId = :companyId ORDER BY rt.tableNumber ASC")
        List<RestaurantTable> findAllTablesAsc(Long companyId);
}
