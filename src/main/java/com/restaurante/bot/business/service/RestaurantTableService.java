package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.RestaurantTableUseCase;
import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.dto.ChangeStatusTableDTO;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.domain.exception.DomainException;
import com.restaurante.bot.domain.exception.DomainErrorCode;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.SubscriptionRepository;
import com.restaurante.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantTableService implements RestaurantTableInterface, RestaurantTableUseCase {

    private final RestaurantTableRepository restaurantTableRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public List<RestaurantTable> ListarMesas(){
        Company company = getAuthenticatedCompany();
        return restaurantTableRepository.findAllTablesAsc(company.getId());
    }

    @Override
    public RestaurantTable addTable(Long tableNumber) {
        Company company = getAuthenticatedCompany();

        if (restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber, company.getId())) {

            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "La mesa ya existe");

        }else {

            RestaurantTable newTable = new RestaurantTable();
            newTable.setTableNumber(tableNumber);
            newTable.setStatus(1L);
            newTable.setCompanyId(company.getId());
            return restaurantTableRepository.save(newTable);

        }

    }

    @Override
    public GenericResponse deleteTable(Long tableId) {

        if (!restaurantTableRepository.existsById(tableId)) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Mesa no resgistrada en la base de datos");
        }
        restaurantTableRepository.deleteById(tableId);
        return new GenericResponse("Mesa eliminada con exito", 200L);
    }

    @Override
    public RestaurantTable changeStatusOcuped(ChangeStatusTableDTO changeStatusTableDTO) {
        validateTableNumber(changeStatusTableDTO.getTableNumber());
        Company company = getAuthenticatedCompany();
        RestaurantTable table = getTableByCompanyOrThrow(changeStatusTableDTO.getTableNumber(), company.getId());
        table.setStatus(2L);

        String title = "Mesa " + changeStatusTableDTO.getTableNumber() + " - Estado actualizado";
        String body = "La mesa " + changeStatusTableDTO.getTableNumber() + " ha cambiado de estado. Revisa la lista de mesas.";
        notifyCompanyUser(company.getId(), title, body);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusFree(Long tableNumber) {
        validateTableNumber(tableNumber);
        Company company = getAuthenticatedCompany();
        RestaurantTable table = getTableByCompanyOrThrow(tableNumber, company.getId());
        table.setStatus(1L);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusRequestingService(NumberDTO tableNumber) {
        validateTableNumber(tableNumber.getTableNumber());
        Company company = getAuthenticatedCompany();
        RestaurantTable table = getTableByCompanyOrThrow(tableNumber.getTableNumber(), company.getId());
        table.setStatus(3L);

        Long tn = tableNumber.getTableNumber();

        String titleReq = "Mesa " + tn + " - Solicitud de servicio";
        String bodyReq = "El cliente en la mesa " + tn + " ha solicitado servicio. Atiéndelo, por favor.";
        notifyCompanyUser(company.getId(), titleReq, bodyReq);

        return restaurantTableRepository.save(table);
    }

    public RestaurantTable changeStatusReserved(Long tableNumber) {
        /*
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Mesa no resgistrada en la base de datos");
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(4L);

         */

        return null;
    }

    @Override
    public RestaurantTable changeStatusPay(NumberDTO tableNumber) {
        validateTableNumber(tableNumber.getTableNumber());
        Company company = getAuthenticatedCompany();
        RestaurantTable table = getTableByCompanyOrThrow(tableNumber.getTableNumber(), company.getId());
        table.setStatus(5L);

        Long tnPay = tableNumber.getTableNumber();

        String titlePay = "Mesa " + tnPay + " - Pago solicitado";
        String bodyPay = "La mesa " + tnPay + " ha solicitado pagar. Revisar y procesar pago.";
        notifyCompanyUser(company.getId(), titlePay, bodyPay);

        return restaurantTableRepository.save(table);
    }

    private Company getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long tokenCompanyId)) {
            throw new DomainException(DomainErrorCode.UNAUTHORIZED, "No autenticado");
        }

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Compañia no recnocida en la base de datos");
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
        if (company == null) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Compañia no recnocida en la base de datos");
        }
        return company;
    }

    private RestaurantTable getTableByCompanyOrThrow(Long tableNumber, Long companyId) {
        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber, companyId);
        if (table == null) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Mesa no resgistrada en la base de datos");
        }
        return table;
    }

    private void validateTableNumber(Long tableNumber) {
        if (tableNumber == null || tableNumber <= 0) {
            throw new DomainException(DomainErrorCode.INVALID_REQUEST, "Campos con valor invalido: tableNumber");
        }
    }

    private void notifyCompanyUser(Long companyId, String title, String body) {
        User user = userRepository.findUserByCompany(companyId);
        if (user == null) {
            log.warn("No user found for company {} - skipping notification", companyId);
            return;
        }

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());
        if (subscription == null || subscription.getToken() == null) {
            log.warn("No subscription/token found for user {} - skipping notification", user.getUserId());
            return;
        }

        notificationService.sendNotificationToClient(subscription.getToken(), title, body);
    }
}
