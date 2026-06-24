package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.TransferInApplication;
import com.party.mapper.TransferInApplicationMapper;
import com.party.service.TransferInApplicationService;
import org.springframework.stereotype.Service;

@Service
public class TransferInApplicationServiceImpl extends ServiceImpl<TransferInApplicationMapper, TransferInApplication> implements TransferInApplicationService {
}