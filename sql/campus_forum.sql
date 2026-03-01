/*
 Navicat Premium Dump SQL

 Source Server         : 本地Mysql服务器
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : campus_forum

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 01/03/2026 21:29:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS `db_account`;
CREATE TABLE `db_account`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `register_time` datetime NULL DEFAULT NULL,
  `mute` tinyint NULL DEFAULT NULL,
  `banned` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `unique_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_account
-- ----------------------------
INSERT INTO `db_account` VALUES (1, 'admin', '2142609137@qq.com', '$2a$10$rcCqO09i49s/WoWXEg/z6e6moy8SU7fmLm04/1k32cWjKZo5VLDnO', 'admin', '/avatar/5bd414566d0046359cc593ac88827227', '2026-02-25 19:05:56', 0, 0);
INSERT INTO `db_account` VALUES (2, 'user', '3176631261@qq.com', '$2a$10$f8OZHaio1YMsZq1ju3L5fOML9jCpyDiDP1Ki9HaSM7NiavjCzes.K', 'user', NULL, '2026-02-25 19:13:55', 0, 0);

-- ----------------------------
-- Table structure for db_account_details
-- ----------------------------
DROP TABLE IF EXISTS `db_account_details`;
CREATE TABLE `db_account_details`  (
  `id` int NOT NULL,
  `gender` tinyint NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `qq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `wx` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_account_details
-- ----------------------------
INSERT INTO `db_account_details` VALUES (1, 0, '18329503441', '2142609137', '18329503441', 'hello，world');
INSERT INTO `db_account_details` VALUES (2, 0, '17568453525', '3176631261', '17568453525', '我是萌新~~');

-- ----------------------------
-- Table structure for db_account_privacy
-- ----------------------------
DROP TABLE IF EXISTS `db_account_privacy`;
CREATE TABLE `db_account_privacy`  (
  `id` int NOT NULL,
  `phone` tinyint NULL DEFAULT NULL,
  `email` tinyint NULL DEFAULT NULL,
  `wx` tinyint NULL DEFAULT NULL,
  `qq` tinyint NULL DEFAULT NULL,
  `gender` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_account_privacy
-- ----------------------------
INSERT INTO `db_account_privacy` VALUES (1, 1, 1, 1, 1, 1);
INSERT INTO `db_account_privacy` VALUES (2, 1, 1, 1, 1, 1);

-- ----------------------------
-- Table structure for db_email_record
-- ----------------------------
DROP TABLE IF EXISTS `db_email_record`;
CREATE TABLE `db_email_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_email_record
-- ----------------------------
INSERT INTO `db_email_record` VALUES (1, '2142609137@qq.com', '欢迎注册我们的网站', '您的邮件注册验证码为: 935031，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。', '2026-02-25 19:05:38', 1);
INSERT INTO `db_email_record` VALUES (2, '3536154620@qq.com', '欢迎注册我们的网站', '您的邮件注册验证码为: 402704，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。', '2026-02-25 19:12:42', 1);
INSERT INTO `db_email_record` VALUES (3, '3176631261@qq.com', '欢迎注册我们的网站', '您的邮件注册验证码为: 466910，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。', '2026-02-25 19:13:43', 1);

-- ----------------------------
-- Table structure for db_image_store
-- ----------------------------
DROP TABLE IF EXISTS `db_image_store`;
CREATE TABLE `db_image_store`  (
  `uid` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_image_store
-- ----------------------------
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/eb06f0f761704ba784954096dfd3b03a', '2026-02-25 19:29:03');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/b0105208c2624a2da4f4accbf60b8615', '2026-02-25 19:59:08');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/59b965339f9f4174b7d21e2b1cfddac0', '2026-02-25 19:59:36');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/6ab33dcaeb174a0aa31c69dbe1c3910e', '2026-02-25 20:00:16');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/81ad6bf42ef64833874dc4dd94d97227', '2026-02-25 20:00:30');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/87fe50060cce448a95770ee773583fcf', '2026-02-25 20:00:48');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/6266652265a14130aeb06ee5e0d10b54', '2026-02-25 20:05:16');
INSERT INTO `db_image_store` VALUES (2, '/cache/20260225/e7a414df0ae84a0b9d3611707af13dee', '2026-02-25 20:28:54');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260225/b6827fef57554d95b861e2d058eaff05', '2026-02-25 20:42:36');
INSERT INTO `db_image_store` VALUES (2, '/cache/20260301/4b5d7f8634d447949928d93f0626c6cc', '2026-03-01 14:44:45');
INSERT INTO `db_image_store` VALUES (2, '/cache/20260301/eb2fce0674804739ab274deb5c686cef', '2026-03-01 14:51:40');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/405bc7629ffe426a891ac6f3473c3495', '2026-03-01 14:55:31');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/6a14672f8f9f408981ae77b89359843c', '2026-03-01 14:56:09');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/d9127661b59d4c3d959639aa628c6a62', '2026-03-01 14:59:54');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/81279e2588964b668ea0988a8ab356b9', '2026-03-01 15:00:14');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/a9cfb645cadc4eeeb2764a42650bd52f', '2026-03-01 17:50:04');
INSERT INTO `db_image_store` VALUES (1, '/cache/20260301/73fb3826cf714b56beda31517c86fa68', '2026-03-01 17:58:02');

-- ----------------------------
-- Table structure for db_notification
-- ----------------------------
DROP TABLE IF EXISTS `db_notification`;
CREATE TABLE `db_notification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_notification
-- ----------------------------
INSERT INTO `db_notification` VALUES (2, 1, '您有新的帖子回复', 'user 回复了你发表主题: 公益义卖市集温暖开市，快去看看吧！', 'success', '/index/topic-detail/8', '2026-03-01 19:14:02');
INSERT INTO `db_notification` VALUES (3, 2, '您有新的帖子评论回复', 'admin 回复了你发表的评论，快去看看吧！', 'success', '/index/topic-detail/8', '2026-03-01 20:27:21');
INSERT INTO `db_notification` VALUES (4, 1, '您有新的帖子评论回复', 'user 回复了你发表的评论，快去看看吧！', 'success', '/index/topic-detail/8', '2026-03-01 21:11:38');

-- ----------------------------
-- Table structure for db_topic
-- ----------------------------
DROP TABLE IF EXISTS `db_topic`;
CREATE TABLE `db_topic`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `uid` int NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  `top` tinyint NULL DEFAULT 0,
  `locked` tinyint NULL DEFAULT 0,
  `invisible` tinyint NULL DEFAULT 0,
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_topic
-- ----------------------------
INSERT INTO `db_topic` VALUES (1, '测试', '这是一条测试帖子\n', '{\"ops\":[{\"insert\":\"这是一条测试帖子\\n\"}]}', 1, 5, '2026-02-25 19:27:28', 1, 0, 0, 0);
INSERT INTO `db_topic` VALUES (2, '你好', '我是第一个用户\n', '{\"ops\":[{\"insert\":\"我是第一个用户\\n\"}]}', 1, 4, '2026-02-25 19:27:51', 0, 0, 0, 0);
INSERT INTO `db_topic` VALUES (3, '2026 年校园 AI 技术分享会', '本次活动将聚焦 AI 在自然语言处理与 Web 开发中的前沿应用，邀请行业专家与优秀学长分享实战经验，现场还会提供技术 demo 与一对一交流机会，助力同学们提升专业能力。\n活动时间：2026 年 2 月 26 日 17:00-22:00\n活动地点：学校学术报告厅 A 座 201\n主办方：计算机科学与技术学院学生会\n人数限制：不限\n      \n', '{\"ops\":[{\"insert\":\"本次活动将聚焦 AI 在自然语言处理与 Web 开发中的前沿应用，邀请行业专家与优秀学长分享实战经验，现场还会提供技术 demo 与一对一交流机会，助力同学们提升专业能力。\\n活动时间：2026 年 2 月 26 日 17:00-22:00\\n活动地点：学校学术报告厅 A 座 201\\n主办方：计算机科学与技术学院学生会\\n人数限制：不限\\n\"},{\"attributes\":{\"width\":\"173\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260225/b0105208c2624a2da4f4accbf60b8615\"}},{\"insert\":\"   \"},{\"attributes\":{\"width\":\"173\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260225/6ab33dcaeb174a0aa31c69dbe1c3910e\"}},{\"insert\":\"  \"},{\"attributes\":{\"width\":\"173\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260225/81ad6bf42ef64833874dc4dd94d97227\"}},{\"insert\":\" \\n\"}]}', 1, 1, '2026-02-25 20:01:23', 1, 0, 0, 0);
INSERT INTO `db_topic` VALUES (4, '学校二食堂三楼收银台旁捡到黑色皮质钱包一枚', '【失物招领】黑色皮质钱包一枚\n拾获时间：2026年3月10日 下午16:30左右\n拾获地点：学校二食堂三楼收银台旁\n物品描述：黑色短款皮质钱包，边缘有轻微磨损，正面有简约金属logo（无明显品牌标识），内部有现金若干、身份证1张（姓名首字为张）、银行卡2张（储蓄卡+信用卡），无其他贵重物品。\n认领说明：请失主看到后，携带本人有效证件（身份证/校园卡）前往二食堂三楼服务台认领，核对物品细节无误后即可领回。\n联系电话：13505656789（工作日12:00-14:00、17:00-19:00接听）\n温馨提示：请大家妥善保管个人物品，避免遗失带来不便，非常感谢大家的配合～\n\n', '{\"ops\":[{\"insert\":\"【失物招领】黑色皮质钱包一枚\\n拾获时间：2026年3月10日 下午16:30左右\\n拾获地点：学校二食堂三楼收银台旁\\n物品描述：黑色短款皮质钱包，边缘有轻微磨损，正面有简约金属logo（无明显品牌标识），内部有现金若干、身份证1张（姓名首字为张）、银行卡2张（储蓄卡+信用卡），无其他贵重物品。\\n认领说明：请失主看到后，携带本人有效证件（身份证/校园卡）前往二食堂三楼服务台认领，核对物品细节无误后即可领回。\\n联系电话：13505656789（工作日12:00-14:00、17:00-19:00接听）\\n温馨提示：请大家妥善保管个人物品，避免遗失带来不便，非常感谢大家的配合～\\n\"},{\"attributes\":{\"width\":\"501\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260225/e7a414df0ae84a0b9d3611707af13dee\"}},{\"insert\":\"\\n\"}]}', 2, 2, '2026-02-25 20:29:11', 1, 0, 0, 0);
INSERT INTO `db_topic` VALUES (5, 'To 周五下午图书馆三楼靠窗位置的白衬衫男生', '上周三下午四点在图书馆，你帮我捡起了散落在地上的专业课本，还笑着帮我拍掉了封面上的灰尘。你身上带着淡淡的洗衣液香味，说话的时候很温柔。之后的几天我都有在三楼等你，但是都没有遇见你，不知道你能不能看到这条。如果有机会的话，可以认识一下吗？\n', '{\"ops\":[{\"insert\":\"上周三下午四点在图书馆，你帮我捡起了散落在地上的专业课本，还笑着帮我拍掉了封面上的灰尘。你身上带着淡淡的洗衣液香味，说话的时候很温柔。之后的几天我都有在三楼等你，但是都没有遇见你，不知道你能不能看到这条。如果有机会的话，可以认识一下吗？\"},{\"attributes\":{\"width\":\"434\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260225/b6827fef57554d95b861e2d058eaff05\"}},{\"insert\":\"\\n\"}]}', 1, 3, '2026-02-25 20:42:56', 0, 0, 0, 0);
INSERT INTO `db_topic` VALUES (6, '致每天早八坐第三排的白衬衫同学', '连续两周了，我都在高数课上悄悄看你。你总是提前 5 分钟到，会帮同桌占座，记笔记的字迹工整得像印刷体。今天鼓起勇气在表白墙捞一下，如果你看到这条，明天能不能把你的微积分笔记借我抄一下？或者，我们可以一起去图书馆复习？\n', '{\"ops\":[{\"attributes\":{\"italic\":true},\"insert\":\"连续两周了，我都在高数课上悄悄看你。你总是提前 5 分钟到，会帮同桌占座，记笔记的字迹工整得像印刷体。今天鼓起勇气在表白墙捞一下，如果你看到这条，明天能不能把你的微积分笔记借我抄一下？或者，我们可以一起去图书馆复习？\"},{\"attributes\":{\"width\":\"595\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260301/4b5d7f8634d447949928d93f0626c6cc\"}},{\"insert\":\"\\n\"}]}', 2, 3, '2026-03-01 14:44:56', 0, 0, 0, 0);
INSERT INTO `db_topic` VALUES (7, '急寻蓝色校园卡！', '今天下午在图书馆三楼自习室丢失了一张蓝色校园卡，卡号后四位是1234，卡套是卡通小熊图案。里面还有门禁和少量余额，对我非常重要！如果有同学捡到，请联系我：13812345678，万分感谢！\n', '{\"ops\":[{\"insert\":\"今天下午在图书馆三楼自习室丢失了一张蓝色校园卡，卡号后四位是1234，卡套是卡通小熊图案。里面还有门禁和少量余额，对我非常重要！如果有同学捡到，请联系我：13812345678，万分感谢！\"},{\"attributes\":{\"width\":\"250\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260301/eb2fce0674804739ab274deb5c686cef\"}},{\"insert\":\"\\n\"}]}', 2, 2, '2026-03-01 14:51:48', 0, 0, 0, 0);
INSERT INTO `db_topic` VALUES (8, '公益义卖市集温暖开市', '闲置的书籍、可爱的手作、珍藏的周边，都可以变成爱心的桥梁。本周六上午十点，食堂前广场，所有义卖所得将全部捐赠给山区小学，期待你的参与。\n', '{\"ops\":[{\"insert\":\"闲置的书籍、可爱的手作、珍藏的周边，都可以变成爱心的桥梁。本周六上午十点，食堂前广场，所有义卖所得将全部捐赠给山区小学，期待你的参与。\"},{\"attributes\":{\"width\":\"487\"},\"insert\":{\"image\":\"http://localhost:8080/images/cache/20260301/81279e2588964b668ea0988a8ab356b9\"}},{\"insert\":\"\\n\"}]}', 1, 1, '2026-03-01 15:00:31', 0, 0, 0, 0);

-- ----------------------------
-- Table structure for db_topic_comment
-- ----------------------------
DROP TABLE IF EXISTS `db_topic_comment`;
CREATE TABLE `db_topic_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` int NULL DEFAULT NULL,
  `tid` int NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `time` datetime NULL DEFAULT NULL,
  `quote` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_topic_comment
-- ----------------------------
INSERT INTO `db_topic_comment` VALUES (5, 1, 5, '{\"ops\":[{\"insert\":\"好帅啊\\n\\n\"}]}', '2026-03-01 18:15:21', -1, 0);
INSERT INTO `db_topic_comment` VALUES (6, 1, 5, '{\"ops\":[{\"insert\":\"你也好帅啊\\n\\n\"}]}', '2026-03-01 18:16:01', 5, 0);
INSERT INTO `db_topic_comment` VALUES (7, 2, 8, '{\"ops\":[{\"insert\":\"太赞了，我也要贡献自己的一份力量！！！\\n\"}]}', '2026-03-01 19:13:57', -1, 0);
INSERT INTO `db_topic_comment` VALUES (8, 1, 8, '{\"ops\":[{\"insert\":\"只要人人献出一点爱，世界就变得更加美好\\n\\n\"}]}', '2026-03-01 20:27:21', 7, 0);
INSERT INTO `db_topic_comment` VALUES (9, 2, 8, '{\"ops\":[{\"insert\":\"是的，我们青年学生更要担当起来\\n\"}]}', '2026-03-01 21:11:36', 8, 0);
INSERT INTO `db_topic_comment` VALUES (10, 1, 8, '{\"ops\":[{\"insert\":\"傻逼\\n\"}]}', '2026-03-01 21:14:11', -1, 2);

-- ----------------------------
-- Table structure for db_topic_interact_collect
-- ----------------------------
DROP TABLE IF EXISTS `db_topic_interact_collect`;
CREATE TABLE `db_topic_interact_collect`  (
  `tid` int NULL DEFAULT NULL,
  `uid` int NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  UNIQUE INDEX `tid_uid_collect`(`tid` ASC, `uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_topic_interact_collect
-- ----------------------------
INSERT INTO `db_topic_interact_collect` VALUES (3, 1, '2026-02-25 20:37:51');
INSERT INTO `db_topic_interact_collect` VALUES (8, 1, '2026-03-01 15:22:10');

-- ----------------------------
-- Table structure for db_topic_interact_like
-- ----------------------------
DROP TABLE IF EXISTS `db_topic_interact_like`;
CREATE TABLE `db_topic_interact_like`  (
  `tid` int NULL DEFAULT NULL,
  `uid` int NULL DEFAULT NULL,
  `time` datetime NULL DEFAULT NULL,
  UNIQUE INDEX `tid_uid_like`(`tid` ASC, `uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_topic_interact_like
-- ----------------------------
INSERT INTO `db_topic_interact_like` VALUES (3, 1, '2026-02-25 20:37:50');
INSERT INTO `db_topic_interact_like` VALUES (8, 1, '2026-03-01 15:22:09');

-- ----------------------------
-- Table structure for db_topic_type
-- ----------------------------
DROP TABLE IF EXISTS `db_topic_type`;
CREATE TABLE `db_topic_type`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of db_topic_type
-- ----------------------------
INSERT INTO `db_topic_type` VALUES (1, '校园活动', '本栏目用于发布高校内具有组织性、公开性的各类活动。重点收录学术论坛、技术分享会、学科竞赛、校企宣讲会等与学生成长紧密相关的内容，同时支持文体活动发布，助力营造丰富多元的校园文化氛围。', '#DE4F4F');
INSERT INTO `db_topic_type` VALUES (2, '失物招领', '本板块聚焦校园内物品遗失与归还场景，师生可发布寻物启事或失物招领帖，通过清晰的物品描述、时间地点信息，快速匹配供需，减少物品丢失带来的不便，共建互助友爱的校园环境。', '#D2DE4F');
INSERT INTO `db_topic_type` VALUES (3, '表白墙', '本板块是校园里的情感树洞与表白阵地，同学们可以在这里勇敢表达爱意、送上祝福，或分享生活中的小确幸与小烦恼。它不仅是表白的平台，更是连接彼此、传递温暖的校园纽带。', '#B14FDE');
INSERT INTO `db_topic_type` VALUES (4, '日常闲聊', '本板块是校园里的轻松闲聊阵地，同学们可在此分享校园新鲜事、讨论热门话题、约饭组队或交流生活点滴。无特定主题限制，主打轻松随性，是师生日常互动、拉近距离的核心区域。', '#86DE4F');
INSERT INTO `db_topic_type` VALUES (5, '问题反馈', '本板块是校园平台的用户反馈中心，同学们在使用过程中遇到任何问题（如页面报错、功能异常、内容审核等），都可以在此提交。你的每一条反馈，都是我们改进服务、打造更好校园社区的重要依据。', '#5E5CDE');

SET FOREIGN_KEY_CHECKS = 1;
