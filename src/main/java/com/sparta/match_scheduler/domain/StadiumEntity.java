package com.sparta.match_scheduler.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stadiums")
public class StadiumEntity extends BaseEntity {

    @Id
    @Column(name = "stadium_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double latitude;
    private double longitude;

    // For JPA
    protected StadiumEntity() {}

    public static StadiumEntity of(String name, double latitude, double longitude) {
        StadiumEntity stadium = new StadiumEntity();
        stadium.name = name;
        stadium.latitude = latitude;
        stadium.longitude = longitude;

        return stadium;
    }

    // getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // toString
    @Override
    public String toString() {
        return "StadiumEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}