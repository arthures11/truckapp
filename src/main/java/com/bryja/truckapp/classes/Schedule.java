package com.bryja.truckapp.classes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public String name;
    public Long breaks_every_minutes;

    public Long break_minutes;

    public Long worktime_minutes;

    public Schedule() {
    }

    public Schedule(Long breaks_every_minutes, Long break_minutes, Long worktime_minutes, String name) {
        this.name = name;
        this.breaks_every_minutes = breaks_every_minutes;
        this.break_minutes = break_minutes;
        this.worktime_minutes = worktime_minutes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBreaks_every_minutes() {
        return breaks_every_minutes;
    }

    public void setBreaks_every_minutes(Long breaks_every_minutes) {
        this.breaks_every_minutes = breaks_every_minutes;
    }

    public Long getBreak_minutes() {
        return break_minutes;
    }

    public void setBreak_minutes(Long break_minutes) {
        this.break_minutes = break_minutes;
    }

    public Long getWorktime_minutes() {
        return worktime_minutes;
    }

    public void setWorktime_minutes(Long worktime_minutes) {
        this.worktime_minutes = worktime_minutes;
    }
}
