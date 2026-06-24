package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("party_fee")
public class PartyFee {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String memberName;

    private String year;

    private String month;

    private BigDecimal amount;

    private String status;

    private LocalDateTime payTime;

    private LocalDateTime createTime;
}