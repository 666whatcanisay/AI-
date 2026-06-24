package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.PartyBranch;
import com.party.mapper.PartyBranchMapper;
import com.party.mapper.PartyMemberMapper;
import com.party.service.PartyBranchService;
import org.springframework.stereotype.Service;

@Service
public class PartyBranchServiceImpl extends ServiceImpl<PartyBranchMapper, PartyBranch> implements PartyBranchService {

    private final PartyMemberMapper partyMemberMapper;

    public PartyBranchServiceImpl(PartyMemberMapper partyMemberMapper) {
        this.partyMemberMapper = partyMemberMapper;
    }

    @Override
    public void syncMemberCounts() {
        for (PartyBranch branch : this.list()) {
            int count = partyMemberMapper.countByBranchName(branch.getName());
            branch.setMemberCount(count);
            this.updateById(branch);
        }
    }
}