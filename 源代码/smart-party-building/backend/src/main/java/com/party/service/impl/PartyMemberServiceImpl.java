package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.PartyMember;
import com.party.mapper.PartyMemberMapper;
import com.party.service.PartyMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartyMemberServiceImpl extends ServiceImpl<PartyMemberMapper, PartyMember> implements PartyMemberService {

    @Override
    public List<PartyMember> findByNameAndBranch(String name, String branchName) {
        return baseMapper.findByNameAndBranch(name, branchName);
    }

    @Override
    public void resetAutoIncrement() {
        baseMapper.resetAutoIncrement();
    }

    @Override
    public void reorderAfterDelete(Long deletedId) {
        baseMapper.reorderIds(deletedId);
        baseMapper.resetAutoIncrement();
    }

    @Override
    public void updateBranchNameByOldName(String oldName, String newName) {
        baseMapper.updateBranchNameByOldName(oldName, newName);
    }
}