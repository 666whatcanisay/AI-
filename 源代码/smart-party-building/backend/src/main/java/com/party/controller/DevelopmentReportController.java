
package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.party.common.Result;
import com.party.entity.Application;
import com.party.entity.DevelopmentReport;
import com.party.service.ApplicationService;
import com.party.service.DevelopmentReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/development-report")
@Tag(name = "发展流程报告", description = "发展流程思想报告管理接口")
public class DevelopmentReportController {

    private final DevelopmentReportService developmentReportService;
    private final ApplicationService applicationService;

    public DevelopmentReportController(DevelopmentReportService developmentReportService,
                                        ApplicationService applicationService) {
        this.developmentReportService = developmentReportService;
        this.applicationService = applicationService;
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "获取某个申请的所有报告")
    public Result<List<DevelopmentReport>> getByApplicationId(@PathVariable Long applicationId) {
        QueryWrapper<DevelopmentReport> wrapper = new QueryWrapper<>();
        wrapper.eq("application_id", applicationId).orderByAsc("create_time");
        List<DevelopmentReport> reports = developmentReportService.list(wrapper);
        return Result.success(reports);
    }

    @GetMapping("/application/{applicationId}/type/{reportType}")
    @Operation(summary = "获取某个申请的指定类型报告")
    public Result<List<DevelopmentReport>> getByApplicationIdAndType(
            @PathVariable Long applicationId,
            @PathVariable String reportType) {
        QueryWrapper<DevelopmentReport> wrapper = new QueryWrapper<>();
        wrapper.eq("application_id", applicationId)
               .eq("report_type", reportType)
               .orderByDesc("create_time");
        List<DevelopmentReport> reports = developmentReportService.list(wrapper);
        return Result.success(reports);
    }

    @PostMapping
    @Operation(summary = "提交发展流程报告")
    public Result<DevelopmentReport> submitReport(@RequestBody DevelopmentReport report) {
        // 检查申请是否存在
        Application application = applicationService.getById(report.getApplicationId());
        if (application == null) {
            return Result.error("申请记录不存在");
        }
        report.setStatus("PENDING");
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        developmentReportService.save(report);
        return Result.success(report);
    }

    @PutMapping("/{id}/review")
    @Operation(summary = "审核发展流程报告")
    public Result<String> reviewReport(@PathVariable Long id, @RequestBody DevelopmentReport reviewData) {
        DevelopmentReport report = developmentReportService.getById(id);
        if (report == null) {
            return Result.error("报告不存在");
        }
        report.setStatus(reviewData.getStatus());
        report.setReviewComment(reviewData.getReviewComment());
        report.setReviewer(reviewData.getReviewer());
        report.setReviewTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        developmentReportService.updateById(report);
        return Result.success("审核完成");
    }

    @GetMapping("/check/{applicationId}/{reportType}")
    @Operation(summary = "检查某个申请是否已提交指定类型的报告")
    public Result<Boolean> checkReportSubmitted(@PathVariable Long applicationId, @PathVariable String reportType) {
        QueryWrapper<DevelopmentReport> wrapper = new QueryWrapper<>();
        wrapper.eq("application_id", applicationId)
               .eq("report_type", reportType)
               .eq("status", "APPROVED");
        long count = developmentReportService.count(wrapper);
        return Result.success(count > 0);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取报告详情")
    public Result<DevelopmentReport> getById(@PathVariable Long id) {
        DevelopmentReport report = developmentReportService.getById(id);
        return Result.success(report);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除报告")
    public Result<String> deleteReport(@PathVariable Long id) {
        developmentReportService.removeById(id);
        return Result.success("删除成功");
    }
}
