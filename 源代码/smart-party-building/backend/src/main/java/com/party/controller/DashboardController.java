package com.party.controller;

import com.party.common.Result;
import com.party.service.PartyMemberService;
import com.party.service.ActivityService;
import com.party.service.PartyFeeService;
import com.party.service.ApplicationService;
import com.party.service.ReportService;
import com.party.service.VolunteerService;
import com.party.service.PartyBranchService;
import com.party.entity.PartyMember;
import com.party.entity.Activity;
import com.party.entity.PartyFee;
import com.party.entity.Application;
import com.party.entity.Report;
import com.party.entity.Volunteer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "首页数据", description = "首页数据统计接口")
public class DashboardController {

    private final PartyMemberService partyMemberService;
    private final ActivityService activityService;
    private final PartyFeeService partyFeeService;
    private final ApplicationService applicationService;
    private final ReportService reportService;
    private final VolunteerService volunteerService;
    private final PartyBranchService partyBranchService;

    public DashboardController(PartyMemberService partyMemberService,
                               ActivityService activityService,
                               PartyFeeService partyFeeService,
                               ApplicationService applicationService,
                               ReportService reportService,
                               VolunteerService volunteerService,
                               PartyBranchService partyBranchService) {
        this.partyMemberService = partyMemberService;
        this.activityService = activityService;
        this.partyFeeService = partyFeeService;
        this.applicationService = applicationService;
        this.reportService = reportService;
        this.volunteerService = volunteerService;
        this.partyBranchService = partyBranchService;
    }

    @GetMapping("/stats")
    @Operation(summary = "获取统计数据", description = "获取首页各项统计数据")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 党员统计
        long memberTotal = partyMemberService.count();
        QueryWrapper<PartyMember> formalWrapper = new QueryWrapper<>();
        formalWrapper.eq("party_status", "FORMAL").or().isNull("party_status");
        long formalCount = partyMemberService.count(formalWrapper);
        QueryWrapper<PartyMember> probationaryWrapper = new QueryWrapper<>();
        probationaryWrapper.eq("party_status", "PROBATIONARY");
        long probationaryCount = partyMemberService.count(probationaryWrapper);
        stats.put("memberTotal", memberTotal);
        stats.put("formalCount", formalCount);
        stats.put("probationaryCount", probationaryCount);

        // 入党申请统计
        // 待审批：NONE和APPLYING状态
        QueryWrapper<Application> applyingWrapper = new QueryWrapper<>();
        applyingWrapper.in("status", "NONE", "APPLYING");
        long applyingCount = applicationService.count(applyingWrapper);
        
        // 培养对象：所有非FORMAL状态的申请（包括NONE、APPLYING、ACTIVIST、DEVELOP_TARGET、PROBATIONARY）
        QueryWrapper<Application> traineeWrapper = new QueryWrapper<>();
        traineeWrapper.ne("status", "FORMAL");
        long traineeCount = applicationService.count(traineeWrapper);
        
        // 各阶段统计
        QueryWrapper<Application> activistWrapper = new QueryWrapper<>();
        activistWrapper.eq("status", "ACTIVIST");
        long activistCount = applicationService.count(activistWrapper);
        
        QueryWrapper<Application> developTargetWrapper = new QueryWrapper<>();
        developTargetWrapper.eq("status", "DEVELOP_TARGET");
        long developTargetCount = applicationService.count(developTargetWrapper);
        
        QueryWrapper<Application> appProbationaryWrapper = new QueryWrapper<>();
        appProbationaryWrapper.eq("status", "PROBATIONARY");
        long appProbationaryCount = applicationService.count(appProbationaryWrapper);
        
        stats.put("applyingCount", applyingCount);
        stats.put("traineeCount", traineeCount);
        stats.put("activistCount", activistCount);
        stats.put("developTargetCount", developTargetCount);
        stats.put("appProbationaryCount", appProbationaryCount);

        // 活动统计
        long activityTotal = activityService.count();
        QueryWrapper<Activity> activeActivityWrapper = new QueryWrapper<>();
        activeActivityWrapper.eq("status", "进行中");
        long activeActivityCount = activityService.count(activeActivityWrapper);
        stats.put("activityTotal", activityTotal);
        stats.put("activeActivityCount", activeActivityCount);

        // 志愿服务统计
        long volunteerTotal = volunteerService.count();
        QueryWrapper<Volunteer> activeVolunteerWrapper = new QueryWrapper<>();
        activeVolunteerWrapper.and(w -> w.eq("status", "报名中").or().eq("status", "进行中"));
        long activeVolunteerCount = volunteerService.count(activeVolunteerWrapper);
        stats.put("volunteerTotal", volunteerTotal);
        stats.put("activeVolunteerCount", activeVolunteerCount);

        // 党费统计
        String currentYear = String.valueOf(Year.now().getValue());
        BigDecimal yearTotalFee = partyFeeService.sumByYear(currentYear);
        stats.put("yearTotalFee", yearTotalFee != null ? yearTotalFee : BigDecimal.ZERO);

        // 思想汇报统计
        QueryWrapper<Report> pendingReportWrapper = new QueryWrapper<>();
        pendingReportWrapper.eq("status", "PENDING");
        long pendingReportCount = reportService.count(pendingReportWrapper);
        stats.put("pendingReportCount", pendingReportCount);

        // 党支部统计
        long branchCount = partyBranchService.count();
        stats.put("branchCount", branchCount);

        return Result.success(stats);
    }

    @GetMapping("/recent-activities")
    @Operation(summary = "获取最近活动", description = "获取最近的活动列表")
    public Result<List<Activity>> getRecentActivities() {
        Page<Activity> page = new Page<>(1, 5);
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        Page<Activity> result = activityService.page(page, wrapper);
        return Result.success(result.getRecords());
    }

    @GetMapping("/recent-applications")
    @Operation(summary = "获取最近申请", description = "获取最近的入党申请列表")
    public Result<List<Application>> getRecentApplications() {
        Page<Application> page = new Page<>(1, 5);
        QueryWrapper<Application> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        Page<Application> result = applicationService.page(page, wrapper);
        return Result.success(result.getRecords());
    }

    @GetMapping("/recent-volunteers")
    @Operation(summary = "获取最近志愿活动", description = "获取最近的志愿活动列表")
    public Result<List<Volunteer>> getRecentVolunteers() {
        Page<Volunteer> page = new Page<>(1, 5);
        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        Page<Volunteer> result = volunteerService.page(page, wrapper);
        return Result.success(result.getRecords());
    }

    @GetMapping("/fee-monthly")
    @Operation(summary = "月度党费统计", description = "获取本年度月度党费缴纳统计")
    public Result<Map<String, Object>> getFeeMonthly() {
        String currentYear = String.valueOf(Year.now().getValue());
        Map<String, Object> result = new HashMap<>();
        BigDecimal totalAmount = partyFeeService.sumByYear(currentYear);
        result.put("year", currentYear);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        return Result.success(result);
    }

    @GetMapping("/member-branch")
    @Operation(summary = "支部党员统计", description = "获取各支部党员人数统计")
    public Result<List<Map<String, Object>>> getMemberByBranch() {
        QueryWrapper<PartyMember> wrapper = new QueryWrapper<>();
        wrapper.select("branch_name", "COUNT(*) as count").groupBy("branch_name");
        List<Map<String, Object>> result = partyMemberService.listMaps(wrapper);
        return Result.success(result);
    }
}
