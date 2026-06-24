package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser findByUsername(String username);

    @Update("ALTER TABLE sys_user AUTO_INCREMENT = 1")
    void resetAutoIncrement();
}
