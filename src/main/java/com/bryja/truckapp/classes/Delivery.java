package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @Column(unique=true)
    public String deliveryhash;

    public String description;

    public String deliverycompany;

    public Date addingdate;

    public Date confimdate;

    public int status;  //0-created, 1-in progress, 2-delivered



    @ManyToOne(targetEntity=User.class,fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private User user;

    @Transient
    String username;

    public Delivery() {
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Delivery(String deliveryhash, String deliverycompany, String description) {
        this.deliveryhash = deliveryhash;
        this.deliverycompany = deliverycompany;
        this.status = 0;
        this.addingdate = new Date();
        this.description = description;
    }



    public int getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeliveryhash() {
        return deliveryhash;
    }

    public void setDeliveryhash(String deliveryhash) {
        this.deliveryhash = deliveryhash;
    }

    public String getDeliverycompany() {
        return deliverycompany;
    }

    public void setDeliverycompany(String deliverycompany) {
        this.deliverycompany = deliverycompany;
    }

    public Date getAddingdate() {
        return addingdate;
    }

    public void setAddingdate(Date addingdate) {
        this.addingdate = addingdate;
    }

    public Date getConfimdate() {
        return confimdate;
    }

    public void setConfimdate(Date confimdate) {
        this.confimdate = confimdate;
    }

    public void setStatus(int delivered) {
        this.status = delivered;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
