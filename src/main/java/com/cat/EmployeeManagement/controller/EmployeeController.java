package com.cat.EmployeeManagement.controller;

import com.cat.EmployeeManagement.model.Employee;
import com.cat.EmployeeManagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployee(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile) {
        List<Employee> employees = employeeService.searchEmployees(name, dateOfBirth, email, mobile);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @Value("${file.storage.path}")
    private String storagePath;

    @PostMapping
    public ResponseEntity<String> addEmployee(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("mobile") String mobile,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("photo") MultipartFile photo) {
        employeeService.saveEmployee(new Employee(), firstName, lastName, email, mobile, dateOfBirth, photo);
        return new ResponseEntity<>("Employee added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
                                                   @RequestParam("fullName") String fullName,
                                                   @RequestParam("email") String email,
                                                   @RequestParam("mobile") String mobile,
                                                   @RequestParam("dateOfBirth") String dateOfBirth,
                                                   @RequestParam("photo") MultipartFile photo) {
        Employee updatedEmployee = employeeService.updateEmployee(id, fullName,
                email, mobile, dateOfBirth, photo);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/sort/{field}")
    public List<Employee> getEmployeeSortBy(@PathVariable String field) {
        return employeeService.getAllEmployeesSortedBy(field);
    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    public Page<Employee> getEmployeeWithPagination(@PathVariable int offset,
                                                    @PathVariable int pageSize) {
        return employeeService.getEmployeesWithPagination(offset, pageSize);
    }

    @GetMapping("/pagination-and-sort/{offset}/{pageSize}/{field}/{direction}")
    public Page<Employee> getEmployeeWithPaginationAndSort(@PathVariable int offset,
                                                           @PathVariable int pageSize,
                                                           @PathVariable String field,
                                                           @PathVariable String direction) {
        return employeeService.getEmployeesWithPaginationAndSorting(offset, pageSize, field, direction);
    }
}
