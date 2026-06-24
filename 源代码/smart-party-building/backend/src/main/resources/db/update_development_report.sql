
-- 1. 修改application表：新增申请默认状态改为NONE
-- 已有的APPLYING状态记录保持不变

-- 2. 创建发展流程思想报告表
CREATE TABLE IF NOT EXISTS development_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL COMMENT '关联的申请ID',
    report_type VARCHAR(50) NOT NULL COMMENT '报告类型：APPLICATION-申请报告, ACTIVIST-积极分子思想报告, DEVELOP-发展对象思想报告, PROBATIONARY-预备党员思想报告',
    title VARCHAR(200) NOT NULL COMMENT '报告标题',
    content TEXT COMMENT '报告内容',
    author VARCHAR(100) COMMENT '报告作者',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已审核, REJECTED-已驳回',
    review_comment VARCHAR(500) COMMENT '审核意见',
    reviewer VARCHAR(100) COMMENT '审核人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    review_time DATETIME COMMENT '审核时间',
    INDEX idx_application_id (application_id),
    INDEX idx_report_type (report_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发展流程思想报告表';
