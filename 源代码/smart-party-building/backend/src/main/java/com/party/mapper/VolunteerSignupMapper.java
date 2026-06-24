package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.VolunteerSignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VolunteerSignupMapper extends BaseMapper<VolunteerSignup> {

    @Select("SELECT * FROM volunteer_signup WHERE volunteer_id = #{volunteerId} ORDER BY signup_time ASC")
    List<VolunteerSignup> findByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Select("SELECT * FROM volunteer_signup WHERE volunteer_id = #{volunteerId} AND member_id = #{memberId}")
    VolunteerSignup findByVolunteerAndMember(@Param("volunteerId") Long volunteerId, @Param("memberId") Long memberId);

    @Select("SELECT COALESCE(SUM(CAST(vs.service_hours_actual AS SIGNED)), 0) FROM volunteer_signup vs JOIN volunteer v ON vs.volunteer_id = v.id WHERE vs.member_id = #{memberId} AND vs.status = '已完成'")
    Integer getTotalHours(@Param("memberId") Long memberId);
    
    @Select("SELECT COUNT(*) FROM volunteer_signup WHERE volunteer_id = #{volunteerId}")
    int countByVolunteerId(@Param("volunteerId") Long volunteerId);
}
