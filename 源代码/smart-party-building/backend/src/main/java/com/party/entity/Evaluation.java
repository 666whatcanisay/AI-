package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("evaluation")
public class Evaluation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String memberName;

    private String year;

    private String month;

    private String grade;

    private String comment;

    private String evaluator;

    private LocalDateTime evaluateTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
