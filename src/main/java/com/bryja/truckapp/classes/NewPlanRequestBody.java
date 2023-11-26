package com.bryja.truckapp.classes;

import java.time.LocalDate;
import java.util.List;

public class NewPlanRequestBody {
    private List<Integer> idArray;
    private LocalDate startDate;
    private LocalDate endDate;

    public List<Integer> getIdArray() {
        return idArray;
    }

    public void setIdArray(List<Integer> idArray) {
        this.idArray = idArray;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
