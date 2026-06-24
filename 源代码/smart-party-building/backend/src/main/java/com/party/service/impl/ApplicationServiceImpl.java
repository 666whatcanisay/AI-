package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Application;
import com.party.mapper.ApplicationMapper;
import com.party.service.ApplicationService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Override
    public void resetAutoIncrement() {
        baseMapper.resetAutoIncrement();
    }
}
