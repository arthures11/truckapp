package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Delivery;
import com.bryja.truckapp.classes.Privilege;
import com.bryja.truckapp.classes.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByDeliveryhash(String deliveryhash);


    List<Delivery> findAllByUserNull();


    Page<Delivery> findAll(Pageable pageable);
}
