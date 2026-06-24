package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.Volunteer;
import com.party.entity.VolunteerSignup;

import java.util.List;

public interface VolunteerService extends IService<Volunteer> {

    boolean signup(Long volunteerId, Long memberId, String memberName);

    List<VolunteerSignup> getSignups(Long volunteerId);

    boolean finishActivity(Long volunteerId);

    Integer getTotalHours(Long memberId);

    boolean deleteWithSignups(Long volunteerId);
}
