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
@RequestMapping("/api/branch")
@Tag(name = "党支部管理", description = "党支部管理接口")
public class PartyBranchController {

    private final PartyBranchService partyBranchService;
    private final PartyMemberService partyMemberService;

    public PartyBranchController(PartyBranchService partyBranchService, PartyMemberService partyMemberService) {
        this.partyBranchService = partyBranchService;
        this.partyMemberService = partyMemberService;
    }

    @GetMapping
    @Operation(summary = "获取党支部列表", description = "分页获取党支部列表")
    public Result<IPage<PartyBranch>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Page<PartyBranch> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        wrapper.orderByDesc("create_time");
        IPage<PartyBranch> result = partyBranchService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有党支部", description = "获取所有党支部用于下拉框")
    public Result<List<PartyBranch>> getAll() {
        QueryWrapper<PartyBranch> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<PartyBranch> list = partyBranchService.list(wrapper);
        return Result.success(list);
    }

    @PostMapping("/sync")
    @Operation(summary = "同步党员人数", description = "根据党员管理数据同步各党支部的党员人数")
    public Result<String> syncMemberCount() {
        partyBranchService.syncMemberCounts();
        return Result.success("同步成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取党支部详情", description = "根据ID获取党支部详情")
    public Result<PartyBranch> getById(@PathVariable Long id) {
        PartyBranch branch = partyBranchService.getById(id);
        return Result.success(branch);
    }

    @PostMapping
    @Operation(summary = "创建党支部", description = "创建新党支部")
    public Result<PartyBranch> add(@RequestBody PartyBranch branch) {
        branch.setStatus("正常");
        branch.setMemberCount(0);
        branch.setCreateTime(LocalDateTime.now());
        branch.setUpdateTime(LocalDateTime.now());
        partyBranchService.save(branch);
        return Result.success(branch);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新党支部", description = "更新党支部信息")
    public Result<PartyBranch> update(@PathVariable Long id, @RequestBody PartyBranch branch) {
        PartyBranch existingBranch = partyBranchService.getById(id);
        if (existingBranch == null) {
            return Result.error("党支部不存在");
        }
        String oldName = existingBranch.getName();
        branch.setId(id);
        branch.setUpdateTime(LocalDateTime.now());
        partyBranchService.updateById(branch);
        if (!oldName.equals(branch.getName())) {
            partyMemberService.updateBranchNameByOldName(oldName, branch.getName());
        }
        return Result.success(branch);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除党支部", description = "删除党支部")
    public Result<String> delete(@PathVariable Long id) {
        partyBranchService.removeById(id);
        return Result.success("删除成功");
    }
}