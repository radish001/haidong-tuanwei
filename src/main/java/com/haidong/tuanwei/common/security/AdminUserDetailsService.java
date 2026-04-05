package com.haidong.tuanwei.common.security;

import com.haidong.tuanwei.auth.dao.AdminUserDao;
import com.haidong.tuanwei.auth.entity.AdminUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserDao adminUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser user = adminUserDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new AdminUserDetails(user, adminUserDao.findRoleCodesByUserId(user.getId()));
    }
}
