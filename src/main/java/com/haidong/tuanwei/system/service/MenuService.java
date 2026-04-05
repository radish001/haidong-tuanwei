package com.haidong.tuanwei.system.service;

import com.haidong.tuanwei.system.entity.Menu;
import java.util.List;

public interface MenuService {

    List<Menu> getMenusByUserId(Long userId);
}
