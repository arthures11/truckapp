package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Coord;
import com.bryja.truckapp.classes.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordRepository extends JpaRepository<Coord, Long> {
}
