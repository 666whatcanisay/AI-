package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Application;
import com.party.entity.ApprovalRecord;
import com.party.entity.DevelopmentReport;
import com.party.entity.PartyMember;
import com.party.service.ApplicationService;
import com.party.service.ApprovalService;
import com.party.service.DevelopmentReportService;
import com.party.service.PartyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
@Tag(name = "审批管理", description = "审批管理接口")
public class ApprovalController {

    private final ApplicationService applicationService;
    private final ApprovalService approvalService;
    private final PartyMemberService partyMemberService;
    private final DevelopmentReportService developmentReportService;

    public ApprovalController(ApplicationService applicationService,
                              ApprovalService approvalService,
                              PartyMemberService partyMemberService,
                              DevelopmentReportService developmentReportService) {
        this.applicationService = applicationService;
        this.approvalService = approvalService;
        this.partyMemberService = partyMemberService;
        this.developmentReportService = developmentReportService;
    }

    @GetMapping
    @Operation(summary = "获取审批列表", description = "分页获取待审批列表")
    public Result<IPage<Application>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String level) {
        Page<Application> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Application> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        // 排除已成为正式党员的记录
        wrapper.ne("status", "FORMAL");
        wrapper.orderByDesc("create_time");
        IPage<Application> result = applicationService.page(page, wrapper);

        // 为每条记录设置当前审批层级
        result.getRecords().forEach(app -> {
            String currentLevel = getLevelByStatus(app.getStatus());
            app.setCurrentLevel(currentLevel);
        });

        return Result.success(result);
    }

    @PostMapping("/{id}")
    @Operation(summary = "审批操作", description = "审批通过或驳回")
    public Result<String> approve(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Boolean approved = (Boolean) params.get("approved");
        String comment = (String) params.get("comment");

        Application application = applicationService.getById(id);
        if (application == null) {
            return Result.error("申请不存在");
        }

        // 审批前检查是否已提交并通过了对应类型的思想报告
        String requiredReportType = getRequiredReportType(application.getStatus());
        if (requiredReportType != null) {
            QueryWrapper<DevelopmentReport> reportWrapper = new QueryWrapper<>();
            reportWrapper.eq("application_id", id)
                         .eq("report_type", requiredReportType)
                         .eq("status", "APPROVED");
            long approvedReportCount = developmentReportService.count(reportWrapper);
            if (approvedReportCount == 0) {
                // 检查是否有待审核的报告
                QueryWrapper<DevelopmentReport> pendingWrapper = new QueryWrapper<>();
                pendingWrapper.eq("application_id", id)
                              .eq("report_type", requiredReportType)
                              .eq("status", "PENDING");
                long pendingCount = developmentReportService.count(pendingWrapper);
                if (pendingCount > 0) {
                    return Result.error("申请人已提交思想报告但尚未审核通过，请先审核思想报告");
                }
                return Result.error("申请人尚未提交本阶段所需的思想报告，无法审批");
            }
        }

        // 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(id);
        record.setApproved(approved);
        record.setComment(comment);
        record.setApproveTime(LocalDateTime.now());
        String currentLevel = getLevelByStatus(application.getStatus());
        record.setLevelName(getLevelLabel(currentLevel));
        approvalService.save(record);

        // 更新申请状态
        if (approved) {
            String nextStatus = getNextStatus(application.getStatus());
            application.setStatus(nextStatus);

            // 审批成为预备党员时，自动添加到党员表
            if ("PROBATIONARY".equals(nextStatus)) {
                PartyMember member = new PartyMember();
                member.setName(application.getName());
                member.setPassword(application.getPassword() != null ? application.getPassword() : "123456");
                member.setGender(application.getGender());
                member.setBirthDate(application.getBirthDate());
                member.setIdCard(application.getIdCard());
                member.setStudentNo(application.getStudentNo());
                member.setClassName(application.getClassName());
                member.setPhone(application.getPhone());
                member.setEmail(application.getEmail());
                member.setNativePlace(application.getNativePlace());
                member.setEducation(application.getEducation());
                member.setPoliticalStatus("中共党员");
                member.setPartyStatus("PROBATIONARY");
                member.setBranchName(application.getDepartment());
                member.setIntroducer(application.getIntroducer());
                member.setJoinPartyTime(LocalDateTime.now());
                member.setCreateTime(LocalDateTime.now());
                member.setUpdateTime(LocalDateTime.now());
                partyMemberService.save(member);
            }

            // 审批成为正式党员时，更新党员表状态并删除申请记录
            if ("FORMAL".equals(nextStatus)) {
                QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
                // 使用身份证号唯一标识成员，避免同名问题
                memberWrapper.eq("id_card", application.getIdCard());
                PartyMember member = partyMemberService.getOne(memberWrapper, false);
                if (member != null) {
                    member.setPartyStatus("FORMAL");
                    member.setUpdateTime(LocalDateTime.now());
                    partyMemberService.updateById(member);
                }
                // 成为正式党员后，从申请表中删除该记录
                application.setUpdateTime(LocalDateTime.now());
                applicationService.updateById(application);
                return Result.success("审批成功，该成员已成为正式党员");
            }
        }
        application.setUpdateTime(LocalDateTime.now());
        applicationService.updateById(application);

        return Result.success("审批成功");
    }

    private String getLevelByStatus(String status) {
        return switch (status) {
            case "NONE" -> "LEVEL_1";
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
            case "NONE" -> "APPLYING";
            case "APPLYING" -> "ACTIVIST";
            case "ACTIVIST" -> "DEVELOP_TARGET";
            case "DEVELOP_TARGET" -> "PROBATIONARY";
            case "PROBATIONARY" -> "FORMAL";
            default -> currentStatus;
        };
    }

    /**
     * 根据当前申请状态获取所需的思想报告类型
     * NONE -> 需要APPLICATION报告才能进入APPLYING
     * APPLYING -> 需要ACTIVIST报告才能审批进入ACTIVIST
     * ACTIVIST -> 需要DEVELOP报告才能审批进入DEVELOP_TARGET
     * DEVELOP_TARGET -> 需要PROBATIONARY报告才能审批进入PROBATIONARY
     * PROBATIONARY -> 需要FORMAL报告才能审批进入FORMAL
     */
    private String getRequiredReportType(String currentStatus) {
        return switch (currentStatus) {
            case "NONE" -> "APPLICATION";
            case "APPLYING" -> "ACTIVIST";
            case "ACTIVIST" -> "DEVELOP";
            case "DEVELOP_TARGET" -> "PROBATIONARY";
            case "PROBATIONARY" -> "FORMAL";
            default -> null;
        };
    }
}
