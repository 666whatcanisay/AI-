package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Activity;
import com.party.entity.ActivitySignup;
import com.party.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
@Tag(name = "组织生活管理", description = "活动管理接口")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    @Operation(summary = "获取活动列表", description = "分页获取活动列表")
    public Result<IPage<Activity>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByAsc("create_time");
        IPage<Activity> result = activityService.page(page, wrapper);
        
        // 动态计算每个活动的报名人数
        for (Activity activity : result.getRecords()) {
            Integer count = activityService.getSignupCount(activity.getId());
            activity.setCurrentParticipants(count);
        }
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取活动详情", description = "根据ID获取活动详情")
    public Result<Activity> getById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        return Result.success(activity);
    }

    @PostMapping
    @Operation(summary = "发布活动", description = "发布新活动")
    public Result<Activity> add(@RequestBody Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setStatus("报名中");
        activity.setCreateTime(LocalDateTime.now());
        activityService.save(activity);
        return Result.success(activity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新活动", description = "更新活动信息")
    public Result<Activity> update(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        activityService.updateById(activity);
        return Result.success(activity);
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "开始活动", description = "将活动状态从报名中改为进行中")
    public Result<String> startActivity(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        activity.setStatus("进行中");
        activityService.updateById(activity);
        return Result.success("活动已开始");
    }

    @PutMapping("/{id}/end")
    @Operation(summary = "结束活动", description = "将活动状态改为已结束")
    public Result<String> endActivity(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.error("活动不存在");
        }
        activity.setStatus("已结束");
        activityService.updateById(activity);
        return Result.success("活动已结束");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动", description = "删除活动")
    public Result<String> delete(@PathVariable Long id) {
        activityService.removeById(id);
        activityService.resetAutoIncrement();
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/signup")
    @Operation(summary = "活动报名", description = "报名参加活动")
    public Result<String> signup(@PathVariable Long id, @RequestBody ActivitySignup signup) {
        activityService.signup(id, signup.getMemberId(), signup.getMemberName());
        return Result.success("报名成功");
    }

    @PostMapping("/{id}/checkin")
    @Operation(summary = "签到", description = "活动签到")
    public Result<String> checkin(@PathVariable Long id, @RequestParam Long memberId) {
        activityService.checkin(id, memberId);
        return Result.success("签到成功");
    }

    @GetMapping("/{id}/signups")
    @Operation(summary = "获取报名列表", description = "获取活动的报名列表")
    public Result<List<ActivitySignup>> getSignups(@PathVariable Long id) {
        List<ActivitySignup> signups = activityService.getSignups(id);
        return Result.success(signups);
    }

    @GetMapping("/member/{memberId}/signups")
    @Operation(summary = "获取个人报名", description = "获取个人的报名记录")
    public Result<List<ActivitySignup>> getMemberSignups(@PathVariable Long memberId) {
        List<ActivitySignup> signups = activityService.getMemberSignups(memberId);
        return Result.success(signups);
    }

    @PostMapping("/refresh-counts")
    @Operation(summary = "刷新所有活动报名人数", description = "根据实际报名记录重新统计所有活动的报名人数")
    public Result<String> refreshAllSignupCounts() {
        activityService.refreshAllSignupCounts();
        return Result.success("刷新成功");
    }

    @PostMapping("/{id}/refresh-count")
    @Operation(summary = "刷新单个活动报名人数", description = "根据实际报名记录重新统计指定活动的报名人数")
    public Result<String> refreshSignupCount(@PathVariable Long id) {
        activityService.refreshSignupCount(id);
        return Result.success("刷新成功");
    }
}