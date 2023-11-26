package com.bryja.truckapp.classes;

import java.util.List;

public class ConfirmDayRequestBody {

    Long workdayId;

    Long scheduleId;

    List<Integer> deliveryIds;

    String description;

    String file;

    public Long getWorkdayId() {
        return workdayId;
    }

    public void setWorkdayId(Long workdayId) {
        this.workdayId = workdayId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public List<Integer> getDeliveryIds() {
        return deliveryIds;
    }

    public void setDeliveryIds(List<Integer> deliveryIds) {
        this.deliveryIds = deliveryIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
