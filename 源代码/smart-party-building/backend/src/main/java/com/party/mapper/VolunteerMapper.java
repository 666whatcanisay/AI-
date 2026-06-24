package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.Volunteer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VolunteerMapper extends BaseMapper<Volunteer> {
    
    @Select("SELECT COUNT(*) FROM volunteer_signup WHERE volunteer_id = #{volunteerId}")
    int countSignups(@Param("volunteerId") Long volunteerId);
}
