package com.party.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.party.common.Result;
import com.party.entity.Evaluation;
import com.party.entity.PartyMember;
import com.party.service.EvaluationService;
import com.party.service.PartyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/evaluation")
@Tag(name = "评议考核管理", description = "评议考核管理接口")
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final PartyMemberService partyMemberService;

    public EvaluationController(EvaluationService evaluationService, PartyMemberService partyMemberService) {
        this.evaluationService = evaluationService;
        this.partyMemberService = partyMemberService;
    }

    @GetMapping
    @Operation(summary = "获取考核列表", description = "分页获取评议考核列表")
    public Result<IPage<Evaluation>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String month) {
        Page<Evaluation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        if (month != null && !month.isEmpty()) {
            wrapper.eq("month", month);
        }
        wrapper.orderByDesc("year", "month", "evaluate_time");
        IPage<Evaluation> result = evaluationService.page(page, wrapper);
        return Result.success(result);
    }

    @GetMapping("/by-month")
    @Operation(summary = "按月份获取考核列表", description = "获取某月份所有正式党员的考核情况")
    public Result<List<Map<String, Object>>> getByYearMonth(
            @RequestParam String year,
            @RequestParam String month) {
        List<Map<String, Object>> evaluations = evaluationService.getByYearMonth(year, month);
        return Result.success(evaluations);
    }

    @GetMapping("/unevaluated")
    @Operation(summary = "获取未考核党员", description = "获取某月份未考核的正式党员列表")
    public Result<List<PartyMember>> getUnevaluatedMembers(
            @RequestParam String year,
            @RequestParam String month) {
        List<Map<String, Object>> evaluations = evaluationService.getByYearMonth(year, month);
        List<Long> evaluatedMemberIds = evaluations.stream()
                .filter(m -> m.get("member_id") != null)
                .map(m -> ((Number) m.get("member_id")).longValue())
                .collect(Collectors.toList());

        QueryWrapper<PartyMember> wrapper = new QueryWrapper<>();
        wrapper.eq("party_status", "FORMAL").or().isNull("party_status");
        if (!evaluatedMemberIds.isEmpty()) {
            wrapper.notIn("id", evaluatedMemberIds);
        }
        List<PartyMember> members = partyMemberService.list(wrapper);
        return Result.success(members);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "获取党员考核情况", description = "获取某个党员的考核状态")
    public Result<Map<String, Object>> getMemberEvaluation(@PathVariable Long memberId) {
        Map<String, Object> evaluation = evaluationService.getMemberEvaluation(memberId);
        return Result.success(evaluation);
    }

    @PostMapping
    @Operation(summary = "创建考核", description = "为党员创建考核记录")
    public Result<Evaluation> add(@RequestBody Evaluation evaluation) {
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        evaluation.setEvaluateTime(LocalDateTime.now());
        evaluationService.save(evaluation);
        return Result.success(evaluation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新考核", description = "更新考核记录")
    public Result<Evaluation> update(@PathVariable Long id, @RequestBody Evaluation evaluation) {
        evaluation.setId(id);
        evaluation.setUpdateTime(LocalDateTime.now());
        evaluationService.updateById(evaluation);
        return Result.success(evaluation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除考核", description = "删除考核记录")
    public Result<String> delete(@PathVariable Long id) {
        evaluationService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/stats")
    @Operation(summary = "获取考核统计", description = "获取各等级考核统计")
    public Result<Map<String, Object>> getStats(
            @RequestParam String year,
            @RequestParam String month) {
        List<Map<String, Object>> evaluations = evaluationService.getByYearMonth(year, month);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", evaluations.size());
        
        long excellentCount = evaluations.stream().filter(e -> "优秀".equals(e.get("grade"))).count();
        long goodCount = evaluations.stream().filter(e -> "良好".equals(e.get("grade"))).count();
        long qualifiedCount = evaluations.stream().filter(e -> "合格".equals(e.get("grade"))).count();
        long unqualifiedCount = evaluations.stream().filter(e -> "不合格".equals(e.get("grade"))).count();
        
        stats.put("excellentCount", excellentCount);
        stats.put("goodCount", goodCount);
        stats.put("qualifiedCount", qualifiedCount);
        stats.put("unqualifiedCount", unqualifiedCount);
        
        return Result.success(stats);
    }
}
