package com.party.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.party.entity.PartyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PartyMemberMapper extends BaseMapper<PartyMember> {

    @Select("SELECT * FROM party_member WHERE name LIKE CONCAT('%', #{name}, '%') AND branch_name LIKE CONCAT('%', #{branchName}, '%')")
    List<PartyMember> findByNameAndBranch(@Param("name") String name, @Param("branchName") String branchName);

    @Select("SELECT COUNT(*) FROM party_member WHERE branch_name = #{branchName}")
    int countByBranchName(@Param("branchName") String branchName);

    @Update("ALTER TABLE party_member AUTO_INCREMENT = 1")
    void resetAutoIncrement();

    /**
     * 将大于deletedId的所有记录ID减1，实现ID重新排列
     */
    @Update("UPDATE party_member SET id = id - 1 WHERE id > #{deletedId}")
    int reorderIds(@Param("deletedId") Long deletedId);

    @Update("UPDATE party_member SET branch_name = #{newName} WHERE branch_name = #{oldName}")
    void updateBranchNameByOldName(@Param("oldName") String oldName, @Param("newName") String newName);
}