USE example_db;

-- 创建汇报任务表
CREATE TABLE IF NOT EXISTS report_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    content TEXT COMMENT '任务说明',
    year VARCHAR(4) NOT NULL COMMENT '年份',
    quarter VARCHAR(2) COMMENT '季度',
    month VARCHAR(2) COMMENT '月份',
    deadline VARCHAR(20) COMMENT '截止日期',
    status VARCHAR(20) NOT NULL DEFAULT '进行中' COMMENT '状态：进行中/已结束',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_year_quarter (year, quarter),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='汇报任务表';

-- 更新汇报表，添加任务ID字段
ALTER TABLE report ADD COLUMN IF NOT EXISTS task_id BIGINT COMMENT '关联任务ID' AFTER id;
ALTER TABLE report ADD COLUMN IF NOT EXISTS member_id BIGINT COMMENT '党员ID' AFTER task_id;
ALTER TABLE report ADD COLUMN IF NOT EXISTS member_name VARCHAR(50) COMMENT '党员姓名' AFTER member_id;
ALTER TABLE report ADD INDEX IF NOT EXISTS idx_task_id (task_id);
ALTER TABLE report ADD INDEX IF NOT EXISTS idx_member_id (member_id);
