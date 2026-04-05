package com.haidong.tuanwei.system.dao;

import com.haidong.tuanwei.system.entity.Menu;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MenuDao {

    List<Menu> findMenusByUserId(@Param("userId") Long userId);
}
