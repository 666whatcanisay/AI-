package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

    @Select("SELECT * FROM approval_record WHERE application_id = #{applicationId} ORDER BY approve_time ASC")
    List<ApprovalRecord> findByApplicationId(@Param("applicationId") Long applicationId);
}
