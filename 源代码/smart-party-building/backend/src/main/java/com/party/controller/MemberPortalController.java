package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.*;
import com.party.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portal")
@Tag(name = "党员门户", description = "党员登录后使用的接口")
public class MemberPortalController {

    private final PartyMemberService partyMemberService;
    private final PartyFeeService partyFeeService;
    private final ActivityService activityService;
    private final ActivitySignupService activitySignupService;
    private final ReportService reportService;
    private final VolunteerService volunteerService;
    private final VolunteerSignupService volunteerSignupService;
    private final ApplicationService applicationService;
    private final ApprovalService approvalService;
    private final DevelopmentReportService developmentReportService;
    private final FeeNotificationService feeNotificationService;
    private final ReportTaskService reportTaskService;
    private final ReportTaskAssignmentService reportTaskAssignmentService;

    public MemberPortalController(PartyMemberService partyMemberService,
                                   PartyFeeService partyFeeService,
                                   ActivityService activityService,
                                   ActivitySignupService activitySignupService,
                                   ReportService reportService,
                                   VolunteerService volunteerService,
                                   VolunteerSignupService volunteerSignupService,
                                   ApplicationService applicationService,
                                   ApprovalService approvalService,
                                   DevelopmentReportService developmentReportService,
                                   FeeNotificationService feeNotificationService,
                                   ReportTaskService reportTaskService,
                                   ReportTaskAssignmentService reportTaskAssignmentService) {
        this.partyMemberService = partyMemberService;
        this.partyFeeService = partyFeeService;
        this.activityService = activityService;
        this.activitySignupService = activitySignupService;
        this.reportService = reportService;
        this.volunteerService = volunteerService;
        this.volunteerSignupService = volunteerSignupService;
        this.applicationService = applicationService;
        this.approvalService = approvalService;
        this.developmentReportService = developmentReportService;
        this.feeNotificationService = feeNotificationService;
        this.reportTaskService = reportTaskService;
        this.reportTaskAssignmentService = reportTaskAssignmentService;
    }

    @GetMapping("/info/{memberId}")
    @Operation(summary = "获取党员个人信息")
    public Result<PartyMember> getMemberInfo(@PathVariable Long memberId) {
        PartyMember member = partyMemberService.getById(memberId);
        return Result.success(member);
    }

    @PutMapping("/info/{memberId}")
    @Operation(summary = "更新党员个人信息")
    public Result<PartyMember> updateMemberInfo(@PathVariable Long memberId, @RequestBody PartyMember member) {
        member.setId(memberId);
        member.setUpdateTime(LocalDateTime.now());
        partyMemberService.updateById(member);
        return Result.success(member);
    }


    @PostMapping("/application")
    @Operation(summary = "申请人提交入党申请")
    public Result<Application> submitApplication(@RequestBody Application application) {
        // 检查是否已有申请记录
        QueryWrapper<Application> existWrapper = new QueryWrapper<>();
        existWrapper.eq("name", application.getName()).orderByDesc("create_time").last("LIMIT 1");
        Application existing = applicationService.getOne(existWrapper, false);
        if (existing != null) {
            return Result.error("您已提交过入党申请，请勿重复提交");
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

    @PutMapping("/application/{id}")
    @Operation(summary = "申请人更新自己的申请信息")
    public Result<Application> updateApplicationInfo(@PathVariable Long id, @RequestBody Application application) {
        Application existing = applicationService.getById(id);
        if (existing == null) {
            return Result.error("申请记录不存在");
        }
        // 只允许更新以下字段
        existing.setGender(application.getGender());
        existing.setBirthDate(application.getBirthDate());
        existing.setDepartment(application.getDepartment());
        existing.setPhone(application.getPhone());
        existing.setIdCard(application.getIdCard());
        existing.setStudentNo(application.getStudentNo());
        existing.setClassName(application.getClassName());
        existing.setEmail(application.getEmail());
        existing.setNativePlace(application.getNativePlace());
        existing.setEducation(application.getEducation());
        existing.setPoliticalStatus(application.getPoliticalStatus());
        existing.setIntroducer(application.getIntroducer());
        existing.setUpdateTime(LocalDateTime.now());
        applicationService.updateById(existing);
        return Result.success(existing);
    }

    @GetMapping("/dashboard/{memberId}")
    @Operation(summary = "党员首页数据")
    public Result<Map<String, Object>> getDashboard(@PathVariable Long memberId) {
        Map<String, Object> data = new HashMap<>();

        // 党费统计
        java.math.BigDecimal totalFee = partyFeeService.sumByMemberAndYear(memberId, String.valueOf(LocalDateTime.now().getYear()));
        data.put("totalFee", totalFee != null ? totalFee : java.math.BigDecimal.ZERO);

        // 参与活动数
        QueryWrapper<ActivitySignup> activityWrapper = new QueryWrapper<>();
        activityWrapper.eq("member_id", memberId);
        long activityCount = activitySignupService.count(activityWrapper);
        data.put("activityCount", activityCount);

        // 思想汇报数
        PartyMember member = partyMemberService.getById(memberId);
        QueryWrapper<Report> reportWrapper = new QueryWrapper<>();
        if (member != null && member.getName() != null) {
            reportWrapper.eq("author", member.getName());
        }
        long reportCount = reportService.count(reportWrapper);
        data.put("reportCount", reportCount);

        // 志愿服务时长（只计算关联活动存在的记录）
        QueryWrapper<VolunteerSignup> volunteerWrapper = new QueryWrapper<>();
        volunteerWrapper.eq("member_id", memberId).eq("status", "已完成");
        List<VolunteerSignup> signups = volunteerSignupService.list(volunteerWrapper);
        double totalHours = signups.stream()
            .filter(s -> s.getServiceHoursActual() != null)
            .filter(s -> volunteerService.getById(s.getVolunteerId()) != null)
            .mapToDouble(s -> {
                try { return Double.parseDouble(s.getServiceHoursActual()); }
                catch (Exception e) { return 0; }
            })
            .sum();
        data.put("volunteerHours", totalHours);

        // 最近活动
        QueryWrapper<Activity> recentActivityWrapper = new QueryWrapper<>();
        recentActivityWrapper.orderByDesc("start_time").last("LIMIT 5");
        List<Activity> recentActivities = activityService.list(recentActivityWrapper);
        data.put("recentActivities", recentActivities);

        return Result.success(data);
    }

    @GetMapping("/fee/{memberId}")
    @Operation(summary = "获取我的党费记录")
    public Result<List<PartyFee>> getMyFees(@PathVariable Long memberId) {
        QueryWrapper<PartyFee> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId).orderByDesc("year").orderByDesc("month");
        List<PartyFee> fees = partyFeeService.list(wrapper);
        return Result.success(fees);
    }

    @GetMapping("/activity")
    @Operation(summary = "获取活动列表（党员端）")
    public Result<IPage<Activity>> getActivityList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("start_time");
        IPage<Activity> result = activityService.page(page, wrapper);
        return Result.success(result);
    }

    @PostMapping("/activity/signup")
    @Operation(summary = "报名活动")
    public Result<String> signupActivity(@RequestBody ActivitySignup signup) {
        // 检查是否已报名
        QueryWrapper<ActivitySignup> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", signup.getActivityId()).eq("member_id", signup.getMemberId());
        long count = activitySignupService.count(wrapper);
        if (count > 0) {
            return Result.error("您已报名该活动");
        }
        
        // 查询党员姓名
        if (signup.getMemberId() != null) {
            PartyMember member = partyMemberService.getById(signup.getMemberId());
            if (member != null) {
                signup.setMemberName(member.getName());
            }
        }
        
        signup.setSignupTime(LocalDateTime.now());
        activitySignupService.save(signup);
        return Result.success("报名成功");
    }

    @GetMapping("/activity/my/{memberId}")
    @Operation(summary = "获取我报名的活动")
    public Result<List<java.util.Map<String, Object>>> getMyActivities(@PathVariable Long memberId) {
        QueryWrapper<ActivitySignup> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId).orderByDesc("signup_time");
        List<ActivitySignup> signups = activitySignupService.list(wrapper);
        
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (ActivitySignup signup : signups) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", signup.getId());
            map.put("activityId", signup.getActivityId());
            map.put("memberId", signup.getMemberId());
            map.put("memberName", signup.getMemberName());
            map.put("status", signup.getStatus());
            map.put("signupTime", signup.getSignupTime());
            map.put("checkinTime", signup.getCheckinTime());
            
            // 查询活动名称
            Activity activity = activityService.getById(signup.getActivityId());
            if (activity != null) {
                map.put("activityTitle", activity.getTitle());
            } else {
                map.put("activityTitle", "未知活动");
            }
            result.add(map);
        }
        return Result.success(result);
    }

    @GetMapping("/report")
    @Operation(summary = "获取思想汇报列表（党员端）")
    public Result<IPage<Report>> getReportList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long memberId) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Report> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        wrapper.orderByDesc("create_time");
        IPage<Report> result = reportService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/report/task")
    @Operation(summary = "获取汇报任务列表（党员端）")
    public Result<List<ReportTask>> getReportTaskList(@RequestParam Long memberId,
                                                       HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("authenticatedUserId");
        if (authenticatedUserId == null || !authenticatedUserId.equals(memberId)) {
            return Result.error("无权查看其他党员的思想汇报任务");
        }
        // 获取当前党员信息，确定党员加入时间
        PartyMember member = partyMemberService.getById(memberId);
        if (member == null) {
            return Result.success(new java.util.ArrayList<>());
        }
        if (!"FORMAL".equals(member.getPartyStatus())
                && !"PROBATIONARY".equals(member.getPartyStatus())) {
            return Result.success(new java.util.ArrayList<>());
        }
        QueryWrapper<ReportTaskAssignment> assignmentWrapper = new QueryWrapper<>();
        assignmentWrapper.eq("member_id", memberId);
        List<Long> assignedTaskIds = reportTaskAssignmentService.list(assignmentWrapper).stream()
                .map(ReportTaskAssignment::getTaskId)
                .toList();
        if (assignedTaskIds.isEmpty()) {
            return Result.success(new java.util.ArrayList<>());
        }
        LocalDateTime memberCreateTime = member.getCreateTime();
        
        // 获取所有已发布的任务
        QueryWrapper<ReportTask> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "已发布").orderByDesc("publish_time");
        List<ReportTask> allTasks = reportTaskService.list(wrapper);
        allTasks = allTasks.stream()
                .filter(task -> assignedTaskIds.contains(task.getId()))
                .toList();
        
        // 获取当前党员已提交的任务ID列表
        QueryWrapper<Report> reportWrapper = new QueryWrapper<>();
        reportWrapper.eq("member_id", memberId);
        List<Report> submittedReports = reportService.list(reportWrapper);
        
        // 过滤掉已提交的任务，并且只显示党员加入后发布的任务
        List<ReportTask> pendingTasks = allTasks.stream()
            .filter(task -> {
                // 任务发布时间必须晚于或等于党员加入时间，党员才能看到
                // 如果任务发布时间早于党员加入时间，说明党员是后来加入的，不应看到历史任务
                LocalDateTime taskPublishTime = task.getPublishTime();
                if (taskPublishTime != null && memberCreateTime != null) {
                    if (taskPublishTime.isBefore(memberCreateTime)) {
                        return false;
                    }
                }
                return true;
            })
            .filter(task -> submittedReports.stream()
                .noneMatch(report -> task.getId().equals(report.getTaskId())))
            .collect(java.util.stream.Collectors.toList());
        
        return Result.success(pendingTasks);
    }

    @GetMapping("/report/stats/{memberId}")
    @Operation(summary = "获取个人汇报统计（党员端）")
    public Result<Map<String, Object>> getReportStats(@PathVariable Long memberId) {
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<Report> totalWrapper = new QueryWrapper<>();
        totalWrapper.eq("member_id", memberId);
        long total = reportService.count(totalWrapper);
        result.put("total", total);
        
        QueryWrapper<Report> approvedWrapper = new QueryWrapper<>();
        approvedWrapper.eq("member_id", memberId).eq("status", "APPROVED");
        long approved = reportService.count(approvedWrapper);
        result.put("approved", approved);
        
        QueryWrapper<Report> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("member_id", memberId).eq("status", "PENDING");
        long pending = reportService.count(pendingWrapper);
        result.put("pending", pending);
        
        return Result.success(result);
    }

    @PostMapping("/report")
    @Operation(summary = "提交思想汇报")
    public Result<Report> submitReport(@RequestBody Report report, HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("authenticatedUserId");
        if (authenticatedUserId == null || !authenticatedUserId.equals(report.getMemberId())) {
            return Result.error("无权代替其他党员提交思想汇报");
        }
        if (report.getTaskId() != null && report.getMemberId() != null) {
            PartyMember member = partyMemberService.getById(report.getMemberId());
            boolean eligible = member != null
                    && ("FORMAL".equals(member.getPartyStatus())
                    || "PROBATIONARY".equals(member.getPartyStatus()));
            long assignmentCount = reportTaskAssignmentService.count(
                    new QueryWrapper<ReportTaskAssignment>()
                            .eq("task_id", report.getTaskId())
                            .eq("member_id", report.getMemberId()));
            if (!eligible || assignmentCount == 0) {
                return Result.error("您不在本次思想汇报任务的提交名单中");
            }
        }
        // 检查任务是否存在且未过截止时间
        if (report.getTaskId() != null) {
            ReportTask task = reportTaskService.getById(report.getTaskId());
            if (task != null) {
                // 检查任务状态是否为已结束
                if ("已结束".equals(task.getStatus())) {
                    return Result.error("该任务已结束，无法提交");
                }
                
                // 检查是否已过截止时间
                if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                    try {
                        LocalDateTime deadline = parseDateTime(task.getDeadline());
                        if (LocalDateTime.now().isAfter(deadline)) {
                            return Result.error("该任务已过截止时间，无法提交");
                        }
                    } catch (Exception e) {
                        // 忽略日期解析错误
                    }
                }
            }
        }
        
        // 检查是否已提交（同一任务同一党员只能提交一次）
        if (report.getTaskId() != null && report.getMemberId() != null) {
            QueryWrapper<Report> wrapper = new QueryWrapper<>();
            wrapper.eq("task_id", report.getTaskId()).eq("member_id", report.getMemberId());
            long count = reportService.count(wrapper);
            if (count > 0) {
                return Result.error("您已提交过本次汇报任务");
            }
        }
        
        report.setStatus("PENDING");
        report.setCreateTime(LocalDateTime.now());
        if (report.getMemberId() != null) {
            PartyMember member = partyMemberService.getById(report.getMemberId());
            if (member != null) {
                report.setMemberName(member.getName());
                report.setAuthor(member.getName());
            }
        }
        reportService.save(report);
        return Result.success(report);
    }

    @GetMapping("/volunteer")
    @Operation(summary = "获取志愿活动列表（党员端）")
    public Result<IPage<Volunteer>> getVolunteerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Volunteer> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
        wrapper.ne("status", "已结束").or().isNull("status");
        wrapper.orderByDesc("create_time");
        IPage<Volunteer> result = volunteerService.page(page, wrapper);
        return Result.success(result);
    }

    @PostMapping("/volunteer/signup")
    @Operation(summary = "报名志愿活动")
    public Result<String> signupVolunteer(@RequestBody VolunteerSignup signup) {
        // 获取志愿活动信息
        Volunteer volunteer = volunteerService.getById(signup.getVolunteerId());
        if (volunteer == null) {
            return Result.error("志愿活动不存在");
        }
        
        // 检查活动是否已结束
        if ("已结束".equals(volunteer.getStatus())) {
            return Result.error("活动已结束");
        }
        
        // 检查是否已报名
        QueryWrapper<VolunteerSignup> wrapper = new QueryWrapper<>();
        wrapper.eq("volunteer_id", signup.getVolunteerId()).eq("member_id", signup.getMemberId());
        long count = volunteerSignupService.count(wrapper);
        if (count > 0) {
            return Result.error("您已报名该志愿活动");
        }
        
        // 动态统计当前报名人数
        QueryWrapper<VolunteerSignup> countWrapper = new QueryWrapper<>();
        countWrapper.eq("volunteer_id", signup.getVolunteerId());
        long currentCount = volunteerSignupService.count(countWrapper);
        
        // 检查活动是否已满
        if (currentCount >= volunteer.getMaxParticipants()) {
            return Result.error("活动已满");
        }
        
        // 查询党员姓名
        PartyMember member = partyMemberService.getById(signup.getMemberId());
        if (member != null) {
            signup.setMemberName(member.getName());
        }
        signup.setSignupTime(LocalDateTime.now());
        signup.setStatus("已报名");
        volunteerSignupService.save(signup);
        
        // 更新活动报名人数（使用动态统计的结果）
        volunteer.setCurrentParticipants((int) (currentCount + 1));
        volunteerService.updateById(volunteer);
        
        return Result.success("报名成功");
    }

    @GetMapping("/volunteer/my/{memberId}")
    @Operation(summary = "获取我报名的志愿活动")
    public Result<List<Map<String, Object>>> getMyVolunteers(@PathVariable Long memberId) {
        QueryWrapper<VolunteerSignup> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId).orderByDesc("signup_time");
        List<VolunteerSignup> signups = volunteerSignupService.list(wrapper);
        
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (VolunteerSignup signup : signups) {
            Volunteer volunteer = volunteerService.getById(signup.getVolunteerId());
            if (volunteer == null) {
                continue;
            }
            
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", signup.getId());
            item.put("volunteerId", signup.getVolunteerId());
            item.put("memberId", signup.getMemberId());
            item.put("memberName", signup.getMemberName());
            item.put("status", signup.getStatus());
            item.put("serviceHoursActual", signup.getServiceHoursActual());
            item.put("signupTime", signup.getSignupTime());
            item.put("volunteerTitle", volunteer.getTitle());
            item.put("location", volunteer.getLocation());
            item.put("serviceDate", volunteer.getServiceDate());
            item.put("serviceHours", volunteer.getServiceHours());
            
            result.add(item);
        }
        
        return Result.success(result);
    }

    @GetMapping("/volunteer/total-hours")
    @Operation(summary = "获取本人累计志愿服务时长")
    public Result<Integer> getMyVolunteerTotalHours(
            @RequestAttribute(value = "authenticatedUserId", required = false) Long memberId) {
        if (memberId == null) {
            return Result.error("请先登录");
        }
        return Result.success(volunteerService.getTotalHours(memberId));
    }

    @GetMapping("/development/{memberId}")
    @Operation(summary = "获取党员发展流程")
    public Result<Map<String, Object>> getDevelopmentProcess(@PathVariable Long memberId) {
        Map<String, Object> data = new HashMap<>();
        PartyMember member = partyMemberService.getById(memberId);
        if (member == null) {
            data.put("currentStep", 0);
            data.put("steps", getStepList());
            data.put("application", null);
            data.put("approvalRecords", List.of());
            return Result.success(data);
        }

        // 根据党员状态确定当前步骤
        String partyStatus = member.getPartyStatus();
        int currentStep = getCurrentStep(partyStatus);

        // 查找入党申请记录（通过姓名匹配）
        QueryWrapper<Application> appWrapper = new QueryWrapper<>();
        appWrapper.eq("name", member.getName()).orderByDesc("create_time").last("LIMIT 1");
        Application application = applicationService.getOne(appWrapper, false);

        // 查找审批记录
        List<ApprovalRecord> approvalRecords = List.of();
        if (application != null) {
            QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
            approvalWrapper.eq("application_id", application.getId()).orderByAsc("id");
            approvalRecords = approvalService.list(approvalWrapper);
        }

        data.put("currentStep", currentStep);
        data.put("steps", getStepList());
        data.put("application", application);
        data.put("approvalRecords", approvalRecords);
        data.put("member", member);
        return Result.success(data);
    }

    @GetMapping("/development/application/{name}")
    @Operation(summary = "通过姓名获取入党申请和发展流程")
    public Result<Map<String, Object>> getDevelopmentByName(@PathVariable String name) {
        Map<String, Object> data = new HashMap<>();

        // 查找入党申请记录
        QueryWrapper<Application> appWrapper = new QueryWrapper<>();
        appWrapper.eq("name", name).orderByDesc("create_time").last("LIMIT 1");
        Application application = applicationService.getOne(appWrapper, false);

        // 根据申请状态确定当前步骤
        int currentStep = 0;
        if (application != null) {
            currentStep = getApplicationStep(application.getStatus());
        } else {
            // 没有申请记录，查询party_member表根据党员状态确定步骤
            QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
            memberWrapper.eq("name", name).last("LIMIT 1");
            PartyMember member = partyMemberService.getOne(memberWrapper, false);
            if (member != null) {
                currentStep = getCurrentStep(member.getPartyStatus());
            }
        }

        // 查找审批记录
        List<ApprovalRecord> approvalRecords = List.of();
        if (application != null) {
            QueryWrapper<ApprovalRecord> approvalWrapper = new QueryWrapper<>();
            approvalWrapper.eq("application_id", application.getId()).orderByAsc("id");
            approvalRecords = approvalService.list(approvalWrapper);
        }

        data.put("currentStep", currentStep);
        data.put("steps", getStepList());
        data.put("application", application);
        data.put("approvalRecords", approvalRecords);
        return Result.success(data);
    }

    /**
     * 党员发展步骤列表
     */
    private List<Map<String, Object>> getStepList() {
        return List.of(
            Map.of("step", 0, "title", "尚未申请", "description", "尚未提交入党申请书", "statusKey", "NONE"),
            Map.of("step", 1, "title", "提交入党申请", "description", "向党组织提交书面入党申请书", "statusKey", "APPLYING"),
            Map.of("step", 2, "title", "确定为积极分子", "description", "经党组织研究确定为入党积极分子", "statusKey", "ACTIVIST"),
            Map.of("step", 3, "title", "确定为发展对象", "description", "经过一年以上培养考察，确定为发展对象", "statusKey", "DEVELOP_TARGET"),
            Map.of("step", 4, "title", "接收为预备党员", "description", "经支部大会讨论通过，接收为预备党员", "statusKey", "PROBATIONARY"),
            Map.of("step", 5, "title", "转为正式党员", "description", "预备期满，经支部大会讨论转为正式党员", "statusKey", "FORMAL")
        );
    }

    /**
     * 根据党员的partyStatus确定当前步骤
     */
    private int getCurrentStep(String partyStatus) {
        if (partyStatus == null) return 0;
        return switch (partyStatus) {
            case "APPLICANT" -> 1;       // 已提交申请
            case "ACTIVIST" -> 2;        // 积极分子
            case "DEVELOP_TARGET" -> 3;  // 发展对象
            case "PROBATIONARY" -> 4;    // 预备党员
            case "FORMAL" -> 6;          // 正式党员，所有步骤完成
            default -> 0;
        };
    }

    /**
     * 根据申请状态确定当前步骤
     */
    private int getApplicationStep(String status) {
        if (status == null) return 0;
        return switch (status) {
            case "NONE" -> 1;       // 已提交申请，等待审核
            case "APPLYING" -> 2;   // 申请已通过，确定为积极分子阶段
            case "ACTIVIST" -> 3;   // 积极分子，等待确定为发展对象
            case "DEVELOP_TARGET" -> 4; // 发展对象，等待接收为预备党员
            case "PROBATIONARY" -> 5;   // 预备党员，等待转正
            case "FORMAL" -> 6;     // 已转正，所有步骤完成
            default -> 0;
        };
    }

    @GetMapping("/application")
    @Operation(summary = "根据姓名查询入党申请信息")
    public Result<Application> getApplicationByName(@RequestParam String name) {
        QueryWrapper<Application> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name).orderByDesc("create_time").last("LIMIT 1");
        Application app = applicationService.getOne(wrapper, false);
        if (app == null) {
            return Result.error("未找到申请记录");
        }
        return Result.success(app);
    }

    @GetMapping("/fee/notifications/{memberId}")
    @Operation(summary = "获取缴费通知列表（党员端）")
    public Result<List<Map<String, Object>>> getFeeNotifications(@PathVariable Long memberId) {
        PartyMember member = partyMemberService.getById(memberId);
        if (member == null) {
            return Result.success(new java.util.ArrayList<>());
        }

        // 先获取该党员的所有缴费记录，确定哪些通知对该党员有效
        QueryWrapper<PartyFee> feeWrapper = new QueryWrapper<>();
        feeWrapper.eq("member_id", memberId);
        List<PartyFee> fees = partyFeeService.list(feeWrapper);

        // 构建有效的年/月集合
        java.util.Set<String> validYearMonthSet = new java.util.HashSet<>();
        for (PartyFee fee : fees) {
            validYearMonthSet.add(fee.getYear() + "-" + fee.getMonth());
        }

        // 获取所有已发布的通知
        QueryWrapper<FeeNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "已发布").orderByDesc("publish_time");
        List<FeeNotification> notifications = feeNotificationService.list(wrapper);

        // 只返回该党员有缴费记录的通知
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (FeeNotification notification : notifications) {
            String yearMonth = notification.getYear() + "-" + notification.getMonth();
            if (validYearMonthSet.contains(yearMonth)) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", notification.getId());
                item.put("title", notification.getTitle());
                item.put("content", notification.getContent());
                item.put("year", notification.getYear());
                item.put("month", notification.getMonth());
                item.put("publishTime", notification.getPublishTime());
                result.add(item);
            }
        }
        return Result.success(result);
    }

    @GetMapping("/fee/notification/{notificationId}/status")
    @Operation(summary = "获取个人某通知的缴费状态")
    public Result<Map<String, Object>> getMemberFeeStatus(@PathVariable Long notificationId, @RequestParam Long memberId) {
        FeeNotification notification = feeNotificationService.getById(notificationId);
        if (notification == null) {
            return Result.error("通知不存在");
        }

        QueryWrapper<PartyFee> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId)
               .eq("year", notification.getYear())
               .eq("month", notification.getMonth());
        PartyFee fee = partyFeeService.getOne(wrapper, false);

        Map<String, Object> result = new HashMap<>();
        result.put("notification", notification);
        if (fee != null) {
            result.put("feeId", fee.getId());
            result.put("amount", fee.getAmount());
            result.put("status", fee.getStatus());
            result.put("payTime", fee.getPayTime());
        } else {
            result.put("feeId", null);
            result.put("amount", BigDecimal.ZERO);
            result.put("status", "待缴费");
            result.put("payTime", null);
        }
        return Result.success(result);
    }

    @PutMapping("/fee/pay/{feeId}")
    @Operation(summary = "党员缴费")
    public Result<PartyFee> payFee(@PathVariable Long feeId, @RequestBody PartyFee fee) {
        PartyFee existingFee = partyFeeService.getById(feeId);
        if (existingFee == null) {
            return Result.error("缴费记录不存在");
        }
        if ("已缴费".equals(existingFee.getStatus())) {
            return Result.error("该月份已缴费，请勿重复缴费");
        }

        existingFee.setAmount(fee.getAmount());
        existingFee.setStatus("已缴费");
        existingFee.setPayTime(LocalDateTime.now());
        partyFeeService.updateById(existingFee);
        return Result.success(existingFee);
    }

    @GetMapping("/fee/summary/{memberId}")
    @Operation(summary = "获取党员缴费汇总")
    public Result<Map<String, Object>> getFeeSummary(@PathVariable Long memberId) {
        Map<String, Object> result = new HashMap<>();

        PartyMember member = partyMemberService.getById(memberId);
        if (member == null) {
            result.put("totalAmount", BigDecimal.ZERO);
            result.put("paidCount", 0);
            result.put("unpaidCount", 0);
            result.put("allFees", new java.util.ArrayList<>());
            return Result.success(result);
        }

        QueryWrapper<PartyFee> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        List<PartyFee> allFees = partyFeeService.list(wrapper);

        BigDecimal totalAmount = allFees.stream()
                .filter(f -> f.getAmount() != null && "已缴费".equals(f.getStatus()))
                .map(PartyFee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long paidCount = allFees.stream().filter(f -> "已缴费".equals(f.getStatus())).count();
        long unpaidCount = allFees.stream().filter(f -> "待缴费".equals(f.getStatus())).count();

        result.put("totalAmount", totalAmount);
        result.put("paidCount", paidCount);
        result.put("unpaidCount", unpaidCount);
        result.put("allFees", allFees);

        return Result.success(result);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码（党员端）")
    public Result<String> changePassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (username == null || oldPassword == null || newPassword == null) {
            return Result.error("参数不能为空");
        }

        // 先尝试从 party_member 表查找（正式党员和预备党员）
        QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("name", username);
        PartyMember member = partyMemberService.getOne(memberWrapper, false);

        if (member != null) {
            // 验证旧密码
            if (!oldPassword.equals(member.getPassword())) {
                return Result.error("原密码不正确");
            }
            // 更新新密码
            member.setPassword(newPassword);
            member.setUpdateTime(LocalDateTime.now());
            partyMemberService.updateById(member);
            return Result.success("密码修改成功");
        }

        // 如果党员表没有找到，尝试从 application 表查找（申请人）
        QueryWrapper<Application> appWrapper = new QueryWrapper<>();
        appWrapper.eq("name", username);
        Application application = applicationService.getOne(appWrapper, false);

        if (application != null) {
            // 验证旧密码
            if (!oldPassword.equals(application.getPassword())) {
                return Result.error("原密码不正确");
            }
            // 更新新密码
            application.setPassword(newPassword);
            application.setUpdateTime(LocalDateTime.now());
            applicationService.updateById(application);
            return Result.success("密码修改成功");
        }

        return Result.error("用户不存在");
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
