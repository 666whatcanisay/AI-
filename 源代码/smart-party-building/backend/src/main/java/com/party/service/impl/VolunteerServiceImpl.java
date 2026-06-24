package com.party.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Volunteer;
import com.party.entity.VolunteerSignup;
import com.party.mapper.VolunteerMapper;
import com.party.mapper.VolunteerSignupMapper;
import com.party.service.VolunteerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VolunteerServiceImpl extends ServiceImpl<VolunteerMapper, Volunteer> implements VolunteerService {

    private final VolunteerSignupMapper volunteerSignupMapper;

    public VolunteerServiceImpl(VolunteerSignupMapper volunteerSignupMapper) {
        this.volunteerSignupMapper = volunteerSignupMapper;
    }

    @Override
    @Transactional
    public boolean signup(Long volunteerId, Long memberId, String memberName) {
        Volunteer volunteer = getById(volunteerId);
        if (volunteer == null) {
            throw new RuntimeException("志愿活动不存在");
        }
        if ("已结束".equals(volunteer.getStatus())) {
            throw new RuntimeException("活动已结束");
        }
        VolunteerSignup existing = volunteerSignupMapper.findByVolunteerAndMember(volunteerId, memberId);
        if (existing != null) {
            throw new RuntimeException("已报名该活动");
        }
        int currentCount = volunteerSignupMapper.countByVolunteerId(volunteerId);
        if (currentCount >= volunteer.getMaxParticipants()) {
            throw new RuntimeException("活动已满");
        }
        VolunteerSignup signup = new VolunteerSignup();
        signup.setVolunteerId(volunteerId);
        signup.setMemberId(memberId);
        signup.setMemberName(memberName);
        signup.setStatus("已报名");
        signup.setSignupTime(LocalDateTime.now());
        volunteerSignupMapper.insert(signup);
        volunteer.setCurrentParticipants(currentCount + 1);
        updateById(volunteer);
        return true;
    }

    @Override
    public List<VolunteerSignup> getSignups(Long volunteerId) {
        return volunteerSignupMapper.findByVolunteerId(volunteerId);
    }

    @Override
    @Transactional
    public boolean finishActivity(Long volunteerId) {
        Volunteer volunteer = getById(volunteerId);
        if (volunteer == null) {
            throw new RuntimeException("志愿活动不存在");
        }
        volunteer.setStatus("已结束");
        volunteer.setUpdateTime(LocalDateTime.now());
        updateById(volunteer);
        List<VolunteerSignup> signups = volunteerSignupMapper.findByVolunteerId(volunteerId);
        for (VolunteerSignup signup : signups) {
            signup.setStatus("已完成");
            signup.setServiceHoursActual(volunteer.getServiceHours());
            volunteerSignupMapper.updateById(signup);
        }
        return true;
    }

    @Override
    public Integer getTotalHours(Long memberId) {
        return volunteerSignupMapper.getTotalHours(memberId);
    }

    @Override
    @Transactional
    public boolean deleteWithSignups(Long volunteerId) {
        QueryWrapper<VolunteerSignup> signupWrapper = new QueryWrapper<>();
        signupWrapper.eq("volunteer_id", volunteerId);
        volunteerSignupMapper.delete(signupWrapper);
        
        return removeById(volunteerId);
    }

    public void refreshAllSignupCounts() {
        QueryWrapper<Volunteer> wrapper = new QueryWrapper<>();
        List<Volunteer> volunteers = list(wrapper);
        for (Volunteer volunteer : volunteers) {
            int signupCount = volunteerSignupMapper.countByVolunteerId(volunteer.getId());
            volunteer.setCurrentParticipants(signupCount);
            updateById(volunteer);
        }
    }
}
