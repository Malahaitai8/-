package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Administrator;
import com.example.springboot.entity.Employee;
import com.example.springboot.entity.Organization;
import com.example.springboot.entity.Volunteer;
import com.example.springboot.exception.CustomException;
import com.example.springboot.service.AdministratorService;
import com.example.springboot.service.EmployeeService;
import com.example.springboot.service.OrganizationService;
import com.example.springboot.service.VolunteerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WebController {
    @Resource
    private EmployeeService employeeService;
    @Resource
    private VolunteerService volunteerService;
    @Resource
    private AdministratorService administratorService;
    @Resource
    private OrganizationService organizationService;

    @GetMapping("/hello")
    public Result hello() {
        return Result.success("Hello~");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Employee employee) throws CustomException {
        Employee dbEmployee = employeeService.login(employee);
        return Result.success(dbEmployee);
    }





    @PostMapping("/register")
    public Result register(@RequestBody Employee employee) throws CustomException {
        employeeService.register(employee);
        return Result.success();
    }

//    @PostMapping("/administratorlogin")
//    public Result adminLogin(@RequestBody Administrator administrator) {
//        try {
//            Administrator dbAdmin = administratorService.adminLogin(administrator);
//            return Result.success(dbAdmin);
//        } catch (CustomException e) {
//            return Result.success();
//        }
//    }


    @PostMapping("/organizationregister")
    public Result organizationRegister(@RequestBody Organization organization) {
        try {
            organizationService.register(organization);
            return Result.success("注册成功");
        } catch (CustomException e) {
            return Result.success();
        }
    }

    @GetMapping("/count")
    public Result count() throws CustomException {
        throw new CustomException("500", "请求失败");
    }

    @GetMapping("/map")
    public Result map() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Doyeon");
        map.put("age", 32);
        return Result.success(map);
    }
}