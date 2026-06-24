package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.FeeNotification;

import java.util.List;

public interface FeeNotificationService extends IService<FeeNotification> {

    List<FeeNotification> findByYearAndMonth(String year, String month);

    List<FeeNotification> findActiveNotifications();
}
