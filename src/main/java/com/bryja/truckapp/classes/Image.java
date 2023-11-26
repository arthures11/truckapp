package com.bryja.truckapp.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Base64;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;


    public byte[] avatar;


    @ManyToOne(targetEntity=Workday.class,fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private Workday workday;

    public Image() {
    }

    public Image(byte[] avatar, Workday workday) {
        this.avatar = avatar;
        this.workday = workday;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Workday getWorkday() {
        return workday;
    }

    public void setWorkday(Workday workday) {
        this.workday = workday;
    }
}
