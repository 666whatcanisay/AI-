package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("party_branch")
public class PartyBranch {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String leaderName;

    private String leaderPhone;

    private Integer memberCount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}