package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.AdminUser;
import com.party.mapper.AdminUserMapper;
import com.party.service.AdminUserService;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final AdminUserMapper adminUserMapper;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    @Override
    public AdminUser findByUsername(String username) {
        return adminUserMapper.findByUsername(username);
    }
}
