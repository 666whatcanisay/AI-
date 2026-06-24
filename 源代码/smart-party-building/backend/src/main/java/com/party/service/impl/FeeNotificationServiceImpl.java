package com.party.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.FeeNotification;
import com.party.mapper.FeeNotificationMapper;
import com.party.service.FeeNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeeNotificationServiceImpl extends ServiceImpl<FeeNotificationMapper, FeeNotification> implements FeeNotificationService {

    @Override
    public List<FeeNotification> findByYearAndMonth(String year, String month) {
        QueryWrapper<FeeNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("year", year).eq("month", month);
        return list(wrapper);
    }

    @Override
    public List<FeeNotification> findActiveNotifications() {
        QueryWrapper<FeeNotification> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "已发布").orderByDesc("publish_time");
        return list(wrapper);
    }
}
