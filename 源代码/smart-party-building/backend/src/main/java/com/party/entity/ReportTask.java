package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report_task")
public class ReportTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String year;

    private String quarter;

    private String month;

    private String deadline;

    private String status;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
