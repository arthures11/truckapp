package com.bryja.truckapp.repository;


import com.bryja.truckapp.classes.Truck;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.classes.UserDTO;

import java.util.Collection;

public interface DeliveryProjection {
    Long getId();
    String getCode();
    String getDescription();
    String getDeliveryCompany();

    String getStatus();
    UserDTO getUser();

    Long getTotalElements();
    int getTotalPages();

}