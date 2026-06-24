package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.ReportTask;
import com.party.mapper.ReportTaskMapper;
import com.party.service.ReportTaskService;
import org.springframework.stereotype.Service;

@Service
public class ReportTaskServiceImpl extends ServiceImpl<ReportTaskMapper, ReportTask> implements ReportTaskService {
}
