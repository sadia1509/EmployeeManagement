package com.cat.EmployeeManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class PageController {
    @GetMapping("/home")
    public String home() {
        return "page/home";
    }

    @GetMapping("/employee")
    public String employee() {
        return "page/employee";
    }

    @GetMapping("/add-employee")
    public String addEmployee() {
        return "page/addEmployee";
    }
}
