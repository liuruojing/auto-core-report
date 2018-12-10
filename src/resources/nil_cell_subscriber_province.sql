/*
Navicat PGSQL Data Transfer

Source Server         : 192.168.6.15
Source Server Version : 90605
Source Host           : 192.168.6.15:5432
Source Database       : training_bigdata_liuruojing
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90605
File Encoding         : 65001

Date: 2018-12-10 18:20:16
*/


-- ----------------------------
-- Table structure for nil_cell_subscriber_province
-- ----------------------------
DROP TABLE IF EXISTS "public"."nil_cell_subscriber_province";
CREATE TABLE "public"."nil_cell_subscriber_province" (
"attribution_province_code" varchar COLLATE "default" NOT NULL,
"people_count" int8 NOT NULL,
"statics_time" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of nil_cell_subscriber_province
-- ----------------------------
INSERT INTO "public"."nil_cell_subscriber_province" VALUES ('001', '1000', '2018-12-10 16:41:15');
INSERT INTO "public"."nil_cell_subscriber_province" VALUES ('001', '1234', '2018-12-03 16:42:07');
INSERT INTO "public"."nil_cell_subscriber_province" VALUES ('002', '1200', '2018-12-09 16:41:28');
INSERT INTO "public"."nil_cell_subscriber_province" VALUES ('002', '2211', '2018-11-27 16:42:28');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
