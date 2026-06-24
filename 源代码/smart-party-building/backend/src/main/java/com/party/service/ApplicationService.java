package com.party.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.party.entity.Application;

public interface ApplicationService extends IService<Application> {
    void resetAutoIncrement();
}
