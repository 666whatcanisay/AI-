package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("volunteer_signup")
public class VolunteerSignup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long volunteerId;

    private Long memberId;

    private String memberName;

    private String status;

    private String serviceHoursActual;

    private LocalDateTime signupTime;
}
