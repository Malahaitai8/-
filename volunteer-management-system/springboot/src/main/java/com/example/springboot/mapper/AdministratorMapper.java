package com.example.springboot.mapper;

import com.example.springboot.entity.Administrator;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

public interface AdministratorMapper {
    // 查询所有管理员
    List<Administrator> selectAll(Administrator administrator);

    /**
     * 根据管理员ID查询管理员信息 (包括密码，主要用于登录和密码验证)
     * @param adminId 管理员ID
     * @return 管理员对象，如果未找到则返回null
     */
    @Select("SELECT * FROM tbl_Administrator WHERE AdminID = #{adminId}")
    Administrator selectByAdminIdWithPassword(@Param("adminId") String adminId); // 更名以区分
    /**
     * 根据管理员ID查询管理员信息.
     * 注意：此方法会查询密码，通常用于内部验证或特定场景，
     * Service层在返回给Controller前应擦除密码。
     * 如果仅为显示，应创建不含密码的查询。
     *
     * @param adminId 管理员ID
     * @return 管理员对象，如果未找到则返回null
     */
    @Select("SELECT * FROM tbl_Administrator WHERE AdminID = #{adminId}")
    Administrator selectByAdminId(@Param("adminId") String adminId);

    /**
     * 调用存储过程 sp_AdminLogin 进行管理员登录
     *
     * 注意：MyBatis 处理存储过程时，RAISERROR 会被捕获为 SQLException。
     * 成功时 (RETURN 0)，存储过程中的 SELECT 语句的结果集会被返回。
     * 我们期望返回单个 Administrator 对象。
     *
     * @param params 包含 adminIdentifier 和 password
     * @return 登录成功时返回 Administrator 对象，否则 MyBatis 会因 SQLException 而抛出异常
     */

    @Options(statementType = StatementType.CALLABLE)
    Administrator callAdminLogin(Map<String, Object> params);

    // 根据管理员ID查询管理员信息
    @Select("SELECT * FROM administrator WHERE AdminID = #{adminId}")
    Administrator selectByID(String adminId);

    // 插入新的管理员信息
    void insert(Administrator administrator);

    // 根据管理员ID更新管理员信息
    void updateById(Administrator administrator);
    // 根据管理员ID删除管理员信息
    @Delete("DELETE FROM administrator WHERE AdminID = #{adminId}")
    void deleteById(String adminId);

    // 根据姓名查询管理员信息
    @Select("SELECT * FROM administrator WHERE Name = #{name}")
    Administrator selectByName(String name);

    /**
     * 更新管理员密码
     * @param adminId 管理员ID
     * @param newPassword 新密码 (应为加密后的密码)
     * @return 影响行数
     */
    int updatePassword(@Param("adminId") String adminId, @Param("newPassword") String newPassword);


}