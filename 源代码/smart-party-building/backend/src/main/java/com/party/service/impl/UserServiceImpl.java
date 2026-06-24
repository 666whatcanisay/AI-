package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.dto.LoginDTO;
import com.party.entity.SysUser;
import com.party.mapper.SysUserMapper;
import com.party.security.JwtUtil;
import com.party.service.UserService;
import com.party.vo.LoginVO;
import com.party.vo.UserVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(SysUserMapper sysUserMapper, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = sysUserMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRole(user.getRole());
        userVO.setMemberId(user.getMemberId());
        userVO.setRealName(user.getRealName());
        loginVO.setUser(userVO);
        return loginVO;
    }

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.findByUsername(username);
    }

    @Override
    public void resetAutoIncrement() {
        sysUserMapper.resetAutoIncrement();
    }
}
