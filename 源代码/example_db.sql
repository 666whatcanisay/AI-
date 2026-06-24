/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : localhost:3306
 Source Schema         : example_db

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 14/06/2026 20:47:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动内容',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime(0) NOT NULL COMMENT '开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '结束时间',
  `max_participants` int(0) NOT NULL DEFAULT 50 COMMENT '最大参与人数',
  `current_participants` int(0) NOT NULL DEFAULT 0 COMMENT '当前参与人数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '进行中' COMMENT '状态：进行中/已结束',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (1, '主题党日活动', '学习党的二十大精神，开展专题讨论', '党员活动室', '2024-06-15 14:00:00', '2024-06-15 16:00:00', 30, 5, '已结束', '2026-05-25 17:08:30');
INSERT INTO `activity` VALUES (2, '志愿服务活动', '社区志愿服务，帮助孤寡老人', '幸福社区', '2024-06-20 09:00:00', '2024-06-20 12:00:00', 20, 4, '进行中', '2026-05-25 17:08:30');
INSERT INTO `activity` VALUES (3, '党史学习教育', '参观革命纪念馆', '革命历史纪念馆', '2024-05-01 09:00:00', '2024-05-01 12:00:00', 50, 45, '已结束', '2026-05-25 17:08:30');
INSERT INTO `activity` VALUES (4, '讲座', '', '图书馆', '2026-06-01 00:00:00', '2026-06-02 00:00:00', 50, 0, '报名中', '2026-05-31 19:51:49');

-- ----------------------------
-- Table structure for activity_signup
-- ----------------------------
DROP TABLE IF EXISTS `activity_signup`;
CREATE TABLE `activity_signup`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint(0) NOT NULL COMMENT '活动ID',
  `member_id` bigint(0) NOT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '党员姓名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已报名' COMMENT '状态：已报名/已签到',
  `signup_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '报名时间',
  `checkin_time` datetime(0) NULL DEFAULT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity`(`activity_id`) USING BTREE,
  INDEX `idx_member`(`member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_signup
-- ----------------------------
INSERT INTO `activity_signup` VALUES (1, 1, 1, '张三', '已签到', '2026-05-25 17:08:30', '2024-06-15 13:50:00');
INSERT INTO `activity_signup` VALUES (2, 1, 2, '李四', '已签到', '2026-05-25 17:08:30', '2026-05-27 12:44:21');
INSERT INTO `activity_signup` VALUES (3, 1, 3, '王五', '已签到', '2026-05-25 17:08:30', '2024-06-15 13:45:00');
INSERT INTO `activity_signup` VALUES (4, 2, 1, '张三', '已签到', '2026-05-25 17:08:30', '2026-05-27 13:08:17');
INSERT INTO `activity_signup` VALUES (5, 2, 4, '赵六', '已报名', '2026-05-25 17:08:30', NULL);
INSERT INTO `activity_signup` VALUES (6, 3, 1, '张三', '已签到', '2026-05-25 17:08:30', '2024-05-01 08:55:00');
INSERT INTO `activity_signup` VALUES (7, 3, 2, '李四', '已签到', '2026-05-25 17:08:30', '2024-05-01 08:58:00');
INSERT INTO `activity_signup` VALUES (8, 3, 3, '王五', '已签到', '2026-05-25 17:08:30', '2024-05-01 09:00:00');
INSERT INTO `activity_signup` VALUES (9, 3, 4, '赵六', '已签到', '2026-05-25 17:08:30', '2024-05-01 09:02:00');
INSERT INTO `activity_signup` VALUES (10, 1, 6, '吴思儒', '已签到', '2026-05-27 13:05:49', '2026-05-27 13:06:00');
INSERT INTO `activity_signup` VALUES (11, 1, 9, '陈七', '已签到', '2026-05-30 21:33:07', '2026-05-30 21:33:32');
INSERT INTO `activity_signup` VALUES (12, 2, 9, '陈七', '已报名', '2026-05-30 21:33:13', NULL);
INSERT INTO `activity_signup` VALUES (13, 2, 20, '笑笑', '已报名', '2026-05-31 20:00:14', NULL);
INSERT INTO `activity_signup` VALUES (14, 4, 20, '笑笑', '已报名', '2026-05-31 20:01:55', NULL);
INSERT INTO `activity_signup` VALUES (15, 1, 20, '笑笑', '已签到', '2026-05-31 20:01:56', '2026-05-31 20:02:02');
INSERT INTO `activity_signup` VALUES (16, 2, 19, '吴迪', '已报名', '2026-05-31 20:02:46', NULL);
INSERT INTO `activity_signup` VALUES (17, 4, 23, '李小龙', '已报名', '2026-06-14 19:32:07', NULL);
INSERT INTO `activity_signup` VALUES (18, 4, 6, '吴思儒', '已报名', '2026-06-14 20:15:17', NULL);
INSERT INTO `activity_signup` VALUES (19, 2, 6, '吴思儒', '已报名', '2026-06-14 20:15:18', NULL);

-- ----------------------------
-- Table structure for application
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录密码',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birth_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出生年月',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所在部门',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学号',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `native_place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '籍贯',
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历',
  `political_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '政治面貌',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '申请理由',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'APPLYING' COMMENT '状态：APPLYING/ACTIVIST/DEVELOP_TARGET/PROBATIONARY/FORMAL',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请书文件地址',
  `current_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前审批层级',
  `introducer` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入党介绍人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `last_update_time` datetime(0) NULL DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '入党申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of application
-- ----------------------------
INSERT INTO `application` VALUES (1, '吴迪', '123456', '男', '2017-05-03', '计算机学院党支部', '13654895256', '440982200123654699', '2025002', '23软件', '222@qq.com', '广东', '博士', '共青团员', NULL, 'PROBATIONARY', NULL, NULL, '吴思儒', '2026-05-30 23:28:19', '2026-05-30 23:37:34', NULL);
INSERT INTO `application` VALUES (2, '李小龙', '123456', '男', '2003-06-04', '计算机学院党支部', '13800138088', '440982200306066699', '2026001', '23软件工程', 'sunqi@example.com', '广东广州', '博士', '共青团员', NULL, 'FORMAL', NULL, NULL, '吴思儒', '2026-06-14 18:37:15', '2026-06-14 19:36:41', NULL);
INSERT INTO `application` VALUES (5, '吴小', '123456', '女', '2005-06-21', '文化与传播学院党支部', '13800138088', '440982200403066895', '2026003', '23软件工程技术', 'sunqi@example.com', '广东', '硕士', '共青团员', NULL, 'ACTIVIST', NULL, NULL, '吴思儒', '2026-06-14 20:12:22', '2026-06-14 20:13:44', NULL);

-- ----------------------------
-- Table structure for approval_record
-- ----------------------------
DROP TABLE IF EXISTS `approval_record`;
CREATE TABLE `approval_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '审批记录ID',
  `application_id` bigint(0) NOT NULL COMMENT '申请ID',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审批层级名称',
  `approved` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否通过：0-未通过/1-通过',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核人',
  `approve_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application`(`application_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审批记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of approval_record
-- ----------------------------
INSERT INTO `approval_record` VALUES (81, 1, '支部审核', 1, NULL, '2026-05-30 23:30:17', '');
INSERT INTO `approval_record` VALUES (82, 1, '支部审核', 1, NULL, '2026-05-30 23:32:25', '');
INSERT INTO `approval_record` VALUES (83, 1, '党委审核', 1, NULL, '2026-05-30 23:33:32', '');
INSERT INTO `approval_record` VALUES (84, 1, '组织部审核', 1, NULL, '2026-05-30 23:37:34', '');
INSERT INTO `approval_record` VALUES (91, 2, '支部审核', 1, NULL, '2026-06-14 19:08:53', '');
INSERT INTO `approval_record` VALUES (92, 2, '支部审核', 1, NULL, '2026-06-14 19:09:11', '');
INSERT INTO `approval_record` VALUES (93, 2, '党委审核', 1, NULL, '2026-06-14 19:30:31', '');
INSERT INTO `approval_record` VALUES (94, 2, '组织部审核', 1, NULL, '2026-06-14 19:30:46', '');
INSERT INTO `approval_record` VALUES (95, 2, '组织部审核', 1, NULL, '2026-06-14 19:36:41', '');
INSERT INTO `approval_record` VALUES (96, 5, '支部审核', 1, NULL, '2026-06-14 20:13:29', '');
INSERT INTO `approval_record` VALUES (97, 5, '支部审核', 1, NULL, '2026-06-14 20:13:44', '');

-- ----------------------------
-- Table structure for development_report
-- ----------------------------
DROP TABLE IF EXISTS `development_report`;
CREATE TABLE `development_report`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(0) NOT NULL COMMENT '关联的申请ID',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告类型：APPLICATION-申请报告, ACTIVIST-积极分子思想报告, DEVELOP-发展对象思想报告, PROBATIONARY-预备党员思想报告, FORMAL-转正思想报告',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报告内容',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告作者',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核, APPROVED-已审核, REJECTED-已驳回',
  `review_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `reviewer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_application_id`(`application_id`) USING BTREE,
  INDEX `idx_report_type`(`report_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '发展流程思想报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of development_report
-- ----------------------------
INSERT INTO `development_report` VALUES (21, 1, 'APPLICATION', '1', '1', '吴迪', 'APPROVED', '', '管理员', '2026-05-30 23:29:45', '2026-05-30 23:30:10', '2026-05-30 23:30:10');
INSERT INTO `development_report` VALUES (22, 1, 'ACTIVIST', '2', '2', '吴迪', 'APPROVED', '', '管理员', '2026-05-30 23:32:15', '2026-05-30 23:32:23', '2026-05-30 23:32:23');
INSERT INTO `development_report` VALUES (23, 1, 'DEVELOP', '3', '3', '吴迪', 'REJECTED', '', '管理员', '2026-05-30 23:33:01', '2026-05-30 23:33:06', '2026-05-30 23:33:06');
INSERT INTO `development_report` VALUES (24, 1, 'DEVELOP', '3', '3', '吴迪', 'APPROVED', '', '管理员', '2026-05-30 23:33:22', '2026-05-30 23:33:30', '2026-05-30 23:33:30');
INSERT INTO `development_report` VALUES (25, 1, 'PROBATIONARY', '7', '7', '吴迪', 'APPROVED', '', '管理员', '2026-05-30 23:37:27', '2026-05-30 23:37:32', '2026-05-30 23:37:32');
INSERT INTO `development_report` VALUES (32, 2, 'APPLICATION', '1', '1', '李小龙', 'APPROVED', '', '管理员', '2026-06-14 18:58:44', '2026-06-14 19:08:50', '2026-06-14 19:08:50');
INSERT INTO `development_report` VALUES (35, 2, 'ACTIVIST', '2', '2', '李小龙', 'APPROVED', '', '管理员', '2026-06-14 19:09:02', '2026-06-14 19:09:09', '2026-06-14 19:09:09');
INSERT INTO `development_report` VALUES (36, 2, 'DEVELOP', '3', '3', '李小龙', 'APPROVED', '', '管理员', '2026-06-14 19:30:23', '2026-06-14 19:30:29', '2026-06-14 19:30:29');
INSERT INTO `development_report` VALUES (37, 2, 'PROBATIONARY', '4', '4', '李小龙', 'APPROVED', '', '管理员', '2026-06-14 19:30:41', '2026-06-14 19:30:45', '2026-06-14 19:30:45');
INSERT INTO `development_report` VALUES (38, 2, 'FORMAL', '5', '5', '李小龙', 'APPROVED', '', '管理员', '2026-06-14 19:36:33', '2026-06-14 19:36:39', '2026-06-14 19:36:39');
INSERT INTO `development_report` VALUES (39, 5, 'APPLICATION', '1', '1', '吴小', 'APPROVED', '', '管理员', '2026-06-14 20:13:21', '2026-06-14 20:13:27', '2026-06-14 20:13:27');
INSERT INTO `development_report` VALUES (40, 5, 'ACTIVIST', '2', '2', '吴小', 'APPROVED', '', '管理员', '2026-06-14 20:13:36', '2026-06-14 20:13:41', '2026-06-14 20:13:41');

-- ----------------------------
-- Table structure for evaluation
-- ----------------------------
DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE `evaluation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` bigint(0) NOT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '党员姓名',
  `year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考核年份',
  `month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考核月份',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考核等级：优秀、良好、合格、不合格',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评语',
  `evaluator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考核人',
  `evaluate_time` datetime(0) NULL DEFAULT NULL COMMENT '考核时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id`) USING BTREE,
  INDEX `idx_year_month`(`year`, `month`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评议考核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation
-- ----------------------------
INSERT INTO `evaluation` VALUES (1, 6, '吴思儒', '2026', '5', '优秀', '', '管理员', '2026-05-27 23:24:48', '2026-05-27 23:24:48', '2026-05-27 23:24:48');
INSERT INTO `evaluation` VALUES (2, 1, '张三', '2026', '5', '合格', '', '管理员', '2026-05-27 23:25:00', '2026-05-27 23:25:00', '2026-05-27 23:25:00');
INSERT INTO `evaluation` VALUES (3, 2, '李四', '2026', '5', '良好', '', '管理员', '2026-05-28 10:29:43', '2026-05-28 10:29:43', '2026-05-28 10:29:43');
INSERT INTO `evaluation` VALUES (4, 22, '微微', '2026', '5', '优秀', '', '管理员', '2026-05-31 19:57:40', '2026-05-31 19:57:40', '2026-05-31 19:57:40');

-- ----------------------------
-- Table structure for fee_notification
-- ----------------------------
DROP TABLE IF EXISTS `fee_notification`;
CREATE TABLE `fee_notification`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年份',
  `month` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '月份',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知说明',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '草稿' COMMENT '状态：草稿/已发布',
  `publish_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_year_month`(`year`, `month`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '缴费通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fee_notification
-- ----------------------------
INSERT INTO `fee_notification` VALUES (7, '2026', '05', '2026年05月党员缴费', '', '已发布', '2026-05-26 21:29:14', '2026-05-26 21:29:14');
INSERT INTO `fee_notification` VALUES (19, '2026', '04', '2026年04月党员缴费', '', '已发布', '2026-05-31 08:55:20', '2026-05-31 08:55:20');
INSERT INTO `fee_notification` VALUES (21, '2026', '01', '2026年01月党员缴费', '', '已发布', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `fee_notification` VALUES (24, '2026', '06', '2026年06月党员缴费', '', '已发布', '2026-06-14 20:14:12', '2026-06-14 20:14:12');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知内容',
  `publish_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_publish_time`(`publish_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, '关于开展主题党日活动的通知', '各党支部：为深入学习贯彻党的二十大精神，经研究决定，于近期开展主题党日活动，请各支部认真组织党员参加。具体安排另行通知。', '2024-06-01 10:00:00', '2026-05-28 10:16:15', '2026-05-28 10:16:15');
INSERT INTO `notification` VALUES (2, '党费缴纳提醒', '各位党员：请及时缴纳本月党费，缴费截止日期为每月25日。如有特殊情况请及时联系组织委员。', '2024-06-10 09:00:00', '2026-05-28 10:16:15', '2026-05-28 10:16:15');
INSERT INTO `notification` VALUES (3, '新党员发展培训通知', '各支部：近期将举办新党员发展培训，请各支部通知相关人员按时参加培训。', '2024-06-15 14:00:00', '2026-05-28 10:16:15', '2026-05-28 10:16:15');
INSERT INTO `notification` VALUES (4, '还会', '就好哈哈哈哈哈', '2026-05-28 10:17:35', '2026-05-28 10:17:35', '2026-05-28 10:17:35');
INSERT INTO `notification` VALUES (5, '1', '我的大富大贵阿发法国阿发 是否是噶', '2026-05-28 10:43:59', '2026-05-28 10:43:59', '2026-05-28 10:43:59');
INSERT INTO `notification` VALUES (6, '222', '2222', '2026-05-31 21:24:00', '2026-05-31 21:24:00', '2026-05-31 21:24:00');

-- ----------------------------
-- Table structure for party_branch
-- ----------------------------
DROP TABLE IF EXISTS `party_branch`;
CREATE TABLE `party_branch`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '党支部ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '党支部名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `leader_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人姓名',
  `leader_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `member_count` int(0) NOT NULL DEFAULT 0 COMMENT '党员人数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '正常' COMMENT '状态：正常/停用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '党支部表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of party_branch
-- ----------------------------
INSERT INTO `party_branch` VALUES (1, '计算机学院党支部', '', '吴思儒', '13686722222', 5, '正常', '2026-05-27 13:30:34', '2026-05-31 09:02:33');
INSERT INTO `party_branch` VALUES (2, '商学院党支部', '', '罗泽', '13686711111', 2, '正常', '2026-05-27 13:31:40', '2026-05-30 22:39:19');
INSERT INTO `party_branch` VALUES (3, '财经政法学院党支部', '', '梁栩逸', '15216854449', 2, '正常', '2026-05-27 20:36:17', '2026-05-30 22:38:51');
INSERT INTO `party_branch` VALUES (4, '健康学院党支部', '', '吴小小', '18125001763', 2, '正常', '2026-05-27 20:37:34', '2026-05-30 22:38:31');
INSERT INTO `party_branch` VALUES (5, '建筑工程学院党支部', '', '小米', '15219832222', 0, '正常', '2026-05-28 11:33:09', '2026-05-28 12:21:28');
INSERT INTO `party_branch` VALUES (6, '体育学院党支部', '', '小明', '18231598845', 1, '正常', '2026-05-28 11:33:57', '2026-05-30 22:38:45');
INSERT INTO `party_branch` VALUES (7, '外国语学院党支部', '', '小红', '13694526879', 1, '正常', '2026-05-28 11:34:23', '2026-05-30 22:38:24');
INSERT INTO `party_branch` VALUES (8, '艺术学院党支部', '', '小一', '13652495666', 2, '正常', '2026-05-28 11:35:21', '2026-05-30 22:38:56');
INSERT INTO `party_branch` VALUES (9, '音乐舞蹈学院党支部', '', '笑笑', '18625498235', 3, '正常', '2026-05-28 11:36:04', '2026-05-31 19:32:02');
INSERT INTO `party_branch` VALUES (10, '文化与传播学院党支部', '', '小雯', '13689547652', 1, '正常', '2026-05-28 11:37:06', '2026-05-30 22:38:07');

-- ----------------------------
-- Table structure for party_fee
-- ----------------------------
DROP TABLE IF EXISTS `party_fee`;
CREATE TABLE `party_fee`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '缴费记录ID',
  `member_id` bigint(0) NOT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '党员姓名',
  `year` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年份',
  `month` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '月份',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已缴费' COMMENT '状态：已缴费/待缴费',
  `pay_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '缴费时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member`(`member_id`) USING BTREE,
  INDEX `idx_year_month`(`year`, `month`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 305 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '党费表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of party_fee
-- ----------------------------
INSERT INTO `party_fee` VALUES (35, 1, '张三', '2026', '05', 11.00, '已缴费', '2026-05-30 23:39:05', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (36, 2, '李四', '2026', '05', 0.00, '待缴费', '2026-05-26 21:29:14', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (37, 3, '王五', '2026', '05', 0.01, '已缴费', '2026-05-28 11:28:33', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (38, 4, '赵六', '2026', '05', 0.00, '待缴费', '2026-05-26 21:29:14', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (39, 6, '吴思儒', '2026', '05', 100.00, '已缴费', '2026-05-26 21:30:09', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (40, 7, '威威', '2026', '05', 100.00, '已缴费', '2026-05-26 21:34:42', '2026-05-26 21:29:14');
INSERT INTO `party_fee` VALUES (189, 1, '张三', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (190, 2, '李四', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (191, 3, '王五', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (192, 4, '赵六', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (193, 6, '吴思儒', '2026', '04', 200.00, '已缴费', '2026-05-31 21:27:33', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (194, 8, '零零', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (195, 9, '陈七', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (196, 10, '李明', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (197, 11, '刘洋', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (198, 12, '陈静', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (199, 13, '张伟', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (200, 14, '赵丽', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (201, 15, '孙强', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (202, 16, '周敏', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (203, 17, '吴涛', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (204, 18, '郑雪', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (205, 19, '吴迪', '2026', '04', 0.00, '待缴费', '2026-05-31 08:55:19', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (206, 20, '笑笑', '2026', '04', 7.01, '已缴费', '2026-05-31 08:55:40', '2026-05-31 08:55:20');
INSERT INTO `party_fee` VALUES (226, 1, '张三', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (227, 2, '李四', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (228, 3, '王五', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (229, 4, '赵六', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (230, 6, '吴思儒', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (231, 8, '零零', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (232, 9, '陈七', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (233, 10, '李明', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (234, 11, '刘洋', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (235, 12, '陈静', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (236, 13, '张伟', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (237, 14, '赵丽', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (238, 15, '孙强', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (239, 16, '周敏', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (240, 17, '吴涛', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (241, 18, '郑雪', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (242, 19, '吴迪', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (243, 20, '笑笑', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (244, 21, '安安', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (245, 22, '微微', '2026', '01', 0.00, '待缴费', '2026-05-31 19:42:58', '2026-05-31 19:42:58');
INSERT INTO `party_fee` VALUES (285, 1, '张三', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (286, 3, '王五', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (287, 4, '赵六', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (288, 6, '吴思儒', '2026', '06', 10.00, '已缴费', '2026-06-14 20:14:30', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (289, 8, '零零', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (290, 9, '陈七', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (291, 10, '李明', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (292, 11, '刘洋', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (293, 12, '陈静', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (294, 13, '张伟', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (295, 14, '赵丽', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (296, 15, '孙强', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (297, 16, '周敏', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (298, 17, '吴涛', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (299, 18, '郑雪', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (300, 19, '吴迪', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (301, 20, '笑笑', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (302, 21, '安安', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (303, 22, '微微', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');
INSERT INTO `party_fee` VALUES (304, 23, '李小龙', '2026', '06', 0.00, '待缴费', '2026-06-14 20:14:11', '2026-06-14 20:14:12');

-- ----------------------------
-- Table structure for party_member
-- ----------------------------
DROP TABLE IF EXISTS `party_member`;
CREATE TABLE `party_member`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '党员ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录密码',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '性别',
  `birth_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `student_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学号',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `native_place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '籍贯',
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历',
  `political_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '政治面貌',
  `party_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '党员状态：FORMAL/PROBATIONARY',
  `branch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '党支部',
  `join_party_time` datetime(0) NULL DEFAULT NULL COMMENT '入党时间',
  `introducer` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入党介绍人',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_branch`(`branch_name`) USING BTREE,
  INDEX `idx_party_status`(`party_status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '党员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of party_member
-- ----------------------------
INSERT INTO `party_member` VALUES (1, '张三', '123456', '男', '2002-01-15', '440982200604183248', '2021001', '计算机2101班', '13800138001', 'zhangsan@example.com', '北京', '本科', '中共党员', 'FORMAL', '财经政法学院党支部', '2020-06-01 00:00:00', '李书记', NULL, '2026-05-25 17:08:30', '2026-05-27 20:38:39');
INSERT INTO `party_member` VALUES (3, '王五', '', '男', '2002-08-10', '110101200208100033', '2021003', '计算机2102班', '13800138003', 'wangwu@example.com', '广州', '本科', '中共党员', 'FORMAL', '音乐舞蹈学院党支部', '2019-12-01 00:00:00', '王书记', NULL, '2026-05-25 17:08:30', '2026-05-28 12:27:22');
INSERT INTO `party_member` VALUES (4, '赵六', '123456', '女', '2002-11-25', '110101200211250044', '2021004', '计算机2102班', '13800138004', 'zhaoliu@example.com', '深圳', '本科', '中共党员', 'FORMAL', '商学院党支部', '2022-01-20 00:00:00', '王书记', NULL, '2026-05-25 17:08:30', '2026-05-27 12:34:06');
INSERT INTO `party_member` VALUES (6, '吴思儒', '123456', '男', '2025-05-01', '449820322682659988', '22222', '22', '13686877777', '123@qq.com', '广州', '大专', '中共党员', 'FORMAL', '计算机学院党支部', '2026-05-26 19:25:06', NULL, NULL, '2026-05-26 19:25:06', '2026-05-31 19:59:01');
INSERT INTO `party_member` VALUES (8, '零零', '123456', '女', '2013-05-03', '440982200403066688', '112', '23', '13655565652', '1@qq.com', '广东', '博士', '中共党员', 'FORMAL', '健康学院党支部', '2025-05-14 00:00:00', '吴思儒', NULL, '2026-05-27 22:29:43', '2026-05-27 22:29:43');
INSERT INTO `party_member` VALUES (9, '陈七', '', '男', '2021-05-03', '440982200403066655', '1', '25', '13900139001', '1@qq.com', '广州', '博士', '中共党员', 'FORMAL', '艺术学院党支部', '2026-05-28 11:27:27', '吴书记', NULL, '2026-05-28 11:27:27', '2026-05-31 08:55:58');
INSERT INTO `party_member` VALUES (10, '李明', '', '男', '1995-03-15', '110101199503151234', '2021011', '计算机21-1班', '13800001001', 'liming@example.com', '北京市', '本科', '中共党员', 'FORMAL', '文化与传播学院党支部', '2023-06-01 00:00:00', '张三', '预备党员', '2026-05-30 22:36:59', '2026-05-31 08:55:54');
INSERT INTO `party_member` VALUES (11, '刘洋', '', '男', '1994-11-08', '440101199411081234', '2020001', '软件工程20-1班', '13800001003', 'liuyang@example.com', '广东省', '本科', '中共党员', 'FORMAL', '外国语学院党支部', '2022-09-01 00:00:00', '王五', '正式党员', '2026-05-30 22:36:59', '2026-05-30 22:51:02');
INSERT INTO `party_member` VALUES (12, '陈静', '', '女', '1997-02-14', '510101199702141234', '2021013', '网络工程21-1班', '13800001004', 'chenjing@example.com', '四川省', '本科', '中共党员', 'FORMAL', '健康学院党支部', '2023-06-15 00:00:00', '赵六', '预备党员', '2026-05-30 22:36:59', '2026-05-31 08:56:23');
INSERT INTO `party_member` VALUES (13, '张伟', '', '男', '1995-08-30', '320101199508301234', '2020002', '计算机20-1班', '13800001005', 'zhangwei@example.com', '江苏省', '本科', '中共党员', 'FORMAL', '计算机学院党支部', '2021-07-01 00:00:00', '孙七', '正式党员', '2026-05-30 22:36:59', '2026-05-30 22:51:02');
INSERT INTO `party_member` VALUES (14, '赵丽', '', '女', '1998-05-19', '330101199805191234', '2022001', '数据科学22-1班', '13800001006', 'zhaoli@example.com', '浙江省', '本科', '中共党员', 'FORMAL', '体育学院党支部', '2024-01-10 00:00:00', '周八', '预备党员', '2026-05-30 22:36:59', '2026-05-31 08:56:06');
INSERT INTO `party_member` VALUES (15, '孙强', '', '男', '1993-12-25', '420101199312251234', '2019001', '计算机19-1班', '13800001007', 'sunqiang@example.com', '湖北省', '本科', '中共党员', 'FORMAL', '财经政法学院党支部', '2020-07-01 00:00:00', '吴九', '正式党员', '2026-05-30 22:36:59', '2026-05-30 22:51:02');
INSERT INTO `party_member` VALUES (16, '周敏', '', '女', '1996-09-03', '430101199609031234', '2021014', '人工智能21-1班', '13800001008', 'zhoumin@example.com', '湖南省', '本科', '中共党员', 'FORMAL', '艺术学院党支部', '2023-06-20 00:00:00', '郑十', '预备党员', '2026-05-30 22:36:59', '2026-05-31 08:56:10');
INSERT INTO `party_member` VALUES (17, '吴涛', '', '男', '1994-04-17', '360101199404171234', '2020003', '软件工程20-2班', '13800001009', 'wutao@example.com', '江西省', '本科', '中共党员', 'FORMAL', '商学院党支部', '2022-09-01 00:00:00', '王芳', '正式党员', '2026-05-30 22:36:59', '2026-05-30 22:51:02');
INSERT INTO `party_member` VALUES (18, '郑雪', '', '女', '1997-10-28', '370101199710281234', '2021005', '计算机21-3班', '13800001010', 'zhengxue@example.com', '山东省', '本科', '中共党员', 'FORMAL', '计算机学院党支部', '2023-06-25 00:00:00', '刘洋', '预备党员', '2026-05-30 22:36:59', '2026-05-31 08:56:14');
INSERT INTO `party_member` VALUES (19, '吴迪', '123456', '男', '2017-05-03', '440982200123654699', '2025002', '23软件', '13654895256', '222@qq.com', '广东', '博士', '中共党员', 'PROBATIONARY', '计算机学院党支部', '2026-05-30 23:37:34', '吴思儒', NULL, '2026-05-30 23:37:34', '2026-05-30 23:37:34');
INSERT INTO `party_member` VALUES (20, '笑笑', '123456', '女', '2021-05-05', '440982200106044433', '2025006', '23软件', '13525489999', '2222@qq.com', '广东', '本科', '中共党员', 'FORMAL', '音乐舞蹈学院党支部', '2026-05-31 08:46:47', '吴思儒', NULL, '2026-05-31 08:46:47', '2026-05-31 19:59:28');
INSERT INTO `party_member` VALUES (21, '安安', '123456', '男', '2011-05-04', '440982200403088899', '2025008', '24网咯', '13652422265', '1@qqx.com', '广东', '博士', '中共党员', 'FORMAL', '计算机学院党支部', '2019-05-09 00:00:00', '吴思儒', '', '2026-05-31 09:02:33', '2026-05-31 09:02:33');
INSERT INTO `party_member` VALUES (22, '微微', '123456', '女', '2016-05-04', '440982200403068811', '2025010', '25信息', '13525244446', '101@qq.com', '广东', '博士', '中共党员', 'FORMAL', '音乐舞蹈学院党支部', '2026-05-01 00:00:00', '吴思儒', '', '2026-05-31 19:32:02', '2026-05-31 19:32:02');
INSERT INTO `party_member` VALUES (23, '李小龙', '123456', '男', '2003-06-04', '440982200306066699', '2026001', '23软件工程', '13800138088', 'sunqi@example.com', '广东广州', '博士', '中共党员', 'FORMAL', '计算机学院党支部', '2026-06-14 19:30:46', '吴思儒', NULL, '2026-06-14 19:30:46', '2026-06-14 19:36:41');

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '汇报ID',
  `task_id` bigint(0) NULL DEFAULT NULL COMMENT '关联任务ID',
  `member_id` bigint(0) NULL DEFAULT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '党员姓名',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '汇报标题',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '汇报人',
  `report_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '汇报日期',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '汇报内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件地址',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '思想汇报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report
-- ----------------------------
INSERT INTO `report` VALUES (12, 8, 6, '吴思儒', '2026年第1季度思想汇报', '吴思儒', '2026-05-31', '11111111', 'APPROVED', NULL, '', '2026-05-31 19:25:34', '2026-05-31 19:25:40');
INSERT INTO `report` VALUES (13, 8, 20, '笑笑', '2026年第1季度思想汇报', '笑笑', '2026-05-31', '11111', 'PENDING', NULL, NULL, '2026-05-31 20:00:30', NULL);
INSERT INTO `report` VALUES (15, 9, 19, '吴迪', '2026年第2季度思想汇报', '吴迪', '2026-05-31', '是是是', 'APPROVED', NULL, '', '2026-05-31 20:04:51', '2026-05-31 20:04:56');
INSERT INTO `report` VALUES (16, 11, 6, '吴思儒', '2026年第3季度思想汇报', '吴思儒', '2026-06-14', '11', 'PENDING', NULL, NULL, '2026-06-14 20:15:25', NULL);

-- ----------------------------
-- Table structure for report_task
-- ----------------------------
DROP TABLE IF EXISTS `report_task`;
CREATE TABLE `report_task`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '任务说明',
  `year` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年份',
  `quarter` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '季度',
  `month` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '月份',
  `deadline` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '截止日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '进行中' COMMENT '状态：进行中/已结束',
  `publish_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_year_quarter`(`year`, `quarter`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '汇报任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report_task
-- ----------------------------
INSERT INTO `report_task` VALUES (8, '1', '1', '2026', '1', NULL, '2026-06-04T23:59:59', '已结束', '2026-05-31 19:25:00', '2026-05-31 19:25:00', '2026-06-14 19:03:05');
INSERT INTO `report_task` VALUES (9, '2', '222222', '2026', '2', NULL, '2026-06-04T23:59:59', '已结束', '2026-05-31 20:03:26', '2026-05-31 20:03:26', '2026-06-14 19:03:05');
INSERT INTO `report_task` VALUES (11, '3', '3', '2026', '3', NULL, '2026-06-17T23:59:59', '已发布', '2026-06-14 19:15:26', '2026-06-14 19:15:26', '2026-06-14 19:15:26');

-- ----------------------------
-- Table structure for report_task_assignment
-- ----------------------------
DROP TABLE IF EXISTS `report_task_assignment`;
CREATE TABLE `report_task_assignment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(0) NOT NULL,
  `member_id` bigint(0) NOT NULL,
  `member_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `assigned_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_member`(`task_id`, `member_id`) USING BTREE,
  INDEX `idx_member_id`(`member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 71 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report_task_assignment
-- ----------------------------
INSERT INTO `report_task_assignment` VALUES (1, 11, 1, '张三', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (2, 11, 3, '王五', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (3, 11, 4, '赵六', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (4, 11, 6, '吴思儒', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (5, 11, 8, '零零', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (6, 11, 9, '陈七', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (7, 11, 10, '李明', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (8, 11, 11, '刘洋', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (9, 11, 12, '陈静', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (10, 11, 13, '张伟', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (11, 11, 14, '赵丽', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (12, 11, 15, '孙强', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (13, 11, 16, '周敏', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (14, 11, 17, '吴涛', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (15, 11, 18, '郑雪', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (16, 11, 19, '吴迪', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (17, 11, 20, '笑笑', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (18, 11, 21, '安安', '2026-06-14 19:21:54');
INSERT INTO `report_task_assignment` VALUES (19, 11, 22, '微微', '2026-06-14 19:21:54');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
  `member_id` bigint(0) NULL DEFAULT NULL COMMENT '关联党员ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin', 'ADMIN', NULL, NULL, '2026-05-25 17:08:30');
INSERT INTO `sys_user` VALUES (7, '吴思儒', '123456', 'ADMIN', NULL, '吴思儒', '2026-05-27 20:40:03');

-- ----------------------------
-- Table structure for transfer_application
-- ----------------------------
DROP TABLE IF EXISTS `transfer_application`;
CREATE TABLE `transfer_application`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '转移申请ID',
  `member_id` bigint(0) NOT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '党员姓名',
  `current_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '当前党支部',
  `target_branch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标党支部',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '转移原因',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '待审核' COMMENT '状态：待审核/已通过/已拒绝',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '申请时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '组织关系转移申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transfer_application
-- ----------------------------
INSERT INTO `transfer_application` VALUES (1, 7, '威威', '计算机学院党支部', '广州', '哇哇哇哇哇哇哇', '已通过', '2026-05-27 21:02:17', '2026-05-27 21:02:30', '2026-05-27 21:02:30', NULL);
INSERT INTO `transfer_application` VALUES (2, 2, '李四', '建筑工程学院党支部', '哇哇哇', '哇哇哇哇哇哇哇哇', '已拒绝', '2026-05-31 21:28:30', '2026-05-31 21:28:39', '2026-05-31 21:28:39', '');
INSERT INTO `transfer_application` VALUES (3, 2, '李四', '建筑工程学院党支部', 'w\'w\'w\'w\'w\'w', '嗡嗡嗡嗡嗡嗡', '已通过', '2026-05-31 21:28:50', '2026-05-31 21:29:07', '2026-05-31 21:29:07', '');

-- ----------------------------
-- Table structure for transfer_in_application
-- ----------------------------
DROP TABLE IF EXISTS `transfer_in_application`;
CREATE TABLE `transfer_in_application`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birth_date` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `student_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学号',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `native_place` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '籍贯',
  `education` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学历',
  `political_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '中共党员' COMMENT '政治面貌',
  `branch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请转入党支部',
  `join_party_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入党时间',
  `introducer` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入党介绍人',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '转入原因',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '待审核' COMMENT '状态：待审核/已通过/已拒绝',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `review_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `review_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_branch_name`(`branch_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '组织关系转入申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transfer_in_application
-- ----------------------------
INSERT INTO `transfer_in_application` VALUES (1, '零零', '女', '2013-05-03', '440982200403066688', '112', '23', '13655565652', '1@qq.com', '广东', '博士', '中共党员', '健康学院党支部', '2025-05-14', '吴思儒', '哈哈哈哈哈哈哈', '已通过', '2026-05-27 22:20:22', '2026-05-27 22:29:43', '2026-05-27 22:29:43', '同意');

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '志愿活动ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动内容',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务地点',
  `service_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务日期',
  `service_hours` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务时长(小时)',
  `max_participants` int(0) NOT NULL DEFAULT 30 COMMENT '最大参与人数',
  `current_participants` int(0) NOT NULL DEFAULT 0 COMMENT '当前参与人数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '报名中' COMMENT '状态：报名中/进行中/已结束',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '志愿活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (5, '扫地', '', '学校', '2026-05-06', '1', 30, 1, '已结束', '2026-05-26 22:30:51', '2026-05-28 12:03:06');
INSERT INTO `volunteer` VALUES (6, '大扫除', '', '操场', '2026-04-30', '2', 30, 6, '已结束', '2026-05-30 22:17:21', '2026-05-31 21:13:31');
INSERT INTO `volunteer` VALUES (7, '大扫除', '', '学校', '2026-04-30', '2', 30, 2, '报名中', '2026-05-31 21:22:57', '2026-05-31 21:22:57');

-- ----------------------------
-- Table structure for volunteer_signup
-- ----------------------------
DROP TABLE IF EXISTS `volunteer_signup`;
CREATE TABLE `volunteer_signup`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `volunteer_id` bigint(0) NOT NULL COMMENT '志愿活动ID',
  `member_id` bigint(0) NOT NULL COMMENT '党员ID',
  `member_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '党员姓名',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '已报名' COMMENT '状态：已报名/已完成',
  `service_hours_actual` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实际服务时长',
  `signup_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '报名时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_volunteer`(`volunteer_id`) USING BTREE,
  INDEX `idx_member`(`member_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '志愿活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of volunteer_signup
-- ----------------------------
INSERT INTO `volunteer_signup` VALUES (7, 4, 1, '张三', '已完成', '3', '2026-05-26 22:17:01');
INSERT INTO `volunteer_signup` VALUES (8, 4, 6, '吴思儒', '已完成', '3', '2026-05-26 22:30:13');
INSERT INTO `volunteer_signup` VALUES (9, 5, 6, '吴思儒', '已完成', '1', '2026-05-26 22:30:59');
INSERT INTO `volunteer_signup` VALUES (10, 6, 20, '笑笑', '已完成', '2', '2026-05-31 20:00:36');
INSERT INTO `volunteer_signup` VALUES (11, 6, 19, '吴迪', '已完成', '2', '2026-05-31 20:05:30');
INSERT INTO `volunteer_signup` VALUES (12, 6, 6, '吴思儒', '已完成', '2', '2026-05-31 20:05:42');
INSERT INTO `volunteer_signup` VALUES (13, 6, 1, '张三', '已完成', '2', '2026-05-31 20:34:13');
INSERT INTO `volunteer_signup` VALUES (14, 6, 0, '哈哈', '已完成', '2', '2026-05-31 20:34:19');
INSERT INTO `volunteer_signup` VALUES (15, 6, 0, '游客', '已完成', '2', '2026-05-31 20:54:12');
INSERT INTO `volunteer_signup` VALUES (16, 7, 6, '吴思儒', '已报名', NULL, '2026-05-31 21:23:34');
INSERT INTO `volunteer_signup` VALUES (17, 7, 23, '李小龙', '已报名', NULL, '2026-06-14 19:51:25');

SET FOREIGN_KEY_CHECKS = 1;
