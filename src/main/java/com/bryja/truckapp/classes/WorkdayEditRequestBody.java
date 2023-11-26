package com.bryja.truckapp.classes;

import java.time.LocalDate;

public class WorkdayEditRequestBody {

    private LocalDate date;
    private String schedule;
    private String description;


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
