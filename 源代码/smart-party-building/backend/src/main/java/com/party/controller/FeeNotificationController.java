package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.FeeNotification;
import com.party.entity.PartyFee;
import com.party.entity.PartyMember;
import com.party.service.FeeNotificationService;
import com.party.service.PartyFeeService;
import com.party.service.PartyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-notification")
@Tag(name = "缴费通知管理", description = "管理端发布缴费通知接口")
public class FeeNotificationController {

    private final FeeNotificationService feeNotificationService;
    private final PartyMemberService partyMemberService;
    private final PartyFeeService partyFeeService;

    public FeeNotificationController(FeeNotificationService feeNotificationService,
                                      PartyMemberService partyMemberService,
                                      PartyFeeService partyFeeService) {
        this.feeNotificationService = feeNotificationService;
        this.partyMemberService = partyMemberService;
        this.partyFeeService = partyFeeService;
    }

    @GetMapping
    @Operation(summary = "获取缴费通知列表", description = "分页获取缴费通知")
    public Result<IPage<FeeNotification>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String status) {
        Page<FeeNotification> page = new Page<>(pageNum, pageSize);
        QueryWrapper<FeeNotification> wrapper = new QueryWrapper<>();
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("publish_time");
        IPage<FeeNotification> result = feeNotificationService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取缴费通知详情", description = "根据ID获取缴费通知")
    public Result<FeeNotification> getById(@PathVariable Long id) {
        FeeNotification notification = feeNotificationService.getById(id);
        return Result.success(notification);
    }

    @PostMapping
    @Operation(summary = "发布缴费通知", description = "发布新的缴费通知并创建待缴费记录")
    @Transactional
    public Result<FeeNotification> add(@RequestBody FeeNotification notification) {
        QueryWrapper<FeeNotification> existWrapper = new QueryWrapper<>();
        existWrapper.eq("year", notification.getYear())
                    .eq("month", notification.getMonth())
                    .eq("status", "已发布");
        long count = feeNotificationService.count(existWrapper);
        if (count > 0) {
            return Result.error("该年月的缴费通知已存在，请勿重复发布");
        }

        notification.setStatus("已发布");
        notification.setPublishTime(LocalDateTime.now());
        notification.setCreateTime(LocalDateTime.now());

        String title = notification.getYear() + "年" + notification.getMonth() + "月党员缴费";
        notification.setTitle(title);

        feeNotificationService.save(notification);

        QueryWrapper<PartyMember> memberWrapper = new QueryWrapper<>();
        memberWrapper.in("party_status", "FORMAL", "PROBATIONARY");
        List<PartyMember> allMembers = partyMemberService.list(memberWrapper);
        List<PartyFee> feeRecords = new ArrayList<>();
        for (PartyMember member : allMembers) {
            PartyFee fee = new PartyFee();
            fee.setMemberId(member.getId());
            fee.setMemberName(member.getName());
            fee.setYear(notification.getYear());
            fee.setMonth(notification.getMonth());
            fee.setAmount(BigDecimal.ZERO);
            fee.setStatus("待缴费");
            fee.setCreateTime(LocalDateTime.now());
            feeRecords.add(fee);
        }
        if (!feeRecords.isEmpty()) {
            partyFeeService.saveBatch(feeRecords);
        }

        return Result.success(notification);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新缴费通知", description = "更新缴费通知")
    public Result<FeeNotification> update(@PathVariable Long id, @RequestBody FeeNotification notification) {
        notification.setId(id);
        feeNotificationService.updateById(notification);
        return Result.success(notification);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除缴费通知", description = "删除缴费通知及对应的缴费记录")
    @Transactional
    public Result<String> delete(@PathVariable Long id) {
        FeeNotification notification = feeNotificationService.getById(id);
        if (notification == null) {
            return Result.error("通知不存在");
        }
        
        feeNotificationService.removeById(id);
        
        QueryWrapper<PartyFee> feeWrapper = new QueryWrapper<>();
        feeWrapper.eq("year", notification.getYear()).eq("month", notification.getMonth());
        partyFeeService.remove(feeWrapper);
        
        return Result.success("删除成功");
    }

    @DeleteMapping("/clean-all")
    @Operation(summary = "清理所有缴费数据", description = "删除所有缴费通知和缴费记录，用于清理测试数据")
    @Transactional
    public Result<String> cleanAll() {
        feeNotificationService.remove(new QueryWrapper<>());
        partyFeeService.remove(new QueryWrapper<>());
        return Result.success("清理成功");
    }

    @GetMapping("/status/{notificationId}")
    @Operation(summary = "获取通知缴费状态", description = "获取某通知下所有党员的缴费状态")
    public Result<Map<String, Object>> getNotificationStatus(@PathVariable Long notificationId) {
        FeeNotification notification = feeNotificationService.getById(notificationId);
        if (notification == null) {
            return Result.error("通知不存在");
        }

        QueryWrapper<PartyFee> wrapper = new QueryWrapper<>();
        wrapper.eq("year", notification.getYear()).eq("month", notification.getMonth());
        List<PartyFee> feeList = partyFeeService.list(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("notification", notification);
        result.put("totalMembers", feeList.size());
        result.put("paidCount", feeList.stream().filter(f -> "已缴费".equals(f.getStatus())).count());
        result.put("unpaidCount", feeList.stream().filter(f -> "待缴费".equals(f.getStatus())).count());
        result.put("totalAmount", feeList.stream()
                .filter(f -> f.getAmount() != null)
                .map(PartyFee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("feeList", feeList);

        return Result.success(result);
    }

    @GetMapping("/active")
    @Operation(summary = "获取已发布的通知", description = "获取所有已发布的缴费通知")
    public Result<List<FeeNotification>> getActiveNotifications() {
        List<FeeNotification> notifications = feeNotificationService.findActiveNotifications();
        return Result.success(notifications);
    }
}
