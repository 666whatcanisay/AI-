package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.ActivitySignup;
import com.party.mapper.ActivitySignupMapper;
import com.party.service.ActivitySignupService;
import org.springframework.stereotype.Service;

@Service
public class ActivitySignupServiceImpl extends ServiceImpl<ActivitySignupMapper, ActivitySignup> implements ActivitySignupService {
}
