USE example_db;

-- 创建组织关系转入申请表
CREATE TABLE IF NOT EXISTS transfer_in_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    birth_date VARCHAR(50) COMMENT '出生日期',
    id_card VARCHAR(20) COMMENT '身份证号',
    student_no VARCHAR(50) COMMENT '学号',
    class_name VARCHAR(100) COMMENT '班级',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    native_place VARCHAR(100) COMMENT '籍贯',
    education VARCHAR(20) COMMENT '学历',
    political_status VARCHAR(20) DEFAULT '中共党员' COMMENT '政治面貌',
    branch_name VARCHAR(100) COMMENT '申请转入党支部',
    join_party_time VARCHAR(20) COMMENT '入党时间',
    introducer VARCHAR(50) COMMENT '入党介绍人',
    reason TEXT COMMENT '转入原因',
    status VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核/已通过/已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    review_time DATETIME COMMENT '审核时间',
    review_comment TEXT COMMENT '审核意见',
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_branch_name (branch_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织关系转入申请表';

-- 创建组织关系转出申请表
CREATE TABLE IF NOT EXISTS transfer_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    member_id BIGINT NOT NULL COMMENT '党员ID',
    member_name VARCHAR(50) NOT NULL COMMENT '党员姓名',
    current_branch VARCHAR(100) NOT NULL COMMENT '当前党支部',
    target_branch VARCHAR(100) NOT NULL COMMENT '目标党支部',
    reason TEXT COMMENT '转移原因',
    status VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核/已通过/已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    review_time DATETIME COMMENT '审核时间',
    review_comment TEXT COMMENT '审核意见',
    INDEX idx_member_id (member_id),
    INDEX idx_status (status),
    INDEX idx_current_branch (current_branch)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织关系转出申请表';