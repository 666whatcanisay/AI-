package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    @Update("ALTER TABLE application AUTO_INCREMENT = 1")
    void resetAutoIncrement();
}
