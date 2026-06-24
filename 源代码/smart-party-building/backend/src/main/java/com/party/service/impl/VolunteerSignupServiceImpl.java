package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.VolunteerSignup;
import com.party.mapper.VolunteerSignupMapper;
import com.party.service.VolunteerSignupService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerSignupServiceImpl extends ServiceImpl<VolunteerSignupMapper, VolunteerSignup> implements VolunteerSignupService {
}
