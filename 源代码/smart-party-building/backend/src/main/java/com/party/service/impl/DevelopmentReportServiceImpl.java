
package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.DevelopmentReport;
import com.party.mapper.DevelopmentReportMapper;
import com.party.service.DevelopmentReportService;
import org.springframework.stereotype.Service;

@Service
public class DevelopmentReportServiceImpl extends ServiceImpl<DevelopmentReportMapper, DevelopmentReport> implements DevelopmentReportService {
}
