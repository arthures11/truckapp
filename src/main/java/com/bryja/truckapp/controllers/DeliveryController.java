package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.Delivery;
import com.bryja.truckapp.classes.Truck;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.classes.UserDTO;
import com.bryja.truckapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
public class DeliveryController {
    private final UserRepository repository;
    private final TruckRepository truckRepository;

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final DeliveryRepository deliveryRepository;

    public DeliveryController(UserRepository repository, TruckRepository truckRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, DeliveryRepository deliveryRepository) {
        this.repository = repository;
        this.truckRepository = truckRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.deliveryRepository = deliveryRepository;
    }




    @PostMapping(value ="/delivery/rawadd", consumes = {"*/*"})
    public String addNewDelivery(Authentication authentication, @RequestBody Delivery de) {

        String code = generateRandomString(10);

        while(codeExists(code)){
            code = generateRandomString(10);
        }


        Delivery d = new Delivery(code,de.deliverycompany,de.description);
        deliveryRepository.save(d);
        return "ok";
    }

    @GetMapping(value="/drivers-assign-delivery", consumes = {"*/*"})
    public List<User> driverToAssignDelivery(Authentication authentication) {
        return repository.findAll();
    }





    @GetMapping(value="/panel/attachable-delivery", consumes = {"*/*"})
    public List<Delivery> getAttachableDeliveries() {

        return deliveryRepository.findAllByUserNull();

    }


    @GetMapping(value="/panel/delivery", consumes = {"*/*"})
    public List<DeliveryProjection> adminGetDeliveries(Pageable pageable) {

        Page<Delivery> deliveries = deliveryRepository.findAll(pageable);

        List<Delivery> d = deliveries.stream().toList();


        List<DeliveryProjection> projections = d.stream()
                .map(delivery -> new DeliveryProjection() {

                    @Override
                    public Long getId() {
                        return delivery.getId();
                    }

                    @Override
                    public String getCode() {
                        return delivery.getDeliveryhash();
                    }

                    @Override
                    public String getDescription() {
                        return delivery.getDescription();
                    }

                    @Override
                    public String getDeliveryCompany() {
                        return delivery.getDeliverycompany();
                    }

                    @Override
                    public String getStatus() {
                        if(delivery.status==0){
                            return "created";
                        }
                        else if(delivery.status==1){
                            return "in progress";
                        }
                        else if(delivery.status==2){
                            return "delivered";
                        }
                        else {
                            return "undefined";
                        }
                    }


                    @Override
                    public UserDTO getUser() {
                        if(delivery.getUser()!=null) {
                            UserDTO us = repository.findUserById(delivery.getUser().getId());
                            if (us!=null) {
                                return us;
                            }
                            else return null;
                        }
                        else return null;
                    }

                    @Override
                    public Long getTotalElements() {
                        return deliveries.getTotalElements();
                    }

                    @Override
                    public int getTotalPages() {
                        return deliveries.getTotalPages();
                    }
                })
                .collect(Collectors.toList());


        return projections;
    }



    @Transactional
    @DeleteMapping(value="/delivery/delete/{id}", consumes = {"*/*"})
    public String deleteDelivery(Authentication authentication, @PathVariable Long id) {
        Optional<Delivery> tr = deliveryRepository.findById(id);
        if (tr.isEmpty()) {

            return"nie znaleziono trucka";
        }

        deliveryRepository.delete(tr.get());

        return "success";
    }


    @Transactional
    @DeleteMapping(value="/user/design-from-delivery/{code}", consumes = {"*/*"})
    public String deleteUserTruck(Authentication authentication, @PathVariable String code) {


        System.out.println(code);
        Delivery tr = deliveryRepository.findByDeliveryhash(code);
        if(tr!=null){
            tr.setUser(null);
            deliveryRepository.save(tr);
        }

        return "success";
    }

    @Transactional
    @PostMapping(value="/delivery/{id}", consumes = {"*/*"})
    public String editDelivery(Authentication authentication, @PathVariable Long id,@RequestBody Delivery delivery) {
        Optional<Delivery> delivery1 = deliveryRepository.findById(id);
        if (delivery1.isEmpty()) {
            return"nie znaleziono trucka";
        }

        Delivery fg = delivery1.get();
        fg.setUser(repository.findByName(delivery.getUsername()));
        fg.setDeliverycompany(delivery.getDeliverycompany());
        fg.setDescription(delivery.getDescription());
        fg.setStatus(delivery.status);

        deliveryRepository.save(fg);

        return "success";
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomValue = random.nextInt(36);

            char randomChar;
            if (randomValue < 10) {
                randomChar = (char) ('0' + randomValue);
            } else {
                randomChar = (char) ('a' + (randomValue - 10));
            }

            sb.append(randomChar);
        }

        return sb.toString();
    }


    private boolean codeExists(String code) {
        return deliveryRepository.findByDeliveryhash(code) != null;
    }
}
