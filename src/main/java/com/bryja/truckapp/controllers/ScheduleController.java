package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.Delivery;
import com.bryja.truckapp.classes.Schedule;
import com.bryja.truckapp.classes.UserDTO;
import com.bryja.truckapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ScheduleController {

    private final UserRepository repository;
    private final TruckRepository truckRepository;

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final DeliveryRepository deliveryRepository;

    private final ScheduleRepository scheduleRepository;

    public ScheduleController(UserRepository repository, TruckRepository truckRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, DeliveryRepository deliveryRepository, ScheduleRepository scheduleRepository) {
        this.repository = repository;
        this.truckRepository = truckRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.deliveryRepository = deliveryRepository;
        this.scheduleRepository = scheduleRepository;
    }


    @PostMapping(value ="/schedule/rawadd", consumes = {"*/*"})
    public String addNewSchedule(Authentication authentication, @RequestBody Schedule de) {
        Schedule d = new Schedule(de.breaks_every_minutes,de.break_minutes,de.worktime_minutes,de.name);
        scheduleRepository.save(d);

        return "ok";
    }

    @Transactional
    @DeleteMapping(value="/schedule/delete/{id}", consumes = {"*/*"})
    public String deleteSchedule(Authentication authentication, @PathVariable Long id) {
        Optional<Schedule> tr = scheduleRepository.findById(id);
        if (tr.isEmpty()) {

            return"nie znaleziono trucka";
        }

        scheduleRepository.delete(tr.get());

        return "success";
    }


    @GetMapping(value="/panel/schedule", consumes = {"*/*"})
    public List<ScheduleProjection> adminGetSchedules(Pageable pageable) {

        Page<Schedule> schedy = scheduleRepository.findAll(pageable);

        List<Schedule> d = schedy.stream().toList();


        List<ScheduleProjection> projections = d.stream()
                .map(schedule -> new ScheduleProjection() {

                    @Override
                    public Long getId() {
                        return schedule.getId();
                    }

                    @Override
                    public String getName() {
                        return schedule.getName();
                    }
                    @Override
                    public Long getBreakEveryMin() {
                        return schedule.getBreaks_every_minutes();
                    }

                    @Override
                    public Long getTotalBreak() {
                        return schedule.getBreak_minutes();
                    }

                    @Override
                    public Long getTotalWorktime() {
                        return schedule.getWorktime_minutes();
                    }

                    @Override
                    public Long getTotalElements() {
                        return schedy.getTotalElements();
                    }

                    @Override
                    public int getTotalPages() {
                        return schedy.getTotalPages();
                    }
                })
                .collect(Collectors.toList());


        return projections;
    }



    @Transactional
    @PostMapping(value="/schedule/{id}", consumes = {"*/*"})
    public String editSchedule(Authentication authentication, @PathVariable Long id,@RequestBody Schedule delivery) {
        Optional<Schedule> schedule = scheduleRepository.findById(id);
        if (schedule.isEmpty()) {
            return"nie znaleziono schedule";
        }

        Schedule fg = schedule.get();
        fg.setName(delivery.name);
        fg.setBreak_minutes(delivery.break_minutes);
        fg.setBreaks_every_minutes(delivery.breaks_every_minutes);
        fg.setWorktime_minutes(delivery.worktime_minutes);

        scheduleRepository.save(fg);

        return "success";
    }

}
