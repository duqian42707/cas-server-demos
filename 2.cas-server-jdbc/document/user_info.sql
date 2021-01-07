/*
 Navicat Premium Data Transfer

 Source Server         : database-mysql
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : database-mysql:3306
 Source Schema         : cas

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 07/01/2021 21:26:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_id` int(11) NOT NULL COMMENT '主键',
  `nick_name` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '昵称',
  `username` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `password` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` VALUES (1, '管理员', 'admin', '111111');
INSERT INTO `user_info` VALUES (2, '张三', 'zhangsan', '111111_1234abcd');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
