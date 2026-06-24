package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Volunteer;
import com.party.entity.VolunteerSignup;
import com.party.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
@Tag(name = "志愿服务管理", description = "志愿服务管理接口")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping
    @Operation(summary = "获取志愿活动列表", description = "分页获取志愿活动列表")
    public Result<IPage<Volunteer>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        Page<Volunteer> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        IPage<Volunteer> result = volunteerService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取志愿活动详情", description = "根据ID获取志愿活动详情")
    public Result<Volunteer> getById(@PathVariable Long id) {
        Volunteer volunteer = volunteerService.getById(id);
        return Result.success(volunteer);
    }

    @PostMapping
    @Operation(summary = "发布志愿活动", description = "发布新志愿活动")
    public Result<Volunteer> add(@RequestBody Volunteer volunteer) {
        volunteer.setCurrentParticipants(0);
        volunteer.setCreateTime(LocalDateTime.now());
        volunteer.setUpdateTime(LocalDateTime.now());
        volunteerService.save(volunteer);
        return Result.success(volunteer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新志愿活动", description = "更新志愿活动信息")
    public Result<Volunteer> update(@PathVariable Long id, @RequestBody Volunteer volunteer) {
        volunteer.setId(id);
        volunteer.setUpdateTime(LocalDateTime.now());
        volunteerService.updateById(volunteer);
        return Result.success(volunteer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除志愿活动", description = "删除志愿活动及其相关报名记录")
    public Result<String> delete(@PathVariable Long id) {
        volunteerService.deleteWithSignups(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/signup")
    @Operation(summary = "志愿活动报名", description = "报名参加志愿活动")
    public Result<String> signup(@PathVariable Long id, @RequestBody VolunteerSignup signup) {
        volunteerService.signup(id, signup.getMemberId(), signup.getMemberName());
        return Result.success("报名成功");
    }

    @GetMapping("/{id}/signups")
    @Operation(summary = "获取报名列表", description = "获取志愿活动的报名列表")
    public Result<List<VolunteerSignup>> getSignups(@PathVariable Long id) {
        List<VolunteerSignup> signups = volunteerService.getSignups(id);
        return Result.success(signups);
    }

    @PutMapping("/{id}/finish")
    @Operation(summary = "结束志愿活动", description = "结束志愿活动并更新报名状态")
    public Result<String> finishActivity(@PathVariable Long id) {
        volunteerService.finishActivity(id);
        return Result.success("活动已结束");
    }

    @GetMapping("/stats/total-hours/{memberId}")
    @Operation(summary = "获取党员总志愿时长", description = "获取党员的累计志愿服务时长")
    public Result<Integer> getTotalHours(@PathVariable Long memberId) {
        Integer totalHours = volunteerService.getTotalHours(memberId);
        return Result.success(totalHours);
    }
}
