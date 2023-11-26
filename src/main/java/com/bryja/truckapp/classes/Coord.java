package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Coord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")Long id;

    public Date date;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity=Truck.class)
    @JsonBackReference
    @JsonIgnore
    private Truck truck;

    public double longitude;

    public double  latitude;

    public double getLongitude() {
        return longitude;
    }

    public Coord(Date date, Truck truck, double longitude, double latitude) {
        this.date = date;
        this.truck = truck;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Coord() {
    }

    public Coord(Date date, Truck truck) {
        this.date = date;
        this.truck = truck;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }
}
