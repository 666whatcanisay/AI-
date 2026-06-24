package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Application;
import com.party.entity.ApprovalRecord;
import com.party.mapper.ApprovalRecordMapper;
import com.party.mapper.ApplicationMapper;
import com.party.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalServiceImpl extends ServiceImpl<ApprovalRecordMapper, ApprovalRecord> implements ApprovalService {

    private final ApplicationMapper applicationMapper;

    public ApprovalServiceImpl(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    @Override
    public List<ApprovalRecord> getByApplicationId(Long applicationId) {
        return baseMapper.findByApplicationId(applicationId);
    }

    @Override
    @Transactional
    public boolean approve(Long applicationId, boolean approved, String comment) {
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new RuntimeException("申请不存在");
        }

        // 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(applicationId);
        record.setApproved(approved);
        record.setComment(comment);
        record.setApproveTime(LocalDateTime.now());

        // 根据当前状态确定审批层级
        String currentLevel = getLevelByStatus(application.getStatus());
        record.setLevelName(getLevelLabel(currentLevel));

        // 保存审批记录
        save(record);

        // 更新申请状态
        if (approved) {
            String nextStatus = getNextStatus(application.getStatus());
            application.setStatus(nextStatus);
        } else {
            application.setStatus("APPLYING");
        }
        application.setUpdateTime(LocalDateTime.now());
        applicationMapper.updateById(application);

        return true;
    }

    private String getLevelByStatus(String status) {
        return switch (status) {
            case "APPLYING" -> "LEVEL_1";
            case "ACTIVIST" -> "LEVEL_2";
            case "DEVELOP_TARGET" -> "LEVEL_3";
            case "PROBATIONARY" -> "LEVEL_3";
            default -> "LEVEL_1";
        };
    }

    private String getLevelLabel(String level) {
        return switch (level) {
            case "LEVEL_1" -> "支部审核";
            case "LEVEL_2" -> "党委审核";
            case "LEVEL_3" -> "组织部审核";
            default -> level;
        };
    }

    private String getNextStatus(String currentStatus) {
        return switch (currentStatus) {
            case "APPLYING" -> "ACTIVIST";
            case "ACTIVIST" -> "DEVELOP_TARGET";
            case "DEVELOP_TARGET" -> "PROBATIONARY";
            case "PROBATIONARY" -> "FORMAL";
            default -> currentStatus;
        };
    }
}
