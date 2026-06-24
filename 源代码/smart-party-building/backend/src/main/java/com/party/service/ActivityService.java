package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.Activity;
import com.party.entity.ActivitySignup;

import java.util.List;

public interface ActivityService extends IService<Activity> {

    boolean signup(Long activityId, Long memberId, String memberName);

    boolean checkin(Long activityId, Long memberId);

    List<ActivitySignup> getSignups(Long activityId);

    List<ActivitySignup> getMemberSignups(Long memberId);

    Integer getSignupCount(Long activityId);

    void resetAutoIncrement();

    void refreshSignupCount(Long activityId);

    void refreshAllSignupCounts();
}