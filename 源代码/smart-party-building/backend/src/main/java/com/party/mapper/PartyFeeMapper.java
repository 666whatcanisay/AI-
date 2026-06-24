package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.PartyFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PartyFeeMapper extends BaseMapper<PartyFee> {

    @Select("SELECT SUM(amount) FROM party_fee WHERE member_id = #{memberId} AND year = #{year}")
    BigDecimal sumByMemberAndYear(@Param("memberId") Long memberId, @Param("year") String year);

    @Select("SELECT * FROM party_fee WHERE member_id = #{memberId} ORDER BY year DESC, month DESC")
    List<PartyFee> findByMemberId(@Param("memberId") Long memberId);

    @Select("SELECT SUM(amount) FROM party_fee WHERE year = #{year}")
    BigDecimal sumByYear(@Param("year") String year);

    @Select("SELECT SUM(amount) FROM party_fee WHERE year = #{year} AND status = '已缴费'")
    BigDecimal sumPaidByYear(@Param("year") String year);

    @Select("SELECT COUNT(*) FROM party_fee WHERE year = #{year} AND status = '已缴费'")
    Long countPaidByYear(@Param("year") String year);

    @Select("SELECT COUNT(*) FROM party_fee WHERE year = #{year} AND status = '待缴费'")
    Long countUnpaidByYear(@Param("year") String year);

    @Select("SELECT member_id, member_name, SUM(amount) as total FROM party_fee WHERE year = #{year} GROUP BY member_id, member_name")
    List<java.util.Map<String, Object>> sumGroupByMember(@Param("year") String year);
}