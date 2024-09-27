package com.cat.EmployeeManagement.repository;

import com.cat.EmployeeManagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByFullNameContainingOrEmailContaining(String fullName, String email, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE " +
            "(:name IS NULL OR e.fullName LIKE %:name%) AND " +
            "(:dateOfBirth IS NULL OR e.dateOfBirth = :dateOfBirth) AND " +
            "(:mobile IS NULL OR e.mobile LIKE %:mobile%) AND " +
            "(:email IS NULL OR e.email LIKE %:email%)")
    List<Employee> findEmployeesByCriteria(@Param("name") String name,
                                           @Param("dateOfBirth") LocalDate dateOfBirth,
                                           @Param("email") String email,
                                           @Param("mobile") String mobile);


}