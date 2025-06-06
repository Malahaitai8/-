package com.example.springboot.service;

import com.example.springboot.entity.Employee;
import com.example.springboot.exception.CustomException;
import com.example.springboot.mapper.EmployeeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Resource
    private EmployeeMapper employeeMapper;

    public void insert(Employee employee) {
        employeeMapper.insert(employee);
    }

    public void updateById(Employee employee) {
        employeeMapper.updateById(employee);
    }

    public List<Employee> selectAll(Employee employee) {
         return employeeMapper.selectAll(employee);
        }

    public Employee selectByID(Integer id) {
        return employeeMapper.selectByID(id);
    }

    public PageInfo<Employee> selectPage(Employee employee,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return PageInfo.of(list);
    }


    public void deleteById(Integer id) {
        employeeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for(Integer id:ids){
            this.deleteById(id);
        }
    }

    public Employee login(Employee employee) throws CustomException {
        String username=employee.getUsername();
        Employee dbEmployee=employeeMapper.selectByusername(username);
        if(dbEmployee==null){
            throw new CustomException("账号不存在","500");
        }
        String password=employee.getPassword();
        if(!password.equals(dbEmployee.getPassword())){
            throw new CustomException("密码错误","500");
        }
        return dbEmployee;
    }

    public void register(Employee employee) throws CustomException {
        String username=employee.getUsername();
        Employee dbEmployee=employeeMapper.selectByusername(username);
        if(dbEmployee!=null){
            throw new CustomException("账号已存在","500");
        }
       this.insert(employee);
    }
}

