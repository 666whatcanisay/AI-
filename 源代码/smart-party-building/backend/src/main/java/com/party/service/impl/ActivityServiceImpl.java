package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Activity;
import com.party.entity.ActivitySignup;
import com.party.mapper.ActivityMapper;
import com.party.mapper.ActivitySignupMapper;
import com.party.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivitySignupMapper activitySignupMapper;

    public ActivityServiceImpl(ActivitySignupMapper activitySignupMapper) {
        this.activitySignupMapper = activitySignupMapper;
    }

    @Override
    @Transactional
    public boolean signup(Long activityId, Long memberId, String memberName) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if ("已结束".equals(activity.getStatus())) {
            throw new RuntimeException("活动已结束");
        }
        ActivitySignup existing = activitySignupMapper.findByActivityAndMember(activityId, memberId);
        if (existing != null) {
            throw new RuntimeException("已报名该活动");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动已满");
        }
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setMemberId(memberId);
        signup.setMemberName(memberName);
        signup.setStatus("已报名");
        signup.setSignupTime(LocalDateTime.now());
        activitySignupMapper.insert(signup);
        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        updateById(activity);
        return true;
    }

    @Override
    @Transactional
    public boolean checkin(Long activityId, Long memberId) {
        ActivitySignup signup = activitySignupMapper.findByActivityAndMember(activityId, memberId);
        if (signup == null) {
            throw new RuntimeException("未报名该活动");
        }
        signup.setStatus("已签到");
        signup.setCheckinTime(LocalDateTime.now());
        activitySignupMapper.updateById(signup);
        return true;
    }

    @Override
    public List<ActivitySignup> getSignups(Long activityId) {
        return activitySignupMapper.findByActivityId(activityId);
    }

    @Override
    public List<ActivitySignup> getMemberSignups(Long memberId) {
        return activitySignupMapper.findByMemberId(memberId);
    }

    @Override
    public Integer getSignupCount(Long activityId) {
        return activitySignupMapper.countByActivityId(activityId);
    }

    @Override
    public void resetAutoIncrement() {
        baseMapper.resetAutoIncrement();
    }

    @Override
    public void refreshSignupCount(Long activityId) {
        Activity activity = getById(activityId);
        if (activity != null) {
            Integer count = activitySignupMapper.countByActivityId(activityId);
            activity.setCurrentParticipants(count);
            updateById(activity);
        }
    }

    @Override
    public void refreshAllSignupCounts() {
        List<Activity> activities = list();
        for (Activity activity : activities) {
            refreshSignupCount(activity.getId());
        }
    }
}