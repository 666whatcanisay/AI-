package com.party.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String role;

    private Long memberId;

    private String realName;
}
