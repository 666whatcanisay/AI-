package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Notification;
import com.party.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "通知公告管理", description = "通知公告管理接口")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "获取通知列表", description = "分页获取通知公告列表")
    public Result<IPage<Notification>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("title", keyword.trim());
        }
        wrapper.orderByDesc("publish_time");
        IPage<Notification> result = notificationService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有通知", description = "获取所有通知公告（不分页）")
    public Result<List<Notification>> getAll() {
        List<Notification> notifications = notificationService.listOrderByPublishTime();
        return Result.success(notifications);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取通知详情", description = "根据ID获取通知详情")
    public Result<Notification> getById(@PathVariable Long id) {
        Notification notification = notificationService.getById(id);
        return Result.success(notification);
    }

    @PostMapping
    @Operation(summary = "发布通知", description = "发布新的通知公告")
    public Result<Notification> add(@RequestBody Notification notification) {
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        if (notification.getPublishTime() == null) {
            notification.setPublishTime(LocalDateTime.now());
        }
        notificationService.save(notification);
        return Result.success(notification);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新通知", description = "更新通知公告")
    public Result<Notification> update(@PathVariable Long id, @RequestBody Notification notification) {
        notification.setId(id);
        notification.setUpdateTime(LocalDateTime.now());
        notificationService.updateById(notification);
        return Result.success(notification);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "删除通知公告")
    public Result<String> delete(@PathVariable Long id) {
        notificationService.removeById(id);
        return Result.success("删除成功");
    }
}