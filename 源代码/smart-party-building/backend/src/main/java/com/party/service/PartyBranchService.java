package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.PartyBranch;

public interface PartyBranchService extends IService<PartyBranch> {

    void syncMemberCounts();
}