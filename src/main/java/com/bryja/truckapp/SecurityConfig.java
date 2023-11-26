package com.bryja.truckapp;

import com.bryja.truckapp.classes.Role;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.repository.RoleRepository;
import com.bryja.truckapp.repository.UserRepository;
import com.bryja.truckapp.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customerUserDetailsService ;

    @Bean
    public PasswordEncoder passwordEncoder()
    { return new BCryptPasswordEncoder(); }
    @Value("/profile")
    private String successUrl;
    @Value("/login")
    private String failureUrl;


    @Bean
    protected SecurityFilterChain chains(HttpSecurity http) throws Exception {

        http
                //.cors()
                //.and()
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/dice/**", "/user", "/upload-avatar" ,"/settings/apply",
                                "/users/{id}", "/user/delete/**", "/bonus/delete/{id}", "/bonuses/addnew", "/bonuses/edit/{id}",
                                "/user/rawadd","/bonuses/history", "/bonuses/claim", "/alerts/removeall" ,"/roulette/play", "/roulette/history", "/coinflip/play", "/coinflip/history").hasAnyRole("MANAGER", "DRIVER", "ADMIN")
                        .requestMatchers("/admin/**", "/adminpanelusers", "/adminpanelbonuses").hasRole("ADMIN")
                        //.requestMatchers("/h2-console/**", "/h2-console/#/", "/h2-console**").hasRole("USER")

                        .requestMatchers("/index.html", "/error", "/webjars/**", "/githubprivacyerror.html","/css/**","/assets/**", "/images/**",
                                "/fonts/**", "/scripts/**", "/error", "/login", "/register", "/", "/user2", "/user/add", "/ruleta", "/coinflip",
                                "/bonuses", "/profile", "/register", "/table", "/favicon", "/sock/**", "/chathistory/**", "/usersonline", "/user/profile/{id}", "/v3/api-docs", "/api-docs", "/swagger-ui.html", "/v3/api-docs", "/swagger-ui.html", "/**").permitAll()
                        // .anyRequest().authenticated()
                        .and()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //and()
                .oauth2Login().userInfoEndpoint().oidcUserService(this.oidcUserService()).and()
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .and()
                .formLogin((form) -> form
                        .loginPage("/index")
                        .loginProcessingUrl("/index")
                        .defaultSuccessUrl("/index")
                        .permitAll()
                ).
                httpBasic().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .securityContext()
                .securityContextRepository(new HttpSessionSecurityContextRepository())
                .and();
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();


    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final RoleRepository rolerep;

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);
            User a = new User(oidcUser.getFullName(), oidcUser.getEmail(), passwordEncoder().encode("123"));
            a.setRoles(Arrays.asList(rolerep.findByName("ROLE_MANAGER")));
            if (!emailExists(a.getEmail())) {
                userRepository.save(a);
            }
            // System.out.println(a.getRoles());
            User user = userRepository.findOptionalByEmail(oidcUser.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found !"));
            if(!user.approved){
                return null;
            }
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            Collection<Role> rolesCollection = user.getRoles();
            for (Role element : rolesCollection) {
                mappedAuthorities.add(new SimpleGrantedAuthority(element.getName()));
            }
            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            return oidcUser;
        } ;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customerUserDetailsService);
        return authenticationManagerBuilder.build();
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler(successUrl);
    }

    @Bean
    SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler(failureUrl);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    { return authenticationConfiguration.getAuthenticationManager();}

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}

