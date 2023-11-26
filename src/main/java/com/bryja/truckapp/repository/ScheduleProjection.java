package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.UserDTO;

public interface ScheduleProjection {

    Long getId();
    String getName();
    Long getBreakEveryMin();
    Long getTotalBreak();
    Long getTotalWorktime();
    Long getTotalElements();
    int getTotalPages();

}
