
-- 给application表添加党员管理页面中的字段
ALTER TABLE application ADD COLUMN id_card VARCHAR(20) DEFAULT NULL COMMENT '身份证号' AFTER phone;
ALTER TABLE application ADD COLUMN student_no VARCHAR(50) DEFAULT NULL COMMENT '学号' AFTER id_card;
ALTER TABLE application ADD COLUMN class_name VARCHAR(100) DEFAULT NULL COMMENT '班级' AFTER student_no;
ALTER TABLE application ADD COLUMN email VARCHAR(100) DEFAULT NULL COMMENT '邮箱' AFTER class_name;
ALTER TABLE application ADD COLUMN native_place VARCHAR(100) DEFAULT NULL COMMENT '籍贯' AFTER email;
ALTER TABLE application ADD COLUMN education VARCHAR(20) DEFAULT NULL COMMENT '学历' AFTER native_place;
ALTER TABLE application ADD COLUMN political_status VARCHAR(20) DEFAULT NULL COMMENT '政治面貌' AFTER education;

-- 扩大birth_date列长度以存储ISO日期格式
ALTER TABLE application MODIFY COLUMN birth_date VARCHAR(50) DEFAULT NULL COMMENT '出生日期';
