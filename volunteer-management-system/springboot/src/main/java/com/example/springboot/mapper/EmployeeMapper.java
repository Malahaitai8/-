package com.example.springboot.mapper;

import com.example.springboot.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmployeeMapper {
    List<Employee> selectAll(Employee employee);

    @Select("select * from employee where id=#{id}")
    Employee selectByID(Integer id);

    void insert(Employee employee);

    void updateById(Employee employee);

    void deleteById(Integer id);

    @Select("select * from employee where username=#{username}")
    Employee selectByusername(String username);
}
