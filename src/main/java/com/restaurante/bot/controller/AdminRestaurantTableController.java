package com.restaurante.bot.controller;

import com.restaurante.bot.application.ports.incoming.AdminRestaurantTableUseCase;
import com.restaurante.bot.dto.AdminChangeStatusTableDTO;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Restaurant Table REST Controller - handles table management endpoints.
 * 
 * REFACTORED - Clean Architecture Pattern:
 * This controller is a THIN ADAPTER that:
 * - Maps HTTP requests to DTOs
 * - Delegates to RestaurantTableUseCase (business logic)
 * - Maps responses back to HTTP
 * - NO business logic
 * - NO state machine implementation
 * 
 * Note on State Machine:
 * Although we still have 7 separate endpoints for backward compatibility,
 * the underlying RestaurantTableApplicationService validates all state transitions
 * using RestaurantTableStateValidator to prevent invalid table states.
 * 
 * Future Improvement:
 * Consider consolidating these 7 endpoints into a single /change-status endpoint
 * that accepts the target state as a parameter.
 */
@RestController
@RequestMapping("/${app.request.mapping}/admin/restauranttable")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AdminRestaurantTableController {

    private final AdminRestaurantTableUseCase adminRestaurantTableUseCase;

    /**
     * List all restaurant tables
     * HTTP: GET /restauranttable/get
     */
    @GetMapping("/get")
    public List<RestaurantTable> ListarMesas() {
        log.info("Listing all restaurant tables");
        return adminRestaurantTableUseCase.ListarMesas();
    }

    /**
     * Add new restaurant table
     * HTTP: POST /restauranttable/createTable?tableNumber={tableNumber}
     */
    @PostMapping("/createTable")
    public ResponseEntity<RestaurantTable> addTable(@RequestParam Long tableNumber) {
        log.info("Creating new table: {}", tableNumber);
        return new ResponseEntity<>(adminRestaurantTableUseCase.addTable(tableNumber), HttpStatus.CREATED);
    }

    /**
     * Delete restaurant table
     * HTTP: DELETE /restauranttable/delete/{tableId}
     */
    @DeleteMapping("/delete/{tableId}")
    public ResponseEntity<GenericResponse> deleteTable(@PathVariable("tableId") Long tableId) {
        log.info("Deleting table: {}", tableId);
        return new ResponseEntity<>(adminRestaurantTableUseCase.deleteTable(tableId), HttpStatus.OK);
    }

    /**
     * Change table status to OCCUPIED
     * HTTP: POST /restauranttable/change/status-ocuped
     */
    @PostMapping("/change/status-ocuped")
    public ResponseEntity<RestaurantTable> changeStatusOcuped(@RequestBody AdminChangeStatusTableDTO adminChangeStatusTableDTO) {
        log.info("Changing table {} to OCCUPIED status", adminChangeStatusTableDTO.getTableNumber());
        return new ResponseEntity<>(
                adminRestaurantTableUseCase.changeStatusOcuped(adminChangeStatusTableDTO),
            HttpStatus.OK);
    }

    /**
     * Change table status to AVAILABLE (FREE)
     * HTTP: POST /restauranttable/change/status-free?tableNumber={tableNumber}
     */
    @PostMapping("/change/status-free")
    public ResponseEntity<RestaurantTable> changeStatusFree(@RequestParam Long tableNumber) {
        log.info("Changing table {} to AVAILABLE status", tableNumber);
        return new ResponseEntity<>(adminRestaurantTableUseCase.changeStatusFree(tableNumber), HttpStatus.OK);
    }

    /**
     * Change table status to REQUESTING_SERVICE
     * HTTP: POST /restauranttable/change/status-requesting-service
     */
    @PostMapping("/change/status-requesting-service")
    public ResponseEntity<RestaurantTable> changeStatusRequestingService(@RequestBody NumberDTO tableNumber) {
        log.info("Changing table {} to REQUESTING_SERVICE status", tableNumber.getTableNumber());
        return new ResponseEntity<>(
                adminRestaurantTableUseCase.changeStatusRequestingService(tableNumber),
            HttpStatus.OK);
    }

    /**
     * Change table status to RESERVED
     * HTTP: POST /restauranttable/change/status-reserved?tableNumber={tableNumber}
     */
    @PostMapping("/change/status-reserved")
    public ResponseEntity<RestaurantTable> changeStatusReserved(@RequestParam Long tableNumber) {
        log.info("Changing table {} to RESERVED status", tableNumber);
        return new ResponseEntity<>(adminRestaurantTableUseCase.changeStatusReserved(tableNumber), HttpStatus.OK);
    }

    /**
     * Change table status to PAYING
     * HTTP: POST /restauranttable/change/status-pay
     */
    @PostMapping("/change/status-pay")
    public ResponseEntity<RestaurantTable> changeStatusPay(@RequestBody NumberDTO tableNumber) {
        log.info("Changing table {} to PAYING status", tableNumber.getTableNumber());
        return new ResponseEntity<>(adminRestaurantTableUseCase.changeStatusPay(tableNumber), HttpStatus.OK);
    }
}
