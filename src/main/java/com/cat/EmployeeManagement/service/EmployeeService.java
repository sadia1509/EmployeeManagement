package com.cat.EmployeeManagement.service;

import com.cat.EmployeeManagement.constant.EmployeeConstant;
import com.cat.EmployeeManagement.model.Employee;
import com.cat.EmployeeManagement.repository.EmployeeRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Cloudinary cloudinary;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow();
    }

    public Employee saveEmployee(Employee employee, String firstName, String lastName, String email, String mobile, String dateOfBirth, MultipartFile photo) {
        employee.setFullName(firstName + " " + lastName);
        employee.setEmail(email);
        employee.setMobile(mobile);
        employee.setDateOfBirth(LocalDate.parse(dateOfBirth));
        savePhoto(photo, employee);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        // Delete the profile picture from Cloudinary if it exists
        deletePhoto(id);
        employeeRepository.deleteById(id);
    }


    public Page<Employee> searchEmployees(String searchTerm, Pageable pageable) {
        return employeeRepository.findByFullNameContainingOrEmailContaining(searchTerm, searchTerm, pageable);
    }

    public Employee updateEmployee(Long id, String fullName, String email, String mobile, String dateOfBirth, MultipartFile photo) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setFullName(fullName);
        employee.setEmail(email);
        employee.setMobile(mobile);
        employee.setDateOfBirth(LocalDate.parse(dateOfBirth));
        savePhoto(photo, employee);
        if (photo != null && !photo.isEmpty()) deletePhoto(id);
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployeesSortedBy(String field) {
        return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, field));
    }

    public Page<Employee> getEmployeesWithPagination(int offset, int pageSize) {
        return employeeRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Page<Employee> getEmployeesWithPaginationAndSorting(int offset, int pageSize, String field, String direction) {
        Sort.Direction selectedDirection = Sort.Direction.ASC;
        if (direction.equalsIgnoreCase(EmployeeConstant.DESC))
            selectedDirection = Sort.Direction.DESC;
        return employeeRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(selectedDirection, field)));
    }

    public List<Employee> searchEmployees(String name, String dateOfBirth, String email, String mobile) {
        // Handle empty strings
        LocalDate dateOfBirthParsed = null;
        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            dateOfBirthParsed = LocalDate.parse(dateOfBirth);
        }
        return employeeRepository.findEmployeesByCriteria(name, dateOfBirthParsed, email, mobile);
    }

    public Map uploadFile(MultipartFile photo) throws IOException {
        String fileName = UUID.randomUUID().toString();
        return cloudinary.uploader().upload(photo.getBytes(), ObjectUtils.asMap("public_id", fileName));
    }

    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private void deletePhoto(long id) {
        String profilePictureUrl = employeeRepository.findById(id).get().getPhoto();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            String publicId = profilePictureUrl.split("/")[7].split("\\.")[0];
            try {
                deleteFile(publicId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void savePhoto(MultipartFile photo, Employee employee) {
        if (photo != null && !photo.isEmpty()) {
            // Upload the profile picture to Cloudinary
            try {
                System.out.println(photo.getSize() + " " + photo.getBytes());
                Map uploadResult = uploadFile(photo);
                String profilePictureUrl = (String) uploadResult.get("url");
                employee.setPhoto(profilePictureUrl);
                System.out.println(employee.getPhoto());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}