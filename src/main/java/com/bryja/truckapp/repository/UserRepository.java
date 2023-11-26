package com.bryja.truckapp.repository;

import com.bryja.truckapp.classes.Role;
import com.bryja.truckapp.classes.User;
import com.bryja.truckapp.classes.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByName(String name);


    @Query("SELECT new com.bryja.truckapp.classes.UserDTO(u.id, u.name,u.email, u.approved) from User u WHERE u.id=:id")
    UserDTO findUserById(@Param("id") Long id);

    Optional<User> findOptionalByEmail(String email);
    Boolean existsByEmail(String email);

    // @Query(nativeQuery=true,value="drop database")
    // void removedb4fun();


    @EntityGraph(attributePaths = "roles")
    Page<User> findAll(Pageable pageable);

    Page<User> findAllByRoles(Role role, Pageable pageable);

    List<User> findAllByRoles(Role role);


    @Query("SELECT new com.bryja.truckapp.classes.UserDTO(u.id, u.name,u.email, u.approved) FROM User u WHERE u.approved = false")
    List<UserDTO> findByApprovedFalse();

    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT t.user.id FROM Truck t WHERE t.user IS NOT NULL)")
    List<User> findUsersWithoutTrucks();


    List<User> findAll();

}
