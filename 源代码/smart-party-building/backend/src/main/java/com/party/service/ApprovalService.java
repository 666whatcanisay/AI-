package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.ApprovalRecord;

import java.util.List;

public interface ApprovalService extends IService<ApprovalRecord> {

    List<ApprovalRecord> getByApplicationId(Long applicationId);

    boolean approve(Long applicationId, boolean approved, String comment);
}
