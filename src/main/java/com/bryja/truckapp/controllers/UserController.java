package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.*;
import com.bryja.truckapp.repository.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TruckRepository truckRepository;

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final CoordRepository coordRepository;

    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;

    public UserController(UserRepository repository, TruckRepository truckRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, CoordRepository coordRepository, MessageRepository messageRepository, NotificationRepository notificationRepository) {
        this.repository = repository;
        this.truckRepository = truckRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.coordRepository = coordRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
    }


    @PostMapping(value ="/user/rawadd", consumes = {"*/*"})
    public String addNewUser(Authentication authentication, @RequestParam(value = "passwordrepeat") String rep, @RequestBody User usr) {

        if(!rep.equals(usr.password)){
            return "hasla nie zgadzaja sie";
        }

        User a = new User(usr.getName(),usr.getEmail(),passwordEncoder.encode(rep));
        a.setRoles(Arrays.asList(roleRepository.findByName("ROLE_DRIVER")));
        if (emailExists(a.getEmail())) {
            return "email juz istnieje";
        }
        else{
            repository.save(a);
        }
        return "ok";
    }


    @PostMapping(value ="/truck/rawadd", consumes = {"*/*"})
    public String addnewTruck(Authentication authentication, @RequestBody Truck tr) {


        Truck t = new Truck(tr.getName(),tr.getDistance_traveled(), tr.getCompany());
        truckRepository.save(t);
        return "ok";
    }

    @GetMapping("/asd")
    public String login() {

        return "Hello";
    }

    @GetMapping(value="/user", consumes = {"*/*"})
    public User user(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));

        return usr;
    }

    @GetMapping(value="/trucks-assign", consumes = {"*/*"})
    public List<Truck> truckToAssign(Authentication authentication) {
        return truckRepository.findAllByUserIsNull();
    }


    @GetMapping(value="/drivers-assign", consumes = {"*/*"})
    public List<User> driverToAssign(Authentication authentication) {
        return repository.findUsersWithoutTrucks();
    }

    @GetMapping(value="/to-approve", consumes = {"*/*"})
    public ResponseEntity<?> users_to_approve(Authentication authentication) {

        List<UserDTO> unapprovedUsers = repository.findByApprovedFalse();

        if (unapprovedUsers.isEmpty()) {
            return ResponseEntity.ok(unapprovedUsers);
        }
        return ResponseEntity.ok(unapprovedUsers);

    }

    @GetMapping(value="/role", consumes = {"*/*"})
    public String role (Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));

        if(authentication.getAuthorities().toString().contains("MANAGER")){
            return "Manager";
        }
        else if(authentication.getAuthorities().toString().contains("ADMIN")){
            return "Admin";
        }
        else if(authentication.getAuthorities().toString().contains("DRIVER")){
            return "Driver";
        }
        return "error";
    }

    @Transactional
    @DeleteMapping(value="/user/delete/{id}", consumes = {"*/*"})
    public String deleteUser(Authentication authentication, @PathVariable Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {

            return"nie znaleziono usera";
        }

        Truck tr = truckRepository.findTruckByUser(user.get());
        tr.setUser(null);
        truckRepository.save(tr);
        User usr = user.get();
        repository.delete(usr);

        return "success";
    }


    @Transactional
    @DeleteMapping(value="/truck/delete/{id}", consumes = {"*/*"})
    public String deleteTruck(Authentication authentication, @PathVariable Long id) {
        Optional<Truck> tr = truckRepository.findById(id);
        if (tr.isEmpty()) {

            return"nie znaleziono trucka";
        }

        truckRepository.delete(tr.get());

        return "success";
    }

    @Transactional
    @DeleteMapping(value="/user/design/{id}", consumes = {"*/*"})
    public String deleteUserTruck(Authentication authentication, @PathVariable Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {

            return"nie znaleziono usera";
        }

        Truck tr = truckRepository.findTruckByUser(user.get());
        if(tr!=null){
            tr.setUser(null);
            truckRepository.save(tr);
        }

        return "success";
    }


    @Transactional
    @PostMapping(value="/users/{id}", consumes = {"*/*"})
    public String editUser(Authentication authentication, @PathVariable Long id,@RequestBody User usera) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {

            return"nie znaleziono usera";
        }
        Privilege readPrivilege
                = findPrivilege("READ_PRIVILEGE");
        Role rk = roleRepository.findByName(usera.chip);
        if(rk==null){
            return "nie znaleziono roli";
        }
        rk.setPrivileges(Arrays.asList(readPrivilege));
        List<Role> roles = new ArrayList<>();
        roles.add(rk);
        User userka = user.get();
        userka.setName(usera.getName());
        userka.setEmail(usera.getEmail());
        Truck tru = truckRepository.findTruckByUser(user.get());
        if(tru!=null){
            tru.setUser(null);
            truckRepository.save(tru);
        }
        Optional<Truck> truck = truckRepository.findById(Long.valueOf(usera.truck));
        truck.get().setUser(userka);
        userka.setRoles(roles);
        //existingUser.setRoles(updatedUser.getRoles());

        repository.save(userka);
        truckRepository.save(truck.get());
        //return ResponseEntity.ok(savedUser);
        return "success";
    }


    @Transactional
    @PostMapping(value="/trucks/{id}", consumes = {"*/*"})
    public String editTruck(Authentication authentication, @PathVariable Long id,@RequestBody Truck trucka) {
        Optional<Truck> truck = truckRepository.findById(id);
        if (truck.isEmpty()) {
            return"nie znaleziono trucka";
        }

        Truck fg = truck.get();
        fg.setUser(repository.findByName(trucka.getUsername()));
        fg.setCompany(trucka.getCompany());
        fg.setName(trucka.getName());
        fg.setDistance_traveled(trucka.distance_traveled);

        truckRepository.save(fg);

        return "success";
    }



    Privilege findPrivilege(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            return null;
        }
        return privilege;
    }

    @PostMapping("/upload-avatar")
    @ResponseBody
    public String uploadAvatar(@RequestParam("avatar") String file, Authentication authentication) throws IOException {
        String[] parts = file.split(",");
        String imageString = parts[1]; // extract the base64 string from the parts
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        usr.setAvatar(imageBytes);
        repository.save(usr);
        return "success";
    }

    @PostMapping(value="/settings/apply", consumes = {"*/*"})
    public String changeProfile(Authentication authentication,
                                @RequestParam("username") String new_name,
                                @RequestParam("pass") String new_pass,
                                @RequestParam("repeat") String new_pass_repeat) {

        if(!new_pass.equals(new_pass_repeat)){
            return "hasla sie nie zgadzaja";
        }
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        if(!new_pass.isEmpty()){
            usr.setPassword(passwordEncoder.encode(new_pass));
        }
        if(!new_name.isEmpty()){
            usr.setName(new_name);
        }
        repository.save(usr);
        return "zapisano pomyslnie";
    }


    @PostMapping(value="/user/approve/{id}", consumes = {"*/*"})
    public ResponseEntity<?> approveUser(Authentication authentication, @PathVariable Long id) {

        Optional<User> usr = repository.findById(id);
        usr.get().approved = true;;

        repository.save(usr.get());

        return ResponseEntity.ok("Approved.");
    }

    @PostMapping(value="/user/cancel/{id}", consumes = {"*/*"})
    public ResponseEntity<?> cancelUser(Authentication authentication, @PathVariable Long id) {

        Optional<User> usr = repository.findById(id);
        repository.delete(usr.get());

        return ResponseEntity.ok("Canceled the user successfully.");
    }


    @GetMapping(value="/chathistory", consumes = {"*/*"})
    public List<Message> getChatMessages(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Message> msgs = messageRepository.findFirst20ByChatroomOrderByIdDesc("general");

        for(int i=0;i<msgs.size();i++){
            Optional<User> g = repository.findById(msgs.get(i).getUser().getId());
            msgs.get(i).setAvatar(g.get().getAvatar());
            msgs.get(i).setAuthor_name(g.get().getName());
            msgs.get(i).setUserid(g.get().getId());
        }
        return msgs;
    }
    @Transactional
    @DeleteMapping(value="/alerts/removeall", consumes = {"*/*"})
    public String removeAlerts(Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        usr.getNotifications().clear();
        notificationRepository.deleteAllByUserId(usr.getId());
        repository.save(usr);

        return "success";
    }
    @PostMapping(value="/track/{id}", consumes = {"*/*"})
    public Coord track(@PathVariable Long id) {


        Optional<Truck> t = truckRepository.findById(id);

        if(t.isEmpty()){
            return null;
        }

        double randomDouble1 = getRandomDouble(50.68079714532164, 51);
        double randomDouble2 = getRandomDouble(16.962890625, 17.5);

        Truck tr = t.get();

        Coord coord = new Coord(new Date(),tr,randomDouble1, randomDouble2);


        coordRepository.save(coord);

        return coord;
    }

    public static double getRandomDouble(double min, double max) {
        Random random = new Random();
        return min + (max - min) * random.nextDouble();
    }

    @GetMapping(value="/panel/drivers", consumes = {"*/*"})
    public List<UserProjection>  adminGetUsers(Pageable pageable) {

        Role role = roleRepository.findByName("ROLE_DRIVER");
        Page<User> users = repository.findAllByRoles(role, pageable);

        List<User> d = users.stream().toList();


        List<UserProjection> projections = d.stream()
                .map(user -> new UserProjection() {

                    @Override
                    public Long getId(){
                        return user.getId();
                    }
                    @Override
                    public String getName() {
                        return user.getName();
                    }

                    @Override
                    public String getEmail() {
                        return user.getEmail();
                    }

                    @Override
                    public Truck getTrucks() {
                        return truckRepository.findTruckByUser(user);
                    }

                    @Override
                    public List<String> getRoleNames() {
                        return user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
                    }
                    @Override
                    public Long getTotalElements(){
                        return users.getTotalElements();
                    }
                    @Override
                    public int getTotalPages(){
                        return users.getTotalPages();
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

    @GetMapping(value="/panel/trucks", consumes = {"*/*"})
    public List<TruckProjection>  adminGetTrucks(Pageable pageable) {

        Page<Truck> trucks = truckRepository.findAll(pageable);

        List<Truck> d = trucks.stream().toList();


        List<TruckProjection> projections = d.stream()
                .map(truck -> new TruckProjection() {

                    @Override
                    public Long getId() {
                        return truck.getId();
                    }

                    @Override
                    public String getCompany() {
                        return truck.getCompany();
                    }

                    @Override
                    public Double getDistanceTraveled() {
                        return truck.getDistance_traveled();
                    }

                    @Override
                    public String getName() {
                        return truck.getName();
                    }

                    @Override
                    public UserDTO getUser() {
                        if(truck.getUser()!=null) {
                            UserDTO us = repository.findUserById(truck.getUser().getId());
                            if (us!=null) {
                                return us;
                            }
                            else return null;
                        }
                        else return null;
                    }

                    @Override
                    public Long getTotalElements() {
                        return trucks.getTotalElements();
                    }

                    @Override
                    public int getTotalPages() {
                        return trucks.getTotalPages();
                    }
                })
                .collect(Collectors.toList());


        return projections;
    }


    @PostMapping("/generate")
    public ResponseEntity<Resource> gen(@RequestBody RaportRequestBody a) throws ParseException {

        Long userId = a.getUserId();
        String date = a.getDate();

        Optional<User> user =  repository.findById(userId);
        if(user.isEmpty()){
            return null;
        }
        User user1 = user.get();
        Long hours = Long.valueOf(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
            Date inputDate = dateFormat.parse(date);

            Date currentDate = new Date();


            List<Workday> workdays = new ArrayList<>();
        for (Workday w:user1.getWorkdays()) {
            if (isSameMonthAndYear(currentDate, inputDate)) {
                workdays.add(w);
                if(w.getSchedule()!=null){
                    hours += (w.getSchedule().getWorktime_minutes() / 60);
                }
            } else {
                continue;
            }
        }

        System.out.println(hours);





        byte[] reportData = generateReport(user1, date, workdays, hours);

        ByteArrayResource resource = new ByteArrayResource(reportData);

        // Return the ResponseEntity with appropriate headers
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+user1.getName()+"-"+date+".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportData.length)
                .body(resource);
    }

    private static boolean isSameMonthAndYear(Date date1, Date date2) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy");
        return monthYearFormat.format(date1).equals(monthYearFormat.format(date2));
    }
    public byte[] generateReport(User user, String date, List<Workday> workdays, Long hour) {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet
        Sheet sheet = workbook.createSheet(user.getName()+" raport");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("month:");
        headerRow.createCell(1).setCellValue("name:");
        headerRow.createCell(2).setCellValue("date:");
        headerRow.createCell(3).setCellValue("hours:");
        headerRow.createCell(4).setCellValue("TOTAL HOURS:");

        // Create a data row
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(date);
        dataRow.createCell(1).setCellValue(user.getName());
        dataRow.createCell(4).setCellValue(hour);
        List<Row> rows = new ArrayList<>();
        rows.add(dataRow);
        for(int i=0;i<workdays.size();i++){
            rows.add(sheet.createRow(i+2));
        }


        for(int i=0;i<workdays.size();i++){
            rows.get(i).createCell(2).setCellValue(workdays.get(i).date.toString());
            if(workdays.get(i).getSchedule()!=null){
                rows.get(i).createCell(3).setCellValue((Long)(workdays.get(i).getSchedule().worktime_minutes/60));
            }
            else {
                rows.get(i).createCell(3).setCellValue(0);

            }
        }



        // Auto-size the columns
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the workbook to a ByteArrayOutputStream
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0]; // Return an empty byte array in case of an error
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


    private boolean emailExists(String email) {
        return repository.findByEmail(email) != null;
    }
}
