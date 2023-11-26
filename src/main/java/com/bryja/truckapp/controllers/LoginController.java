package com.bryja.truckapp.controllers;

import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.repository.RoleRepository;
import com.bryja.truckapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;
    private final UserRepository iUserRepository;
    private final RoleRepository iRoleRepository;
    private final RoleRepository rolerep;

    public PasswordEncoder passwordEncoder()
    { return new BCryptPasswordEncoder(); }

    private SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @PostMapping("/login")
    //public ModelAndView login(@RequestParam String email, @RequestParam String password , HttpServletRequest request, HttpServletResponse response) {
    public String login(@RequestParam String email, @RequestParam String password , HttpServletRequest request, HttpServletResponse response) {
        if(!iUserRepository.findByEmail(email).approved){
            return "User has not been approved yet.";
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(token);
       // if(!token.isAuthenticated()){
       //     return "Wrong inputs or account does not exists.";
       // }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        return "Success! Logged in";
       // return new ModelAndView("index");
    }


    @PostMapping("/register")
    //public ModelAndView register(@RequestParam String firstname,@RequestParam String lastname,@RequestParam String email, @RequestParam String password) {
    public String register(@RequestParam String firstname,@RequestParam String lastname,@RequestParam String email, @RequestParam String password) {
        User a = new User(firstname+" "+lastname, email, passwordEncoder().encode(password));
        a.setRoles(Arrays.asList(rolerep.findByName("ROLE_DRIVER")));
        if (!emailExists(a.getEmail())) {
            iUserRepository.save(a);
            return "Account has been created. Wait for the approval or login.";
        }
        else{
            return "Inputs invalid or mail already exists.";
        }
       // return new ModelAndView("login");
    }

    private boolean emailExists(String email) {
        return iUserRepository.findByEmail(email) != null;
    }

}
