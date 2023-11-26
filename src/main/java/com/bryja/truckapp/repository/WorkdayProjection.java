package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Image;
import com.bryja.truckapp.classes.Schedule;

import java.util.Collection;
import java.util.List;

public interface WorkdayProjection {

    Long getId();
    String getDate();
    String getDescription();
    String getUsername();
    String getSchedule();
    List<Image> getImage();
    Long getTotalElements();
    int getTotalPages();

}
