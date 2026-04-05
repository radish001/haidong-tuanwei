package com.haidong.tuanwei.auth.dao;

import com.haidong.tuanwei.auth.entity.AdminUser;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdminUserDao {

    AdminUser findByUsername(@Param("username") String username);

    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
}
