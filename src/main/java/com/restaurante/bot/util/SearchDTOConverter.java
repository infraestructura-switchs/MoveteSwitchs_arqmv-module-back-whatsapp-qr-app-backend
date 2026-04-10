package com.restaurante.bot.util;

import com.restaurante.bot.dto.*;
import com.restaurante.bot.util.Constants;
import com.restaurante.bot.util.SortConstants;

import java.util.Map;

/**
 * Utility class to convert Map<String, String> search parameters to typed SearchDTO objects
 * This class provides backward compatibility while migrating to typed DTOs
 */
public class SearchDTOConverter {

    /**
     * Convert Map to PositionSearchDTO
     */
    public static PositionSearchDTO toPositionSearch(Map<String, String> queryParams) {
        if (queryParams == null) {
            PositionSearchDTO dto = new PositionSearchDTO();
            dto.validate();
            return dto;
        }

        PositionSearchDTO dto = new PositionSearchDTO();
        dto.setPage(getInt(queryParams, "page", 0));
        dto.setSize(getInt(queryParams, "size", 5));
        dto.setOrders(getString(queryParams, "orders", SortConstants.ASC));
        dto.setSortBy(getString(queryParams, "sortBy", "positionId"));
        dto.setStatus(getString(queryParams, "status", Constants.ACTIVE_STATUS));
        dto.setId(getLong(queryParams, "id", null));
        dto.setDescription(getString(queryParams, "description", null));
        dto.validate();
        return dto;
    }

    /**
     * Convert Map to RolSearchDTO
     */
    public static RolSearchDTO toRolSearch(Map<String, String> queryParams) {
        if (queryParams == null) {
            RolSearchDTO dto = new RolSearchDTO();
            dto.validate();
            return dto;
        }

        RolSearchDTO dto = new RolSearchDTO();
        dto.setPage(getInt(queryParams, "page", 0));
        dto.setSize(getInt(queryParams, "size", 5));
        dto.setOrders(getString(queryParams, "orders", SortConstants.ASC));
        dto.setSortBy(getString(queryParams, "sortBy", "rolId"));
        dto.setStatus(getString(queryParams, "status", Constants.ACTIVE_STATUS));
        dto.setRolId(getLong(queryParams, "rolId", null));
        dto.setName(getString(queryParams, "name", null));
        dto.validate();
        return dto;
    }

    /**
     * Convert Map to AreaSearchDTO
     */
    public static AreaSearchDTO toAreaSearch(Map<String, String> queryParams) {
        if (queryParams == null) {
            AreaSearchDTO dto = new AreaSearchDTO();
            dto.validate();
            return dto;
        }

        AreaSearchDTO dto = new AreaSearchDTO();
        dto.setPage(getInt(queryParams, "page", 0));
        dto.setSize(getInt(queryParams, "size", 5));
        dto.setOrders(getString(queryParams, "orders", SortConstants.ASC));
        dto.setSortBy(getString(queryParams, "sortBy", "areaId"));
        dto.setStatus(getString(queryParams, "status", Constants.ACTIVE_STATUS));
        dto.setAreaId(getLong(queryParams, "areaId", null));
        dto.setDescription(getString(queryParams, "description", null));
        dto.validate();
        return dto;
    }

    /**
     * Convert Map to UserSearchDTO
     */
    public static UserSearchDTO toUserSearch(Map<String, String> queryParams) {
        if (queryParams == null) {
            UserSearchDTO dto = new UserSearchDTO();
            dto.validate();
            return dto;
        }

        UserSearchDTO dto = new UserSearchDTO();
        dto.setPage(getInt(queryParams, "page", 0));
        dto.setSize(getInt(queryParams, "size", 5));
        dto.setOrders(getString(queryParams, "orders", SortConstants.ASC));
        dto.setSortBy(getString(queryParams, "sortBy", "userId"));
        dto.setStatus(getString(queryParams, "status", Constants.ACTIVE_STATUS));
        dto.setUserId(getLong(queryParams, "userId", null));
        dto.setName(getString(queryParams, "name", null));
        dto.setEmail(getString(queryParams, "email", null));
        dto.setLogin(getString(queryParams, "login", null));
        dto.setCompanyName(getString(queryParams, "companyName", null));
        dto.setPositionDescription(getString(queryParams, "description", null));
        dto.setAreaDescription(getString(queryParams, "areaDescription", null));
        dto.validate();
        return dto;
    }

    /**
     * Convert Map to ProductSearchDTO
     */
    public static ProductSearchDTO toProductSearch(Map<String, String> queryParams) {
        if (queryParams == null) {
            ProductSearchDTO dto = new ProductSearchDTO();
            dto.validate();
            return dto;
        }

        ProductSearchDTO dto = new ProductSearchDTO();
        dto.setPage(getInt(queryParams, "page", 0));
        dto.setSize(getInt(queryParams, "size", 5));
        dto.setOrders(getString(queryParams, "orders", SortConstants.ASC));
        dto.setSortBy(getString(queryParams, "sortBy", "productId"));
        dto.setStatus(getString(queryParams, "status", Constants.ACTIVE_STATUS));
        dto.setCompanyId(getLong(queryParams, "companyId", null));
        dto.setExternalCompanyId(getLong(queryParams, "externalCompanyId", null));
        dto.setName(getString(queryParams, "name", null));
        dto.setCategoryId(getLong(queryParams, "categoryId", null));
        dto.validate();
        return dto;
    }

    // Helper methods
    private static String getString(Map<String, String> map, String key, String defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        String value = map.get(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private static int getInt(Map<String, String> map, String key, int defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        String value = map.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static Long getLong(Map<String, String> map, String key, Long defaultValue) {
        if (map == null || !map.containsKey(key)) {
            return defaultValue;
        }
        String value = map.get(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
