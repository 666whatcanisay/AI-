
package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("development_report")
public class DevelopmentReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long applicationId;

    private String reportType;

    private String title;

    private String content;

    private String author;

    private String status;

    private String reviewComment;

    private String reviewer;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime reviewTime;
}
