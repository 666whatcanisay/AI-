package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.PartyBranch;
import com.party.entity.PartyMember;
import com.party.service.PartyBranchService;
import com.party.service.PartyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@Tag(name = "党员管理", description = "党员信息管理接口")
public class PartyMemberController {

    private final PartyMemberService partyMemberService;
    private final PartyBranchService partyBranchService;

    public PartyMemberController(PartyMemberService partyMemberService, PartyBranchService partyBranchService) {
        this.partyMemberService = partyMemberService;
        this.partyBranchService = partyBranchService;
    }

    @GetMapping
    @Operation(summary = "获取党员列表", description = "分页获取党员列表")
    public Result<IPage<PartyMember>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String branchName,
            @RequestParam(required = false) String partyStatus) {
        Page<PartyMember> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartyMember> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (branchName != null && !branchName.isEmpty()) {
            wrapper.like("branch_name", branchName);
        }
        if (partyStatus != null && !partyStatus.isEmpty()) {
            wrapper.eq("party_status", partyStatus);
        }
        wrapper.orderByAsc("id");
        IPage<PartyMember> result = partyMemberService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取党员详情", description = "根据ID获取党员详情")
    public Result<PartyMember> getById(@PathVariable Long id) {
        PartyMember member = partyMemberService.getById(id);
        return Result.success(member);
    }

    @PostMapping
    @Operation(summary = "添加党员", description = "添加新党员")
    public Result<PartyMember> add(@RequestBody PartyMember member) {
        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            member.setPassword("123456");
        }
        member.setCreateTime(LocalDateTime.now());
        member.setUpdateTime(LocalDateTime.now());
        partyMemberService.save(member);
        
        if (member.getBranchName() != null && !member.getBranchName().isEmpty()) {
            QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
            wrapper.eq("name", member.getBranchName());
            PartyBranch branch = partyBranchService.getOne(wrapper);
            if (branch != null) {
                branch.setMemberCount(branch.getMemberCount() + 1);
                branch.setUpdateTime(LocalDateTime.now());
                partyBranchService.updateById(branch);
            }
        }
        
        return Result.success(member);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新党员", description = "更新党员信息")
    public Result<PartyMember> update(@PathVariable Long id, @RequestBody PartyMember member) {
        PartyMember oldMember = partyMemberService.getById(id);
        String oldBranchName = oldMember != null ? oldMember.getBranchName() : null;
        
        member.setId(id);
        member.setUpdateTime(LocalDateTime.now());
        // 如果密码为空，则不更新密码
        if (member.getPassword() == null || member.getPassword().isEmpty()) {
            member.setPassword(oldMember != null ? oldMember.getPassword() : null);
        }
        partyMemberService.updateById(member);
        
        if (oldBranchName != null && !oldBranchName.isEmpty() && 
            (member.getBranchName() == null || !member.getBranchName().equals(oldBranchName))) {
            QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
            wrapper.eq("name", oldBranchName);
            PartyBranch oldBranch = partyBranchService.getOne(wrapper);
            if (oldBranch != null) {
                oldBranch.setMemberCount(Math.max(0, oldBranch.getMemberCount() - 1));
                oldBranch.setUpdateTime(LocalDateTime.now());
                partyBranchService.updateById(oldBranch);
            }
        }
        
        if (member.getBranchName() != null && !member.getBranchName().isEmpty() && 
            (oldBranchName == null || !member.getBranchName().equals(oldBranchName))) {
            QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
            wrapper.eq("name", member.getBranchName());
            PartyBranch newBranch = partyBranchService.getOne(wrapper);
            if (newBranch != null) {
                newBranch.setMemberCount(newBranch.getMemberCount() + 1);
                newBranch.setUpdateTime(LocalDateTime.now());
                partyBranchService.updateById(newBranch);
            }
        }
        
        return Result.success(member);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除党员", description = "删除党员")
    public Result<String> delete(@PathVariable Long id) {
        PartyMember member = partyMemberService.getById(id);
        if (member != null && member.getBranchName() != null && !member.getBranchName().isEmpty()) {
            QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
            wrapper.eq("name", member.getBranchName());
            PartyBranch branch = partyBranchService.getOne(wrapper);
            if (branch != null) {
                branch.setMemberCount(Math.max(0, branch.getMemberCount() - 1));
                branch.setUpdateTime(LocalDateTime.now());
                partyBranchService.updateById(branch);
            }
        }
        
        partyMemberService.removeById(id);
        // 重新排序ID，删除后ID连续不断开
        partyMemberService.reorderAfterDelete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/export")
    @Operation(summary = "导出党员列表", description = "导出所有党员数据")
    public Result<List<PartyMember>> export() {
        List<PartyMember> list = partyMemberService.list();
        return Result.success(list);
    }
}