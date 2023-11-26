package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.*;
import com.bryja.truckapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class WorkdayController {

    private final UserRepository repository;
    private final TruckRepository truckRepository;

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final WorkdayRepository workdayRepository;
    private final DeliveryRepository deliveryRepository;
    private final ScheduleRepository scheduleRepository;

    public WorkdayController(UserRepository repository, TruckRepository truckRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, WorkdayRepository workdayRepository, DeliveryRepository deliveryRepository, ScheduleRepository scheduleRepository) {
        this.repository = repository;
        this.truckRepository = truckRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.workdayRepository = workdayRepository;
        this.deliveryRepository = deliveryRepository;
        this.scheduleRepository = scheduleRepository;
    }


    @Transactional
    @PostMapping(value ="/addnewplan/{id}", consumes = {"*/*"})
    public String addNewPlan(Authentication authentication, @RequestBody NewPlanRequestBody requestBody, @PathVariable Long id) {

        Optional<User> usr = repository.findById(id);
        if(!usr.isEmpty()){
            User user = usr.get();

            for (int i=0;i<requestBody.getIdArray().size();i++){
                Optional<Delivery> f = deliveryRepository.findById(Long.valueOf(requestBody.getIdArray().get(i)));
                if(!f.isEmpty()){
                    f.get().setUser(user);
                    f.get().setStatus(1);
                }
                else{
                    return "error";
                }
            }

            LocalDate start = requestBody.getStartDate();
            LocalDate end = requestBody.getEndDate();
            while (!start.isAfter(end)) {

                user.getWorkdays().add(new Workday(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()),user));

               // System.out.println(start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                start = start.plusDays(1);
            }

            repository.save(user);
        }
        else{
            return "error";
        }

        return "ok";
    }


    @GetMapping(value="/panel/driver-workdays/{id}", consumes = {"*/*"})
    public List<WorkdayProjection> adminGetUsersWorkdays(Pageable pageable, @PathVariable Long id) {
        Optional<User> user = repository.findById(id);
        if(user.isEmpty()){
            return null;
        }

        Page<Workday> workdays = workdayRepository.findAllByUser(pageable, user.get());

        List<Workday> w = workdays.stream().toList();


        List<WorkdayProjection> projections = w.stream()
                .map(wrd -> new WorkdayProjection() {

                    @Override
                    public Long getId(){
                        return wrd.getId();
                    }
                    @Override
                    public String getDate() {
                        return wrd.getDate().toString();
                    }

                    @Override
                    public String getDescription() {
                        return wrd.getDescription();
                    }

                    @Override
                    public String getUsername() {
                        return wrd.getUser().getName();
                    }

                    @Override
                    public String getSchedule() {

                                if(wrd.getSchedule()!=null){
                                    return wrd.getSchedule().getName();
                                }
                                else return null;

                    }

                    @Override
                    public List<Image> getImage() {
                        return wrd.getImages();
                    }
                    @Override
                    public Long getTotalElements(){
                        return workdays.getTotalElements();
                    }
                    @Override
                    public int getTotalPages(){
                        return workdays.getTotalPages();
                    }
                })
                .collect(Collectors.toList());

//        for(int i=0;i<projections.size();i++){
//            if(!projections.get(i).getRoleNames().contains("ROLE_DRIVER")){
//                projections.remove(i);
//            }
//        }

        return projections;
    }

    @Transactional
    @DeleteMapping(value="/workday/delete/{id}", consumes = {"*/*"})
    public String deleteWorkday(Authentication authentication, @PathVariable Long id) {
        Optional<Workday> tr = workdayRepository.findById(id);
        if (tr.isEmpty()) {

            return"nie znaleziono trucka";
        }

        workdayRepository.delete(tr.get());

        return "success";
    }



    @Transactional
    @PostMapping(value="/workday/{id}", consumes = {"*/*"})
    public String editWorkday(Authentication authentication, @PathVariable Long id,@RequestBody WorkdayEditRequestBody workday) {
        Optional<Workday> workday1 = workdayRepository.findById(id);
        if (workday1.isEmpty()) {
            return"nie znaleziono schedule";
        }

        Workday fg = workday1.get();

        Date d = Date.from(workday.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        fg.setDate(d);
        fg.setDescription(workday.getDescription());

        Schedule s = scheduleRepository.findByName(workday.getSchedule());

        if(s==null){
            return "error";
        }

        fg.setSchedule(s);

        workdayRepository.save(fg);

        return "success";
    }


    @Transactional
    @GetMapping(value="/workday/confirmdata", consumes = {"*/*"})
    public ConfirmWorkdayDTO confirmWorkday(Authentication authentication) {
        ConfirmWorkdayDTO f = new ConfirmWorkdayDTO();
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));


        Date now = new Date();

        List<Schedule> ses = new ArrayList<>();

        ses = scheduleRepository.findAll();

        for (Workday workday : usr.getWorkdays()) {
            if(isSameDay(workday.date,now)){

                f.setWorkday(workday);
                if(!usr.getInProgressDeliveries().isEmpty()){
                    f.setDeliveries(usr.getInProgressDeliveries());
                }
                f.setSchedules(ses);
                f.setDate(workday.getDate().toString());

                return f;

            }
        }
        return null;


    }




    @Transactional
    @GetMapping(value="/workday/calendar", consumes = {"*/*"})
    public CalendarDTO calendarData(Authentication authentication) {
        CalendarDTO f = new CalendarDTO();
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));


        Date now = new Date();

        YearMonth yearMonth = YearMonth.now();
        int daysInMonth = yearMonth.lengthOfMonth();

        List<Workday> ws = new ArrayList<>();

        Date temp = new Date();
        outerloop: for(int i=1;i<=daysInMonth;i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(temp);
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date updatedDate = calendar.getTime();

            for (Workday workday : usr.getWorkdays()) {
                if(isSameDay(workday.date,updatedDate)){
                    ws.add(workday);
                    continue outerloop;
                }
            }
            ws.add(new Workday());

        }
        f.setWs(ws);
        return f;


    }

    @Transactional
    @PostMapping(value="/workday/confirm", consumes = {"*/*"})
    public String confirmDriverWorkday(@RequestBody ConfirmDayRequestBody bodydata) {

        Optional<Workday> w2 = workdayRepository.findById(bodydata.getWorkdayId());

        if(w2.isEmpty()){
            return "error";
        }
        Workday w = w2.get();

        if(!bodydata.getFile().isEmpty()){
            String file = bodydata.getFile();
            String[] parts = file.split(",");
            String imageString = parts[1];
            byte[] imageBytes = Base64.getDecoder().decode(imageString);
            Image im = new Image(imageBytes,w);
            List<Image> imgs  = w.getImages();
            imgs.add(im);
            w.setImages(imgs);
        }

        if(!bodydata.getDescription().isEmpty()){
            w.setDescription(bodydata.getDescription());
        }

        w.setSchedule(scheduleRepository.findById(bodydata.getScheduleId()).get());


        if(!bodydata.getDeliveryIds().isEmpty()) {
            for (int i = 0; i < bodydata.getDeliveryIds().size(); i++) {
                Optional<Delivery> d = deliveryRepository.findById(Long.valueOf(bodydata.getDeliveryIds().get(i)));
                d.get().status = 2;
                Date dats = new Date();
                d.get().setConfimdate(dats);
                deliveryRepository.save(d.get());
            }
        }

        Role role = roleRepository.findByName("ROLE_MANAGER");
        List<User> users = repository.findAllByRoles(role);

        for(int i=0;i<users.size();i++){
            users.get(i).getNotifications().add(new Notification("Driver "+w.getUser().getName()+" has confirmed the workday",new Date(),users.get(i)));
        }

        repository.saveAll(users);

        return "success";


    }

    public String checkmail(Object authentication){
        if (authentication instanceof DefaultOidcUser) {       //klasa która powstaje przy social loginie
            DefaultOidcUser oauth2User = (DefaultOidcUser) authentication;
            return oauth2User.getAttribute("email");
        } else if (authentication instanceof UserDetails) {    //zwykla klasa posiadająca dane z bazy
            UserDetails userDetails = (UserDetails) authentication;
            return userDetails.getUsername();
        }
        else if (authentication instanceof OAuth2AuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            return email;
        }
        else if (authentication instanceof UsernamePasswordAuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            UsernamePasswordAuthenticationToken oauthToken = (UsernamePasswordAuthenticationToken) authentication;
            String email = oauthToken.getName();
            return email;
        }
        else {
            return "notfound";
        }
    }


    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }
}
