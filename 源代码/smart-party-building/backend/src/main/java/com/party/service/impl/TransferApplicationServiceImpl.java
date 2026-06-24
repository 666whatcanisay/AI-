package com.party.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.party.entity.TransferApplication;
import com.party.mapper.TransferApplicationMapper;
import com.party.service.TransferApplicationService;
import org.springframework.stereotype.Service;

@Service
public class TransferApplicationServiceImpl extends ServiceImpl<TransferApplicationMapper, TransferApplication> implements TransferApplicationService {
}
