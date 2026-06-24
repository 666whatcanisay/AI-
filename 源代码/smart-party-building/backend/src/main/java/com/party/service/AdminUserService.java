package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.AdminUser;

public interface AdminUserService extends IService<AdminUser> {

    AdminUser findByUsername(String username);
}
