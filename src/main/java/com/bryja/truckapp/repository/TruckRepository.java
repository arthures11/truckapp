package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Privilege;
import com.bryja.truckapp.classes.Truck;
import com.bryja.truckapp.classes.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    Truck findTruckByUser(User user);
    List<Truck> findAllByUserIsNull();

    Page<Truck> findAll(Pageable pageable);

}
