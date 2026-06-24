package com.party.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.Notification;
import com.party.mapper.NotificationMapper;
import com.party.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public List<Notification> listOrderByPublishTime() {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("publish_time");
        return baseMapper.selectList(wrapper);
    }
}