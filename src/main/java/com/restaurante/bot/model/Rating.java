package com.restaurante.bot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @SequenceGenerator(name = "RATING_SEQ", sequenceName = "RATING_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RATING_SEQ")
    @Column(name = "rating_id")
    private Integer ratingId;

    @Column(name = "table_id")
    private Integer tableId;

    @Column(name = "qualification")
    private String Qualification;

    @ManyToOne
    @JoinColumn(name = "table_id", referencedColumnName = "table_id", insertable = false, updatable = false)
    private RestaurantTable restaurantTable;

    public Rating() {}

    public Rating(Integer ratingId, Integer tableId, String qualification, RestaurantTable restaurantTable) {
        this.ratingId = ratingId;
        this.tableId = tableId;
        this.Qualification = qualification;

    }

    public Integer getRatingId() {
        return ratingId;
    }
    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }
    public Integer getTableId() {
        return tableId;
    }
    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    public String getQualification() {
        return Qualification;
    }
    public void setQualification(String qualification) {
        Qualification = qualification;
    }
    public RestaurantTable getRestaurantTable() {
        return restaurantTable;
    }
    public void setRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTable = restaurantTable;
    }
}

