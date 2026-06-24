package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Update("ALTER TABLE activity AUTO_INCREMENT = 1")
    void resetAutoIncrement();
}