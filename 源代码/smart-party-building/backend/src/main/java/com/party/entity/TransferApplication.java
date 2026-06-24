package com.party.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transfer_application")
public class TransferApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String memberName;

    private String currentBranch;

    private String targetBranch;

    private String reason;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime reviewTime;

    private String reviewComment;
}
