package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Workday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;


    public Date date;

    @Nullable
    public String description;

    @Nullable
    @OneToMany(targetEntity= Image.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "workday")
    private List<Image> images = new ArrayList<Image>();

    @ManyToOne(targetEntity=Schedule.class,fetch = FetchType.EAGER)
    public Schedule schedule;



    public Workday() {
    }

    public Workday(Date date, User user) {
        this.date = date;
        this.user = user;
    }

    @ManyToOne(targetEntity=User.class,fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public Schedule getSchedule() {
        return schedule;
    }


    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
