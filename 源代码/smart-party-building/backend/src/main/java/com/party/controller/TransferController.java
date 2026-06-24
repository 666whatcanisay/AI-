package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.PartyBranch;
import com.party.entity.PartyMember;
import com.party.entity.TransferApplication;
import com.party.service.PartyBranchService;
import com.party.service.PartyMemberService;
import com.party.service.TransferApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer")
@Tag(name = "管理端-组织关系转移", description = "管理端组织关系转移审批接口")
public class TransferController {

    private final TransferApplicationService transferApplicationService;
    private final PartyMemberService partyMemberService;
    private final PartyBranchService partyBranchService;

    public TransferController(TransferApplicationService transferApplicationService,
                             PartyMemberService partyMemberService,
                             PartyBranchService partyBranchService) {
        this.transferApplicationService = transferApplicationService;
        this.partyMemberService = partyMemberService;
        this.partyBranchService = partyBranchService;
    }

    @GetMapping
    @Operation(summary = "获取转移申请列表", description = "分页获取组织关系转移申请列表")
    public Result<IPage<TransferApplication>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String memberName) {
        Page<TransferApplication> page = new Page<>(pageNum, pageSize);
        QueryWrapper<TransferApplication> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (memberName != null && !memberName.isEmpty()) {
            wrapper.like("member_name", memberName);
        }
        wrapper.orderByDesc("create_time");
        IPage<TransferApplication> result = transferApplicationService.page(page, wrapper);
        return Result.success(result);
    }

    @PutMapping("/{id}/approve")
    @Transactional
    @Operation(summary = "审批通过", description = "同意组织关系转移申请，删除该党员")
    public Result<String> approve(@PathVariable Long id, @RequestBody(required = false) TransferApplication body) {
        TransferApplication application = transferApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请记录不存在");
        }

        if (!"待审核".equals(application.getStatus())) {
            return Result.error("该申请已处理");
        }

        PartyMember member = partyMemberService.getById(application.getMemberId());
        if (member == null) {
            return Result.error("党员信息不存在");
        }

        application.setStatus("已通过");
        application.setReviewTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        if (body != null && body.getReviewComment() != null) {
            application.setReviewComment(body.getReviewComment());
        }

        if (!partyMemberService.removeById(member.getId())) {
            return Result.error("党员移出失败，请重试");
        }
        partyBranchService.syncMemberCounts();

        transferApplicationService.updateById(application);

        return Result.success("审批通过，党员已删除");
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审批拒绝", description = "拒绝组织关系转移申请")
    public Result<String> reject(@PathVariable Long id, @RequestBody TransferApplication body) {
        TransferApplication application = transferApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请记录不存在");
        }

        if (!"待审核".equals(application.getStatus())) {
            return Result.error("该申请已处理");
        }

        application.setStatus("已拒绝");
        application.setReviewTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        if (body != null && body.getReviewComment() != null) {
            application.setReviewComment(body.getReviewComment());
        }

        transferApplicationService.updateById(application);

        return Result.success("已拒绝该申请");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除申请记录", description = "删除转移申请记录")
    public Result<String> delete(@PathVariable Long id) {
        transferApplicationService.removeById(id);
        return Result.success("删除成功");
    }
}
