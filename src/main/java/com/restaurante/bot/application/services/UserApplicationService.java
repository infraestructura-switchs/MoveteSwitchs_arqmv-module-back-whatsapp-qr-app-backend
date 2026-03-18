package com.restaurante.bot.application.services;

import com.restaurante.bot.application.ports.incoming.UserUseCase;
import com.restaurante.bot.application.ports.outgoing.UserRepositoryPort;
import com.restaurante.bot.business.interfaces.LoginService;
import com.restaurante.bot.business.interfaces.LoginStrategy;
import com.restaurante.bot.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("userApplicationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserApplicationService implements UserUseCase {
    private final UserRepositoryPort userRepo;
    private final LoginService loginService;
    // other outgoing ports (Role, Company, etc.) can be injected here

    @Override
    public UserDto login(LoginIn loginIn) {
        LoginOut loginOut = loginService.login(loginIn);
        return loginOut.getData();
    }

    @Override
    @Transactional
    public UserDto save(GgpUserSaveAndUpdateDto dto) {
        // business logic adapted from legacy UserServiceImpl
        if (dto.getId() == null) {
            userRepo.findByEmailOrLogin(dto.getEmail(), dto.getLogin())
                    .ifPresent(u -> { throw new IllegalStateException("Correo o Username ya existe"); });
        } else {
            if (userRepo.existsById(dto.getId())) {
                throw new IllegalStateException("El usuario con este ID ya existe");
            }
        }

        // TODO: fetch related entities through other ports (role, company, position, area)
        // for now we assume they are already loaded or use the same repo for simplicity
        com.restaurante.bot.model.User userEntity = new com.restaurante.bot.model.User();
        userEntity.setName(dto.getName());
        userEntity.setEmail(dto.getEmail());
        userEntity.setLogin(dto.getLogin());
        userEntity.setStatus(com.restaurante.bot.util.Constants.ACTIVE_STATUS);
        // relationships
        // userEntity.setRol(...);
        // userEntity.setPosition(...);
        // userEntity.setCompany(...);
        // userEntity.setArea(...);

        if (dto.getPassword() != null) {
            // this util should be moved to a domain/service as well
            String encoded = new com.restaurante.bot.util.Utils().bcryptEncryptor(dto.getPassword());
            userEntity.setPassword(encoded);
        } else {
            String defaultPass = new com.restaurante.bot.util.Utils().bcryptEncryptor(com.restaurante.bot.util.Constants.DEFAULT_PASSWORD);
            userEntity.setPassword(defaultPass);
        }

        com.restaurante.bot.model.User saved = userRepo.save(userEntity);
        return mapUserDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(Long id, GgpUserSaveAndUpdateDto dto) {
        // similar to save but checking existence and unique constraints
        boolean exists = userRepo.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Object not found");
        }
        java.util.ArrayList<Long> excluded = new java.util.ArrayList<>();
        excluded.add(id);
        Page<com.restaurante.bot.model.User> collisions = userRepo.findByEmailOrLoginAndIdNotIn(dto.getEmail(), dto.getLogin(), excluded, org.springframework.data.domain.PageRequest.of(1,10));
        if (collisions.getTotalElements() > 0) {
            throw new IllegalStateException("Correo o Username ya existe");
        }
        com.restaurante.bot.model.User entity = userRepo.findById(id).orElseThrow();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setStatus(dto.getStatus());
        // update relations
        if (dto.getPassword() != null) {
            String encoded = new com.restaurante.bot.util.Utils().bcryptEncryptor(dto.getPassword());
            entity.setPassword(encoded);
        }
        com.restaurante.bot.model.User updated = userRepo.save(entity);
        return mapUserDto(updated);
    }

    @Override
    public UserDto get(Long id) {
        return userRepo.findById(id)
                .map(this::mapUserDto)
                .orElseThrow(() -> new IllegalStateException("Objecto No existe"));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return userRepo.findById(id).map(u -> {
            u.setStatus(com.restaurante.bot.util.Constants.INACTIVE_STATUS);
            userRepo.save(u);
            return true;
        }).orElse(false);
    }

    @Override
    public Page<GgpUserGetAllDto> getAll(Map<String, String> filters) {
        // transform filters to page request and delegate
        int page = Integer.parseInt(filters.getOrDefault("page", "0"));
        int size = Integer.parseInt(filters.getOrDefault("size", "5"));
        String orders = filters.getOrDefault("orders", "ASC");
        String sortBy = filters.getOrDefault("sortBy", "userId");
        org.springframework.data.domain.Sort.Direction direction = org.springframework.data.domain.Sort.Direction.fromString(orders);
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(direction, sortBy);
        org.springframework.data.domain.Pageable pg = org.springframework.data.domain.PageRequest.of(page, size, sort);
        return userRepo.findByStatus(filters.getOrDefault("status", com.restaurante.bot.util.Constants.ACTIVE_STATUS), pg)
                .map(this::mapUserToGetAllDto);
    }

    @Override
    public Page<GgpUserGetAllDto> search(Map<String, String> filters) {
        int page = Integer.parseInt(filters.getOrDefault("page", "0"));
        int size = Integer.parseInt(filters.getOrDefault("size", "5"));
        org.springframework.data.domain.Pageable pg = org.springframework.data.domain.PageRequest.of(page, size);
        Page<com.restaurante.bot.model.User> results = userRepo.customSearch(filters, pg);
        return results.map(this::mapUserToGetAllDto);
    }

    @Override
    public List<GgpUserGetAllDto> getAllWithoutPage(Map<String, String> filters) {
        List<com.restaurante.bot.model.User> users = userRepo.findByStatus(filters.getOrDefault("status", com.restaurante.bot.util.Constants.ACTIVE_STATUS));
        return users.stream().map(this::mapUserToGetAllDto).toList();
    }

    /* helper mapping methods */
    private UserDto mapUserDto(com.restaurante.bot.model.User u) {
        UserDto dto = new UserDto();
        org.springframework.beans.BeanUtils.copyProperties(u, dto, "rol");
        if (u.getRol() != null) {
            dto.setRol(com.restaurante.bot.dto.RolDto.builder()
                    .rolId(u.getRol().getRolId())
                    .name(u.getRol().getName())
                    .build());
            dto.setRolId(u.getRol().getRolId());
        }
        return dto;
    }

    private GgpUserGetAllDto mapUserToGetAllDto(com.restaurante.bot.model.User u) {
        GgpUserGetAllDto dto = new GgpUserGetAllDto();
        dto.setUserId(u.getUserId());
        dto.setName(u.getName());
        dto.setLogin(u.getLogin());
        dto.setPassword(u.getPassword());
        dto.setEmail(u.getEmail());
        if (u.getRol() != null) {
            dto.setRolId(u.getRol().getRolId());
            dto.setRol(com.restaurante.bot.dto.RolDto.builder()
                    .rolId(u.getRol().getRolId())
                    .name(u.getRol().getName())
                    .build());
        }
        dto.setPosition(mapPositionDto(u.getPosition()));
        dto.setCompany(mapCompanyResponseDto(u.getCompany()));
        dto.setArea(mapAreaDto(u.getArea()));
        dto.setStatus(u.getStatus());
        return dto;
    }

    private PositionDto mapPositionDto(com.restaurante.bot.model.Position position) {
        if (position == null) {
            return null;
        }
        return PositionDto.builder()
                .positionId(position.getPositionId())
                .description(position.getDescription())
                .status(position.getStatus())
                .build();
    }

    private CompanyResponseDTO mapCompanyResponseDto(com.restaurante.bot.model.Company company) {
        if (company == null) {
            return null;
        }
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .companyName(company.getName())
                .logo(company.getLogo())
                .whatsappNumber(company.getNumberWhatsapp())
                .latitude(company.getLatitude())
                .longitude(company.getLongitude())
                .baseValue(company.getBaseValue())
                .aditionalValue(company.getAdditionalValue())
                .status(company.getStatus())
                .externalId(company.getExternalCompanyId())
                .cityId(company.getCityId())
                .apiKey(company.getApiKey())
                .rappyId(company.getRpIntegrationId())
                .numberId(company.getNumberId())
                .tokenMetaQr(company.getTokenMeta())
                .numberBotDelivery(company.getNumberBotDelivery())
                .numberBotMesa(company.getNumberBotMesa())
                .statusRappy(company.getStatusIntegrationRp())
                .tokenMetaDelivery(company.getTokenMetaDelivery())
                .landingTemplate(company.getLandingTemplate())
                .build();
    }

    private AreaDto mapAreaDto(com.restaurante.bot.model.Area area) {
        if (area == null) {
            return null;
        }
        return AreaDto.builder()
                .id(area.getAreaId())
                .description(area.getDescription())
                .status(area.getStatus())
                .build();
    }
}
