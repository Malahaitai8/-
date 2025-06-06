package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Employee;
import com.example.springboot.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 添加
     * */
    @PostMapping("/insert")
    public Result insert(@RequestBody Employee employee){
        employeeService.insert(employee);
        return Result.success();
    }

    /**
     * 更新语句
     * */
    @PutMapping("/updateById")
    public Result updateById(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return Result.success();
    }

    /**
     * 删除语句
     * */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        employeeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     * */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        employeeService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     * */
    @GetMapping("/selectAll")
    public Result selectAll(Employee employee){
        List<Employee> list = employeeService.selectAll(employee);
        return Result.success(list);
    }
    /**
     * 查询单个
     * */
    @GetMapping("/selectByID/{id}")
    public Result selectByID(@PathVariable Integer id){
        Employee employee = employeeService.selectByID(id);
        return Result.success(employee);
    }
    /**
     * 查询单个
     * */
    @GetMapping("/selectOne")
    public Result selectOne(@RequestParam Integer id){
        Employee employee = employeeService.selectByID(id);
        return Result.success(employee);
    }
    /**
     * 分页查询
     * pageNum:当前页面
     * pageSize:每页的个数
     * */
    @GetMapping("/selectPage")
    public Result selectPage(Employee employee,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Employee> pageInfo = employeeService.selectPage(employee,pageNum,pageSize);
        return Result.success(pageInfo);
    }
}
