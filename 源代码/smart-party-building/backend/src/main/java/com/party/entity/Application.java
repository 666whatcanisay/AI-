package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("application")
public class Application {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String password;

    private String gender;

    private String birthDate;

    private String department;

    private String phone;

    private String idCard;

    private String studentNo;

    private String className;

    private String email;

    private String nativePlace;

    private String education;

    private String politicalStatus;

    private String reason;

    private String status;

    private String fileUrl;

    private String currentLevel;

    private String introducer;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastUpdateTime;
}
