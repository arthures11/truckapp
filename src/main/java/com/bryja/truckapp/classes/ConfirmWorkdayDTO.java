package com.bryja.truckapp.classes;

import java.util.List;

public class ConfirmWorkdayDTO {
    private Workday workday;
    private List<Schedule> schedules;
    private List<Delivery> deliveries;

    private String date;

    public ConfirmWorkdayDTO() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ConfirmWorkdayDTO(Workday workday, List<Schedule> schedules, List<Delivery> deliveries) {
        this.workday = workday;
        this.schedules = schedules;
        this.deliveries = deliveries;
    }

    public Workday getWorkday() {
        return workday;
    }

    public void setWorkday(Workday workday) {
        this.workday = workday;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
}
