package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.PartyMember;

import java.util.List;

public interface PartyMemberService extends IService<PartyMember> {

    List<PartyMember> findByNameAndBranch(String name, String branchName);

    void resetAutoIncrement();

    void reorderAfterDelete(Long deletedId);

    void updateBranchNameByOldName(String oldName, String newName);
}