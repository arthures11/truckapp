package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Privilege;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.classes.Workday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkdayRepository extends JpaRepository<Workday, Long> {
    Page<Workday> findAllByUser(Pageable pageable, User user);
}
