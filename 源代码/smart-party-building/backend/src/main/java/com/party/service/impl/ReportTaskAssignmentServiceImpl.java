package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.ReportTaskAssignment;
import com.party.mapper.ReportTaskAssignmentMapper;
import com.party.service.ReportTaskAssignmentService;
import org.springframework.stereotype.Service;

@Service
public class ReportTaskAssignmentServiceImpl
        extends ServiceImpl<ReportTaskAssignmentMapper, ReportTaskAssignment>
        implements ReportTaskAssignmentService {
}
