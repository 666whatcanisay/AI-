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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/portal/transfer")
@Tag(name = "党员端-组织关系转移", description = "党员端组织关系转移接口")
public class TransferPortalController {

    private final TransferApplicationService transferApplicationService;
    private final PartyMemberService partyMemberService;
    private final PartyBranchService partyBranchService;

    public TransferPortalController(TransferApplicationService transferApplicationService,
                                   PartyMemberService partyMemberService,
                                   PartyBranchService partyBranchService) {
        this.transferApplicationService = transferApplicationService;
        this.partyMemberService = partyMemberService;
        this.partyBranchService = partyBranchService;
    }

    @PostMapping
    @Operation(summary = "发起组织关系转移", description = "党员发起组织关系转移申请")
    public Result<String> apply(@RequestBody TransferApplication application,
                               @RequestAttribute(value = "authenticatedUserId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }

        PartyMember member = partyMemberService.getById(userId);
        if (member == null) {
            return Result.error("党员信息不存在");
        }

        if (!"FORMAL".equals(member.getPartyStatus())) {
            return Result.error("只有正式党员才能发起组织关系转移");
        }

        QueryWrapper<TransferApplication> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("member_id", userId).eq("status", "待审核");
        long pendingCount = transferApplicationService.count(pendingWrapper);
        if (pendingCount > 0) {
            return Result.error("您有待审核的转移申请，请等待处理");
        }

        application.setMemberId(userId);
        application.setMemberName(member.getName());
        application.setCurrentBranch(member.getBranchName());
        application.setStatus("待审核");
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());

        transferApplicationService.save(application);
        return Result.success("申请提交成功");
    }

    @GetMapping("/member-info")
    public Result<PartyMember> getMemberInfo(
            @RequestAttribute(value = "authenticatedUserId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }

        PartyMember member = partyMemberService.getById(userId);
        if (member == null) {
            return Result.error("党员信息不存在");
        }
        return Result.success(member);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的转移申请", description = "获取当前用户的转移申请记录")
    public Result<IPage<TransferApplication>> getMyApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute(value = "authenticatedUserId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("请先登录");
        }

        Page<TransferApplication> page = new Page<>(pageNum, pageSize);
        QueryWrapper<TransferApplication> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", userId).orderByDesc("create_time");
        IPage<TransferApplication> result = transferApplicationService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/branches")
    @Operation(summary = "获取党支部列表", description = "获取可转入的党支部列表")
    public Result<List<PartyBranch>> getBranches() {
        QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "正常").orderByDesc("create_time");
        List<PartyBranch> list = partyBranchService.list(wrapper);
        return Result.success(list);
    }
}
