package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("party_member")
public class PartyMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String password;

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

    private String partyStatus;

    private String branchName;

    private LocalDateTime joinPartyTime;

    private String introducer;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}