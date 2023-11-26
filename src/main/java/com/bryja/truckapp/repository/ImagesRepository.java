package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagesRepository extends JpaRepository<Image, Long> {
}
