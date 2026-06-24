package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String location;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String status;

    private LocalDateTime createTime;
}