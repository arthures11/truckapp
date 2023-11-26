package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Delivery;
import com.bryja.truckapp.classes.Privilege;
import com.bryja.truckapp.classes.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findAll(Pageable pageable);

    Schedule findByName(String name);

}
