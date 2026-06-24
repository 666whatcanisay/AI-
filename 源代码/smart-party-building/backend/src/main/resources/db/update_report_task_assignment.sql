CREATE TABLE IF NOT EXISTS report_task_assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    member_name VARCHAR(100) NOT NULL,
    assigned_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_task_member (task_id, member_id),
    INDEX idx_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='思想汇报任务党员分配表';
