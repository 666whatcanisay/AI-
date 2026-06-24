package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Long memberId;

    private String memberName;

    private String title;

    private String author;

    private String reportDate;

    private String content;

    private String status;

    private String fileUrl;

    private String reviewComment;

    private LocalDateTime createTime;

    private LocalDateTime reviewTime;
}
