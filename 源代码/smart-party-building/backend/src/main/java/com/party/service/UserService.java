package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.dto.LoginDTO;
import com.party.entity.SysUser;
import com.party.vo.LoginVO;

public interface UserService extends IService<SysUser> {

    LoginVO login(LoginDTO loginDTO);

    SysUser findByUsername(String username);

    void resetAutoIncrement();
}
