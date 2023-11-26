package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    public void deleteAllByUserId(Long userId);
}