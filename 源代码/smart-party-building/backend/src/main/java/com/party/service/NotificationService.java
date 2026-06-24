package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<Notification> listOrderByPublishTime();
}