package com.haidong.tuanwei.system.service.impl;

import com.haidong.tuanwei.system.dao.MenuDao;
import com.haidong.tuanwei.system.entity.Menu;
import com.haidong.tuanwei.system.service.MenuService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;

    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        List<Menu> flatMenus = menuDao.findMenusByUserId(userId);
        Map<Long, Menu> menuMap = new HashMap<>();
        List<Menu> roots = new ArrayList<>();

        for (Menu menu : flatMenus) {
            menu.getChildren().clear();
            menuMap.put(menu.getId(), menu);
        }

        for (Menu menu : flatMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                roots.add(menu);
                continue;
            }
            Menu parent = menuMap.get(menu.getParentId());
            if (parent != null) {
                parent.getChildren().add(menu);
            }
        }
        return roots;
    }
}
