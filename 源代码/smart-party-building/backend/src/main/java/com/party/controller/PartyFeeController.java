package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.PartyFee;
import com.party.service.PartyFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
@Tag(name = "党费管理", description = "党费管理接口")
public class PartyFeeController {

    private final PartyFeeService partyFeeService;

    public PartyFeeController(PartyFeeService partyFeeService) {
        this.partyFeeService = partyFeeService;
    }

    @GetMapping
    @Operation(summary = "获取缴费记录", description = "分页获取缴费记录")
    public Result<IPage<PartyFee>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String status) {
        Page<PartyFee> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PartyFee> wrapper = new QueryWrapper<>();
        if (memberId != null) {
            wrapper.eq("member_id", memberId);
        }
        if (memberName != null && !memberName.isEmpty()) {
            wrapper.like("member_name", memberName);
        }
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("pay_time");
        IPage<PartyFee> result = partyFeeService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取缴费记录详情", description = "根据ID获取缴费记录")
    public Result<PartyFee> getById(@PathVariable Long id) {
        PartyFee fee = partyFeeService.getById(id);
        return Result.success(fee);
    }

    @PostMapping
    @Operation(summary = "添加缴费记录", description = "添加新缴费记录")
    public Result<PartyFee> add(@RequestBody PartyFee fee) {
        fee.setStatus("已缴费");
        fee.setPayTime(LocalDateTime.now());
        fee.setCreateTime(LocalDateTime.now());
        partyFeeService.save(fee);
        return Result.success(fee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新缴费记录", description = "更新缴费记录")
    public Result<PartyFee> update(@PathVariable Long id, @RequestBody PartyFee fee) {
        fee.setId(id);
        partyFeeService.updateById(fee);
        return Result.success(fee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除缴费记录", description = "删除缴费记录")
    public Result<String> delete(@PathVariable Long id) {
        partyFeeService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/statistics/{year}")
    @Operation(summary = "年度统计", description = "获取年度缴费统计")
    public Result<Map<String, Object>> getYearStatistics(@PathVariable String year) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal totalAmount = partyFeeService.sumPaidByYear(year);
        Long paidCount = partyFeeService.countPaidByYear(year);
        Long unpaidCount = partyFeeService.countUnpaidByYear(year);
        List<Map<String, Object>> memberStats = partyFeeService.sumGroupByMember(year);
        result.put("year", year);
        result.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        result.put("paidCount", paidCount != null ? paidCount : 0L);
        result.put("unpaidCount", unpaidCount != null ? unpaidCount : 0L);
        result.put("memberStats", memberStats);
        return Result.success(result);
    }

    @GetMapping("/member/{memberId}/year/{year}")
    @Operation(summary = "个人年度缴费", description = "获取个人年度缴费总额")
    public Result<BigDecimal> getMemberYearFee(@PathVariable Long memberId, @PathVariable String year) {
        BigDecimal amount = partyFeeService.sumByMemberAndYear(memberId, year);
        return Result.success(amount != null ? amount : BigDecimal.ZERO);
    }
}