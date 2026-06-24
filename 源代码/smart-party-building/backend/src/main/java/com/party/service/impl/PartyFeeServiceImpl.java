package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.PartyFee;
import com.party.mapper.PartyFeeMapper;
import com.party.service.PartyFeeService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PartyFeeServiceImpl extends ServiceImpl<PartyFeeMapper, PartyFee> implements PartyFeeService {

    @Override
    public BigDecimal sumByMemberAndYear(Long memberId, String year) {
        return baseMapper.sumByMemberAndYear(memberId, year);
    }

    @Override
    public List<PartyFee> findByMemberId(Long memberId) {
        return baseMapper.findByMemberId(memberId);
    }

    @Override
    public BigDecimal sumByYear(String year) {
        return baseMapper.sumByYear(year);
    }

    @Override
    public List<Map<String, Object>> sumGroupByMember(String year) {
        return baseMapper.sumGroupByMember(year);
    }

    @Override
    public BigDecimal sumPaidByYear(String year) {
        return baseMapper.sumPaidByYear(year);
    }

    @Override
    public Long countPaidByYear(String year) {
        return baseMapper.countPaidByYear(year);
    }

    @Override
    public Long countUnpaidByYear(String year) {
        return baseMapper.countUnpaidByYear(year);
    }
}