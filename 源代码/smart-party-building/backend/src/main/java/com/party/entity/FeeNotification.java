package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fee_notification")
public class FeeNotification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String year;

    private String month;

    private String title;

    private String content;

    private String status;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;
}
