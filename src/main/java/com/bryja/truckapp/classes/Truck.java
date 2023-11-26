package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")Long id;
    //  @Column(name = "user_id")public Long id2;
    public String name;


    @OneToMany(targetEntity=Coord.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "truck")
    private List<Coord> coords = new ArrayList<Coord>();

    public double distance_traveled;

    public String company;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Transient
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @ManyToOne(targetEntity=User.class,fetch = FetchType.EAGER)
    @JsonBackReference
    @JsonIgnore
    private User user;

    public Truck(String name) {
        this.name = name;
    }

    public Truck() {
    }

    public Truck(String name, double distance_traveled, String company) {
        this.name = name;
        this.distance_traveled = distance_traveled;
        this.company = company;
    }

    public double getDistance_traveled() {
        return distance_traveled;
    }

    public void setDistance_traveled(double distance_traveled) {
        this.distance_traveled = distance_traveled;
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void setCoords(List<Coord> coords) {
        this.coords = coords;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
