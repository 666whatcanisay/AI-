-- 缴费通知表
CREATE TABLE IF NOT EXISTS `fee_notification` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `year` VARCHAR(10) NOT NULL COMMENT '年份',
  `month` VARCHAR(10) NOT NULL COMMENT '月份',
  `title` VARCHAR(200) DEFAULT NULL COMMENT '通知标题',
  `content` TEXT DEFAULT NULL COMMENT '通知说明',
  `status` VARCHAR(20) DEFAULT '草稿' COMMENT '状态：草稿/已发布',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX `idx_year_month` (`year`, `month`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费通知表';
