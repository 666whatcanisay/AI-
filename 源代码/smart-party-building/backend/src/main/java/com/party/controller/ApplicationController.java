package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Application;
import com.party.entity.ApprovalRecord;
import com.party.entity.DevelopmentReport;
import com.party.service.ApplicationService;
import com.party.service.ApprovalService;
import com.party.service.DevelopmentReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/application")
@Tag(name = "入党申请管理", description = "入党申请管理接口")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApprovalService approvalService;
    private final DevelopmentReportService developmentReportService;

    public ApplicationController(ApplicationService applicationService, ApprovalService approvalService, DevelopmentReportService developmentReportService) {
        this.applicationService = applicationService;
        this.approvalService = approvalService;
        this.developmentReportService = developmentReportService;
    }

    @GetMapping
    @Operation(summary = "获取申请列表", description = "分页获取入党申请列表")
    public Result<IPage<Application>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
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
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取申请详情", description = "根据ID获取申请详情")
    public Result<Application> getById(@PathVariable Long id) {
        Application application = applicationService.getById(id);
        return Result.success(application);
    }

    @PostMapping
    @Operation(summary = "提交申请", description = "管理员新增入党申请，只需填写姓名")
    public Result<Application> add(@RequestBody Application application) {
        // 检查是否已有同名申请
        QueryWrapper<Application> existWrapper = new QueryWrapper<>();
        existWrapper.eq("name", application.getName()).orderByDesc("create_time").last("LIMIT 1");
        Application existing = applicationService.getOne(existWrapper, false);
        if (existing != null) {
            return Result.error("该姓名已存在申请记录，请勿重复添加");
        }
        if (application.getPassword() == null || application.getPassword().isEmpty()) {
            application.setPassword("123456");
        }
        application.setStatus("NONE");
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        applicationService.save(application);
        return Result.success(application);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新申请", description = "更新申请信息")
    public Result<Application> update(@PathVariable Long id, @RequestBody Application application) {
        application.setId(id);
        application.setUpdateTime(LocalDateTime.now());
        applicationService.updateById(application);
        return Result.success(application);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除申请", description = "删除申请及相关审批记录和思想报告")
    public Result<String> delete(@PathVariable Long id) {
        // 级联删除审批记录
        approvalService.remove(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ApprovalRecord>().eq("application_id", id));
        // 级联删除思想报告
        developmentReportService.remove(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<DevelopmentReport>().eq("application_id", id));
        // 删除申请
        applicationService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/{id}/process")
    @Operation(summary = "获取审批流程", description = "获取申请的审批流程步骤")
    public Result<List<Map<String, String>>> getProcess(@PathVariable Long id) {
        Application application = applicationService.getById(id);
        List<ApprovalRecord> records = approvalService.getByApplicationId(id);

        List<Map<String, String>> steps = new ArrayList<>();
        String[] statusKeys = {"APPLYING", "ACTIVIST", "DEVELOP_TARGET", "PROBATIONARY", "FORMAL"};
        String[] statusTitles = {"提交申请", "确定积极分子", "确定发展对象", "预备党员", "正式党员"};
        String[] levelNames = {"支部审核", "党委审核", "组织部审核"};

        for (int i = 0; i < statusKeys.length; i++) {
            Map<String, String> step = new HashMap<>();
            step.put("key", statusKeys[i]);
            step.put("title", statusTitles[i]);

            // 查找对应的审批记录
            if (i > 0 && i - 1 < records.size()) {
                ApprovalRecord record = records.get(i - 1);
                step.put("time", record.getApproveTime() != null ? record.getApproveTime().toString() : "");
                step.put("comment", record.getComment() != null ? record.getComment() : "");
                step.put("operator", record.getOperator() != null ? record.getOperator() : "");
            } else if (i == 0) {
                step.put("time", application != null && application.getCreateTime() != null ? application.getCreateTime().toString() : "");
                step.put("comment", "");
                step.put("operator", application != null ? application.getName() : "");
            } else {
                step.put("time", "");
                step.put("comment", "");
                step.put("operator", "");
            }
            steps.add(step);
        }
        return Result.success(steps);
    }

    @GetMapping("/{id}/records")
    @Operation(summary = "获取审批记录", description = "获取申请的审批记录列表")
    public Result<List<ApprovalRecord>> getRecords(@PathVariable Long id) {
        List<ApprovalRecord> records = approvalService.getByApplicationId(id);
        return Result.success(records);
    }
}
