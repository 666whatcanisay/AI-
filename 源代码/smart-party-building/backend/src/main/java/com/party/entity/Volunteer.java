package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("volunteer")
public class Volunteer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String location;

    private String serviceDate;

    private String serviceHours;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
