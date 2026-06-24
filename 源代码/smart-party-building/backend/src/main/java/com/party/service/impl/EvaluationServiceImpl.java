package com.party.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Evaluation;
import com.party.mapper.EvaluationMapper;
import com.party.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    @Override
    public List<Map<String, Object>> getByYearMonth(String year, String month) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        if (year != null && !year.isEmpty()) {
            wrapper.eq("year", year);
        }
        if (month != null && !month.isEmpty()) {
            wrapper.eq("month", month);
        }
        wrapper.orderByDesc("evaluate_time");
        return baseMapper.selectMaps(wrapper);
    }

    @Override
    public Map<String, Object> getMemberEvaluation(Long memberId) {
        QueryWrapper<Evaluation> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        wrapper.orderByDesc("year", "month");
        wrapper.last("LIMIT 1");
        Evaluation evaluation = baseMapper.selectOne(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        if (evaluation != null) {
            result.put("year", evaluation.getYear());
            result.put("month", evaluation.getMonth());
            result.put("grade", evaluation.getGrade());
            result.put("comment", evaluation.getComment());
            result.put("evaluateTime", evaluation.getEvaluateTime());
        }
        return result;
    }
}
