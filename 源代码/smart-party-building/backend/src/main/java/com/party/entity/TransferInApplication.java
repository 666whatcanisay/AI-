package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transfer_in_application")
public class TransferInApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String gender;

    private String birthDate;

    private String idCard;

    private String studentNo;

    private String className;

    private String phone;

    private String email;

    private String nativePlace;

    private String education;

    private String politicalStatus;

    private String branchName;

    private String joinPartyTime;

    private String introducer;

    private String reason;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime reviewTime;

    private String reviewComment;
}