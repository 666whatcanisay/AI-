package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.PartyBranch;
import com.party.entity.PartyMember;
import com.party.entity.TransferInApplication;
import com.party.service.PartyBranchService;
import com.party.service.PartyMemberService;
import com.party.service.TransferInApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer/in")
@Tag(name = "组织关系转入", description = "组织关系转入申请接口")
public class TransferInController {

    private final TransferInApplicationService transferInApplicationService;
    private final PartyMemberService partyMemberService;
    private final PartyBranchService partyBranchService;

    public TransferInController(TransferInApplicationService transferInApplicationService,
                                PartyMemberService partyMemberService,
                                PartyBranchService partyBranchService) {
        this.transferInApplicationService = transferInApplicationService;
        this.partyMemberService = partyMemberService;
        this.partyBranchService = partyBranchService;
    }

    @PostMapping("/apply")
    @Operation(summary = "提交转入申请", description = "外部党员提交组织关系转入申请")
    public Result<String> apply(@RequestBody TransferInApplication application) {
        application.setStatus("待审核");
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        transferInApplicationService.save(application);
        return Result.success("申请提交成功");
    }

    @GetMapping
    @Operation(summary = "获取转入申请列表", description = "分页获取组织关系转入申请列表")
    public Result<IPage<TransferInApplication>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name) {
        Page<TransferInApplication> page = new Page<>(pageNum, pageSize);
        QueryWrapper<TransferInApplication> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        wrapper.orderByDesc("create_time");
        IPage<TransferInApplication> result = transferInApplicationService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取申请详情", description = "获取转入申请详细信息")
    public Result<TransferInApplication> getDetail(@PathVariable Long id) {
        TransferInApplication application = transferInApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请记录不存在");
        }
        return Result.success(application);
    }

    @PutMapping("/{id}/approve")
    @Transactional
    @Operation(summary = "审批通过", description = "同意组织关系转入申请，将申请人录入党员列表")
    public Result<String> approve(@PathVariable Long id, @RequestBody(required = false) TransferInApplication body) {
        TransferInApplication application = transferInApplicationService.getById(id);
        if (application == null) {
            return Result.error("申请记录不存在");
        }

        if (!"待审核".equals(application.getStatus())) {
            return Result.error("该申请已处理");
        }

        PartyMember member = new PartyMember();
        member.setName(application.getName());
        member.setGender(application.getGender());
        member.setBirthDate(application.getBirthDate());
        member.setIdCard(application.getIdCard());
        member.setStudentNo(application.getStudentNo());
        member.setClassName(application.getClassName());
        member.setPhone(application.getPhone());
        member.setEmail(application.getEmail());
        member.setNativePlace(application.getNativePlace());
        member.setEducation(application.getEducation());
        member.setPoliticalStatus(application.getPoliticalStatus());
        member.setBranchName(application.getBranchName());
        if (application.getJoinPartyTime() != null && !application.getJoinPartyTime().isEmpty()) {
            member.setJoinPartyTime(LocalDateTime.parse(application.getJoinPartyTime() + "T00:00:00"));
        }
        member.setIntroducer(application.getIntroducer());
        member.setPartyStatus("FORMAL");
        member.setPassword("123456");
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());

        partyMemberService.save(member);
        partyBranchService.syncMemberCounts();

        application.setStatus("已通过");
        application.setReviewTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        if (body != null && body.getReviewComment() != null) {
            application.setReviewComment(body.getReviewComment());
        }
        transferInApplicationService.updateById(application);

        return Result.success("审批通过，已录入党员列表");
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "审批拒绝", description = "拒绝组织关系转入申请")
    public Result<String> reject(@PathVariable Long id, @RequestBody TransferInApplication body) {
        TransferInApplication application = transferInApplicationService.getById(id);
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

        transferInApplicationService.updateById(application);

        return Result.success("已拒绝该申请");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除申请记录", description = "删除转入申请记录")
    public Result<String> delete(@PathVariable Long id) {
        transferInApplicationService.removeById(id);
        return Result.success("删除成功");
    }
}