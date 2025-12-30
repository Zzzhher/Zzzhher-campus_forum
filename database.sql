/*
 Navicat MySQL Data Transfer

 Source Server         : 本地测试环境
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : db_campus_forum  -- 目标数据库名

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 07/08/2023 00:03:19
*/

-- 核心新增：先创建数据库（如果不存在），避免1049错误
CREATE DATABASE IF NOT EXISTS db_campus_forum
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_0900_ai_ci;

-- 切换到新建的数据库
USE db_campus_forum;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS `db_account`; -- 已切换库，无需再加库名前缀
CREATE TABLE `db_account` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `username` varchar(255) DEFAULT NULL,
                              `email` varchar(255) DEFAULT NULL,
                              `password` varchar(255) DEFAULT NULL,
                              `role` varchar(255) DEFAULT NULL,
                              `register_time` datetime DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `unique_email` (`email`),
                              UNIQUE KEY `unique_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of db_account
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;