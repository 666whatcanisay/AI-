package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Report;
import com.party.entity.ReportTask;
import com.party.entity.ReportTaskAssignment;
import com.party.entity.PartyMember;
import com.party.service.ReportService;
import com.party.service.ReportTaskService;
import com.party.service.ReportTaskAssignmentService;
import com.party.service.PartyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@Tag(name = "思想汇报管理", description = "思想汇报管理接口")
public class ReportController {

    private final ReportService reportService;
    private final ReportTaskService reportTaskService;
    private final PartyMemberService partyMemberService;
    private final ReportTaskAssignmentService reportTaskAssignmentService;

    public ReportController(ReportService reportService, ReportTaskService reportTaskService,
                            PartyMemberService partyMemberService,
                            ReportTaskAssignmentService reportTaskAssignmentService) {
        this.reportService = reportService;
        this.reportTaskService = reportTaskService;
        this.partyMemberService = partyMemberService;
        this.reportTaskAssignmentService = reportTaskAssignmentService;
    }

    @GetMapping("/task")
    @Operation(summary = "获取汇报任务列表")
    public Result<IPage<ReportTask>> getTaskList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String quarter,
            @RequestParam(required = false) String status) {
        Page<ReportTask> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ReportTask> wrapper = new QueryWrapper<>();
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        if (quarter != null && !quarter.isEmpty()) {
            wrapper.eq("quarter", quarter);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("publish_time");
        IPage<ReportTask> result = reportTaskService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/task/{id}")
    @Operation(summary = "获取汇报任务详情")
    public Result<ReportTask> getTaskById(@PathVariable Long id) {
        ReportTask task = reportTaskService.getById(id);
        return Result.success(task);
    }

    @PostMapping("/task")
    @Operation(summary = "发布汇报任务")
    public Result<ReportTask> publishTask(@RequestBody ReportTask task) {
        LocalDateTime now = LocalDateTime.now();
        task.setStatus("已发布");
        task.setPublishTime(now);
        task.setCreateTime(now);
        task.setUpdateTime(now);
        
        System.out.println("收到的任务数据: " + task);
        System.out.println("截止时间: " + task.getDeadline());
        
        // 验证截止时间必须晚于发布时间
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            try {
                LocalDateTime deadline = parseDateTime(task.getDeadline());
                System.out.println("解析后的截止时间: " + deadline);
                System.out.println("当前时间: " + now);
                System.out.println("截止时间是否在当前时间之前: " + deadline.isBefore(now));
                
                if (deadline.isBefore(now)) {
                    return Result.error("截止时间必须晚于当前时间，请选择未来的日期");
                }
                // 转换为标准格式存储
                task.setDeadline(deadline.toString());
            } catch (Exception e) {
                System.err.println("日期解析失败: " + task.getDeadline());
                e.printStackTrace();
                return Result.error("截止时间格式不正确，请重新选择日期");
            }
        }
        
        reportTaskService.save(task);
        QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
        memberWrapper.in("party_status", "FORMAL", "PROBATIONARY");
        List<PartyMember> eligibleMembers = partyMemberService.list(memberWrapper);
        if (!eligibleMembers.isEmpty()) {
            List<ReportTaskAssignment> assignments = eligibleMembers.stream().map(member -> {
                ReportTaskAssignment assignment = new ReportTaskAssignment();
                assignment.setTaskId(task.getId());
                assignment.setMemberId(member.getId());
                assignment.setMemberName(member.getName());
                assignment.setAssignedTime(now);
                return assignment;
            }).toList();
            reportTaskAssignmentService.saveBatch(assignments);
        }
        System.out.println("任务保存成功");
        return Result.success(task);
    }

    @PutMapping("/task/{id}")
    @Operation(summary = "更新汇报任务")
    public Result<ReportTask> updateTask(@PathVariable Long id, @RequestBody ReportTask task) {
        ReportTask existing = reportTaskService.getById(id);
        if (existing == null) {
            return Result.error("任务不存在");
        }
        
        task.setId(id);
        task.setUpdateTime(LocalDateTime.now());
        
        // 验证截止时间必须晚于发布时间
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            try {
                LocalDateTime deadline = parseDateTime(task.getDeadline());
                LocalDateTime publishTime = existing.getPublishTime() != null ? existing.getPublishTime() : LocalDateTime.now();
                if (deadline.isBefore(publishTime) || deadline.isEqual(publishTime)) {
                    return Result.error("截止时间必须晚于发布时间");
                }
                // 转换为标准格式存储
                task.setDeadline(deadline.toString());
            } catch (Exception e) {
                return Result.error("截止时间格式不正确");
            }
        }
        
        reportTaskService.updateById(task);
        return Result.success(task);
    }

    @PutMapping("/task/{id}/stop")
    @Operation(summary = "手动停止汇报任务")
    public Result<ReportTask> stopTask(@PathVariable Long id) {
        ReportTask task = reportTaskService.getById(id);
        if (task == null) {
            return Result.error("任务不存在");
        }
        if ("已结束".equals(task.getStatus())) {
            return Result.error("任务已经是结束状态");
        }
        
        task.setStatus("已结束");
        task.setUpdateTime(LocalDateTime.now());
        reportTaskService.updateById(task);
        return Result.success(task);
    }

    @PutMapping("/task/update-expired")
    @Operation(summary = "更新过期任务状态（自动检查并结束已过期的任务）")
    public Result<String> updateExpiredTasks() {
        LocalDateTime now = LocalDateTime.now();
        QueryWrapper<ReportTask> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "已发布");
        List<ReportTask> tasks = reportTaskService.list(wrapper);
        
        int count = 0;
        for (ReportTask task : tasks) {
            if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                try {
                    LocalDateTime deadline = parseDateTime(task.getDeadline());
                    if (now.isAfter(deadline)) {
                        task.setStatus("已结束");
                        task.setUpdateTime(now);
                        reportTaskService.updateById(task);
                        count++;
                    }
                } catch (Exception e) {
                    // 跳过解析失败的任务
                }
            }
        }
        
        return Result.success("已更新 " + count + " 个过期任务");
    }

    @DeleteMapping("/task/{id}")
    @Operation(summary = "删除汇报任务")
    public Result<String> deleteTask(@PathVariable Long id) {
        // 删除关联的汇报记录
        QueryWrapper<Report> reportWrapper = new QueryWrapper<>();
        reportWrapper.eq("task_id", id);
        reportService.remove(reportWrapper);
        
        // 删除任务
        reportTaskAssignmentService.remove(
                new QueryWrapper<ReportTaskAssignment>().eq("task_id", id));
        reportTaskService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/task/{taskId}/status")
    @Operation(summary = "查看汇报任务的完成情况")
    public Result<Map<String, Object>> getTaskStatus(@PathVariable Long taskId) {
        ReportTask task = reportTaskService.getById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        long totalMembers = reportTaskAssignmentService.count(
                new QueryWrapper<ReportTaskAssignment>().eq("task_id", taskId));

        QueryWrapper<Report> reportWrapper = new QueryWrapper<>();
        reportWrapper.eq("task_id", taskId).eq("status", "APPROVED");
        long completedCount = reportService.count(reportWrapper);

        QueryWrapper<Report> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("task_id", taskId).eq("status", "PENDING");
        long pendingCount = reportService.count(pendingWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("task", task);
        result.put("totalMembers", totalMembers);
        result.put("completedCount", completedCount);
        result.put("pendingCount", pendingCount);
        result.put("unfinishedCount", totalMembers - completedCount);

        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "获取汇报列表", description = "分页获取思想汇报列表")
    public Result<IPage<Report>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like("title", kw).or().like("author", kw));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        IPage<Report> result = reportService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取汇报详情", description = "根据ID获取汇报详情")
    public Result<Report> getById(@PathVariable Long id) {
        Report report = reportService.getById(id);
        return Result.success(report);
    }

    @PostMapping
    @Operation(summary = "提交汇报", description = "提交思想汇报")
    public Result<Report> add(@RequestBody Report report) {
        report.setStatus("PENDING");
        report.setCreateTime(LocalDateTime.now());
        reportService.save(report);
        return Result.success(report);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新汇报", description = "更新思想汇报（用于重新提交被驳回的汇报）")
    public Result<Report> update(@PathVariable Long id, @RequestBody Report report) {
        Report existing = reportService.getById(id);
        if (existing == null) {
            return Result.error("汇报不存在");
        }
        existing.setTitle(report.getTitle());
        existing.setReportDate(report.getReportDate());
        existing.setContent(report.getContent());
        existing.setStatus("PENDING");
        existing.setReviewComment(null);
        existing.setReviewTime(null);
        reportService.updateById(existing);
        return Result.success(existing);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除汇报", description = "删除思想汇报")
    public Result<String> delete(@PathVariable Long id) {
        reportService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/review")
    @Operation(summary = "审核汇报", description = "审核思想汇报")
    public Result<Report> review(@PathVariable Long id, @RequestBody Report report) {
        Report existing = reportService.getById(id);
        if (existing == null) {
            return Result.error("汇报不存在");
        }
        existing.setStatus(report.getStatus());
        existing.setReviewComment(report.getReviewComment());
        existing.setReviewTime(LocalDateTime.now());
        reportService.updateById(existing);
        return Result.success(existing);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取汇报统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> result = new HashMap<>();

        long total = reportService.count();
        result.put("total", total);

        QueryWrapper<Report> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "PENDING");
        long pending = reportService.count(pendingWrapper);
        result.put("pending", pending);

        QueryWrapper<Report> approvedWrapper = new QueryWrapper<>();
        approvedWrapper.eq("status", "APPROVED");
        long approved = reportService.count(approvedWrapper);
        result.put("approved", approved);

        QueryWrapper<Report> rejectedWrapper = new QueryWrapper<>();
        rejectedWrapper.eq("status", "REJECTED");
        long rejected = reportService.count(rejectedWrapper);
        result.put("rejected", rejected);

        long taskCount = reportTaskService.count();
        result.put("taskCount", taskCount);

        return Result.success(result);
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清理所有汇报数据")
    public Result<String> cleanAll() {
        reportService.remove(new QueryWrapper<>());
        reportTaskService.remove(new QueryWrapper<>());
        return Result.success("清理成功");
    }

    /**
     * 解析日期时间字符串，支持多种格式
     * @param dateTimeStr 日期时间字符串（如 "2026-05-31" 或 "2026-05-31T16:00:00" 或 "2026-05-31T16:00:00.000Z"）
     * @return LocalDateTime 对象
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            throw new IllegalArgumentException("日期时间不能为空");
        }
        
        // 移除Z后缀（如果有）
        if (dateTimeStr.endsWith("Z")) {
            dateTimeStr = dateTimeStr.substring(0, dateTimeStr.length() - 1);
        }
        
        // 移除毫秒部分（如果有）
        if (dateTimeStr.contains(".")) {
            int dotIndex = dateTimeStr.indexOf(".");
            dateTimeStr = dateTimeStr.substring(0, dotIndex);
        }
        
        // 如果只有日期部分，添加默认时间（23:59:59）
        if (dateTimeStr.length() <= 10) {
            dateTimeStr = dateTimeStr + "T23:59:59";
        }
        
        // 如果有日期但没有时间，添加默认时间
        if (dateTimeStr.length() <= 13 && dateTimeStr.contains("T")) {
            dateTimeStr = dateTimeStr + ":00:00";
        }
        
        return LocalDateTime.parse(dateTimeStr);
    }
}
