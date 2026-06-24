package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.PartyFee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PartyFeeService extends IService<PartyFee> {

    BigDecimal sumByMemberAndYear(Long memberId, String year);

    List<PartyFee> findByMemberId(Long memberId);

    BigDecimal sumByYear(String year);

    List<Map<String, Object>> sumGroupByMember(String year);

    BigDecimal sumPaidByYear(String year);

    Long countPaidByYear(String year);

    Long countUnpaidByYear(String year);
}