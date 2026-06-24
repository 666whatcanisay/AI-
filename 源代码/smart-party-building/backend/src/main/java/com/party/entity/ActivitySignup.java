package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_signup")
public class ActivitySignup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long memberId;

    private String memberName;

    private String status;

    private LocalDateTime signupTime;

    private LocalDateTime checkinTime;
}