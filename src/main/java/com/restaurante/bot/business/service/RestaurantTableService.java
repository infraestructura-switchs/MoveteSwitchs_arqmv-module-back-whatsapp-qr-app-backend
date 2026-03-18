package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.incoming.RestaurantTableUseCase;
import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.dto.ChangeStatusTableDTO;
import com.restaurante.bot.dto.NumberDTO;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.*;
import com.restaurante.bot.repository.CompanyRepository;
import com.restaurante.bot.repository.RestaurantTableRepository;
import com.restaurante.bot.repository.SubscriptionRepository;
import com.restaurante.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        return restaurantTableRepository.findAllTablesAsc(company.getId());
    }

    @Override
    public RestaurantTable addTable(Long tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        if (restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber, company.getId())) {

            throw new GenericException("La mesa ya existe", HttpStatus.BAD_REQUEST);

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
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        restaurantTableRepository.deleteById(tableId);
        return new GenericResponse("Mesa eliminada con exito", 200L);
    }

    @Override
    public RestaurantTable changeStatusOcuped(ChangeStatusTableDTO changeStatusTableDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }
        log.info("external company id: " + changeStatusTableDTO.getCompanyId());
        Company company = companyRepository.findByExternalCompanyId(changeStatusTableDTO.getCompanyId());
        log.info("company id: " + company.getId());

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());


        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(changeStatusTableDTO.getTableNumber(), company.getId());
        table.setStatus(2L);

        String title = "Mesa " + changeStatusTableDTO.getTableNumber() + " - Estado actualizado";
        String body = "La mesa " + changeStatusTableDTO.getTableNumber() + " ha cambiado de estado. Revisa la lista de mesas.";
        notificationService.sendNotificationToClient(subscription.getToken(), title, body);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusFree(Long tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        if (!restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber, company.getId())) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber, company.getId());
        table.setStatus(1L);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusRequestingService(NumberDTO tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());

        if (!restaurantTableRepository.existsByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId())) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }

        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId());
        table.setStatus(3L);

        Long tn = tableNumber.getTableNumber();
        String titleReq = "Mesa " + tn + " - Solicitud de servicio";
        String bodyReq = "El cliente en la mesa " + tn + " ha solicitado servicio. Atiéndelo, por favor.";
        notificationService.sendNotificationToClient(subscription.getToken(), titleReq, bodyReq);

        return restaurantTableRepository.save(table);
    }

    public RestaurantTable changeStatusReserved(Long tableNumber) {
        /*
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(4L);

         */

        return null;
    }

    @Override
    public RestaurantTable changeStatusPay(NumberDTO tableNumber) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long tokenCompanyId = (Long) authentication.getPrincipal();

        if (!companyRepository.existsByExternalCompanyId(tokenCompanyId)) {
            throw new GenericException("Compañia no recnocida en la base de datos", HttpStatus.BAD_REQUEST);
        }

        Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);

        User user = userRepository.findUserByCompany(company.getId());

        Subscription subscription = subscriptionRepository.findByUserId(user.getUserId());


        RestaurantTable table = restaurantTableRepository.findByTableNumberAndCompanyId(tableNumber.getTableNumber(), company.getId());
        table.setStatus(5L);

        Long tnPay = tableNumber.getTableNumber();
        String titlePay = "Mesa " + tnPay + " - Pago solicitado";
        String bodyPay = "La mesa " + tnPay + " ha solicitado pagar. Revisar y procesar pago.";
        notificationService.sendNotificationToClient(subscription.getToken(), titlePay, bodyPay);

        return restaurantTableRepository.save(table);
    }
}
