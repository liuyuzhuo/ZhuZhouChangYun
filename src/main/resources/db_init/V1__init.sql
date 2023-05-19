CREATE TABLE IF NOT EXISTS `t_user_message`  (
`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,-- COMMENT '主键',
`md5` TEXT NOT NULL ,-- COMMENT '业务主键',
`phone_num` TEXT NOT NULL ,-- COMMENT '手机号',
`car_num` TEXT NOT NULL ,-- COMMENT '车牌号',
`insurance_company` TEXT DEFAULT '' ,-- COMMENT '保险公司',
`insurance_expire_time` TEXT ,-- COMMENT '保险到期日期',
`longitude` REAL NOT NULL ,-- COMMENT '经度',
`latitude` REAL NOT NULL ,-- COMMENT '纬度',
`open_id` TEXT NOT NULL ,-- COMMENT '用户openid',
`address` TEXT DEFAULT '' ,-- COMMENT '地址',
`create_time` TEXT NOT NULL -- COMMENT '创建时间'
) ;

CREATE TABLE IF NOT EXISTS `Counters`  (
`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,-- COMMENT '主键',
`count` REAL NOT NULL DEFAULT 1,
`createdAt` TEXT  ,
`updatedAt` TEXT
) ;