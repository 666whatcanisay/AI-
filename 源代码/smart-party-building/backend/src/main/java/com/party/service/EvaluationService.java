package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.Evaluation;

import java.util.List;
import java.util.Map;

public interface EvaluationService extends IService<Evaluation> {
    List<Map<String, Object>> getByYearMonth(String year, String month);
    Map<String, Object> getMemberEvaluation(Long memberId);
}
