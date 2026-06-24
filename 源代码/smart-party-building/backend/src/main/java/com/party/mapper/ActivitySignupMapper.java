package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.ActivitySignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    @Select("SELECT * FROM activity_signup WHERE activity_id = #{activityId}")
    List<ActivitySignup> findByActivityId(@Param("activityId") Long activityId);

    @Select("SELECT * FROM activity_signup WHERE member_id = #{memberId}")
    List<ActivitySignup> findByMemberId(@Param("memberId") Long memberId);

    @Select("SELECT COUNT(*) FROM activity_signup WHERE activity_id = #{activityId}")
    Integer countByActivityId(@Param("activityId") Long activityId);

    @Select("SELECT * FROM activity_signup WHERE activity_id = #{activityId} AND member_id = #{memberId}")
    ActivitySignup findByActivityAndMember(@Param("activityId") Long activityId, @Param("memberId") Long memberId);
}