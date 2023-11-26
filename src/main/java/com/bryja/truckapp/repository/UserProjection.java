package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Truck;

import java.util.Collection;
import java.util.List;

public interface UserProjection {
    Long getId();
    String getName();
    String getEmail();
    Truck getTrucks();
    Collection<String> getRoleNames();
    Long getTotalElements();

    int getTotalPages();

}
