/*
    MySQL Documentation
    http://dev.mysql.com/doc/refman/5.7/en/create-table.html
    http://dev.mysql.com/doc/refman/5.7/en/integer-types.html
*/

CREATE SCHEMA `scc` DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`branch` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `branchType` TINYINT,
  `name` VARCHAR(45) NULL,
  `tel` VARCHAR(45) NULL,
  `telEtc` VARCHAR(45) NULL,
  `postcode` VARCHAR(6) NULL,
  `address1` VARCHAR(45) NULL,
  `address2` VARCHAR(45) NULL,
  `hpUrl` VARCHAR(255) NULL,
  `locNote` TEXT,
  `locUrl` VARCHAR(255) NULL,
  `opNote` TEXT,
  `weekdayOpen` TEXT,
  `weekendOpen` TEXT,
  `openDt` DATE NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `visibleYn` TINYINT NULL DEFAULT 1,
  `reservationYn` TINYINT NULL DEFAULT 1,
  `singleYn` TINYINT NULL DEFAULT 1,
  `multiYn` TINYINT NULL DEFAULT 1,
  `privateYn` TINYINT NULL DEFAULT 1,
  `fingerprintYn` TINYINT DEFAULT 0,
  `standYn` TINYINT DEFAULT 0,
  `areaType` INT(11) NULL DEFAULT 0,
  `sender_key` VARCHAR(40) NULL,
  `sender_UUID` VARCHAR(40) NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC),
  INDEX `IDX_02` (`name` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch` ADD column `branchType` TINYINT DEFAULT 10 AFTER `branchId`;
ALTER TABLE `scc`.`branch` ADD column `visibleYn` TINYINT DEFAULT 1 AFTER `useYn`;
ALTER TABLE `scc`.`branch` ADD column `openDt` DATE NULL AFTER `opNote`;
ALTER TABLE `scc`.`branch` ADD column `sender_key` VARCHAR(40) NULL AFTER `visibleYn`;	
ALTER TABLE `scc`.`branch` ADD column `sender_UUID` VARCHAR(40) NULL AFTER `sender_key`;
ALTER TABLE `scc`.`branch` ADD column `reservationYn` TINYINT NULL DEFAULT 1 AFTER `visibleYn`;
ALTER TABLE `scc`.`branch` ADD column `singleYn` TINYINT NULL DEFAULT 1 AFTER `reservationYn`;	
ALTER TABLE `scc`.`branch` ADD column `multiYn` TINYINT NULL DEFAULT 1 AFTER `singleYn`;	
ALTER TABLE `scc`.`branch` ADD column `privateYn` TINYINT NULL DEFAULT 1 AFTER `multiYn`;		
ALTER TABLE `scc`.`branch` ADD column `weekdayOpen` TEXT AFTER `opNote`;
ALTER TABLE `scc`.`branch` ADD column `weekendOpen` TEXT AFTER `weekdayOpen`;
ALTER TABLE `scc`.`branch` ADD column `areaType` INT(11) NULL DEFAULT 0 AFTER `privateYn`;	
ALTER TABLE `scc`.`branch` ADD column `ip` VARCHAR(40) NULL AFTER `openDt`;
ALTER TABLE `scc`.`branch` ADD column `fingerprintYn` TINYINT DEFAULT 0 AFTER `privateYn`;
ALTER TABLE `scc`.`branch` ADD column `standYn` TINYINT DEFAULT 0 AFTER `fingerprintYn`;

CREATE TABLE `scc`.`branch_ad_or_banner` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `adOrNoticeType` TINYINT,
  `title` VARCHAR(255) NULL,
  `subtitle` TEXT NULL,
  `imgUrl` VARCHAR(255) NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `visibleYn` TINYINT NULL DEFAULT 1,
  `startDt` DATE NULL,
  `startTm` TIME NULL,
  `endDt` DATE NULL,
  `endTm` TIME NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  INDEX `IDX_01` (`branchId`, `adOrNoticeType` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`branch_manager` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `userId` VARCHAR(45) NULL,
  `branchId` VARCHAR(45) NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`userId` ASC, `branchId` ASC),
  INDEX `IDX_02` (`branchId` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`branch_room` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `roomId` VARCHAR(45) NULL,
  `name` VARCHAR(45) NULL,
  `roomType` TINYINT NULL DEFAULT 10,
  `t` SMALLINT NULL,
  `l` SMALLINT NULL,
  `h` SMALLINT NULL,
  `w` SMALLINT NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `roomNote` TEXT,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `roomId` ASC),
  INDEX `IDX_02` (`name` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_room` ADD column `roomNote` TEXT AFTER `useYn`;
ALTER TABLE `scc`.`branch_room` ADD column `roomType` TINYINT DEFAULT 10 AFTER `name`;
	
CREATE TABLE `scc`.`branch_desk` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `roomId` VARCHAR(45) NULL,
  `deskId` VARCHAR(45) NULL,
  `name` VARCHAR(45) NULL,
  `deskMax` INT DEFAULT 1,
  `deskType` TINYINT NULL DEFAULT 10,
  `t` SMALLINT NULL,
  `l` SMALLINT NULL,
  `h` SMALLINT NULL,
  `w` SMALLINT NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `deskNote` TEXT,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `deskId` ASC),
  INDEX `IDX_02` (`name` ASC),
  INDEX `IDX_03` (`branchId` ASC, `roomId` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_desk` 
	ADD column `deskMax` INT DEFAULT 1 AFTER `name`;
ALTER TABLE `scc`.`branch_desk`
	ADD column `deskNote` TEXT AFTER `useYn`;
ALTER TABLE `scc`.`branch_desk`
	ADD column `deskType` TINYINT DEFAULT 10 AFTER `deskMax`;	


CREATE TABLE `scc`.`branch_member_entry` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `entryDt` DATETIME NULL DEFAULT NOW(),
  `entryType` TINYINT NULL DEFAULT 1,
  `reservationId` VARCHAR(45) NULL,
  `deskId` VARCHAR(45) NULL,
  `businessDt` DATE NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  INDEX `IDX_01` (`branchId` ASC, `memberId` ASC, `entryDt` ASC),
  INDEX `IDX_02` (`branchId` ASC, `entryDt` ASC),
  INDEX `IDX_03` (`branchId` ASC, `reservationId` ASC),
  INDEX `IDX_04` (`branchId` ASC, `deskId` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_member_entry` ADD column `businessDt` DATE NULL AFTER `deskId`;


CREATE TABLE `scc`.`branch_member` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `no` VARCHAR(20) NULL,
  `name` VARCHAR(45) NULL,
  `bfTel` VARCHAR(45) NULL,
  `tel` VARCHAR(45) NULL,
  `telParent` VARCHAR(45) NULL,
  `telEtc` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `school` VARCHAR(45) NULL,
  `schoolGrade` TINYINT NULL DEFAULT -1,
  `examType` TINYINT NULL DEFAULT -1,
  `gender` TINYINT NULL DEFAULT -1,
  `birthDt` VARCHAR(12) NULL,
  `postcode` VARCHAR(6) NULL,
  `address1` VARCHAR(45) NULL,
  `address2` VARCHAR(45) NULL,
  `addressDetail` VARCHAR(45) NULL,
  `membershipNo` VARCHAR(255) NULL,
  `jobType` INT(20) NULL DEFAULT 0,
  `interestType` INT(20) NULL DEFAULT 0,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  `memberNote` TEXT NULL,
  `cabinet` VARCHAR(45) NULL,
  `checkoutYn` TINYINT NULL DEFAULT 0,
  `appNo` VARCHAR(20) NULL,
  `expireYn` TINYINT NULL DEFAULT 0,
  `timeYn` TINYINT NULL DEFAULT 0,
  `remainTime` VARCHAR(45) NULL DEFAULT 0
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `memberId` ASC),
  INDEX `IDX_02` (`branchId` ASC, `no` ASC),
  INDEX `IDX_03` (`branchId` ASC, `name` ASC)
) DEFAULT CHARACTER SET utf8;

-- 
-- ALTER TABLE `scc`.`branch_member` ADD COLUMN `memberNote` TEXT NULL;
-- ALTER TABLE `scc`.`branch_member` ADD COLUMN `cabinet` VARCHAR(45) NULL;

ALTER TABLE `scc`.`branch_member` ADD COLUMN `timeYn` TINYINT NULL DEFAULT 0;
ALTER TABLE `scc`.`branch_member` ADD COLUMN `remainTime` VARCHAR(45) NULL DEFAULT 0;
-- ALTER TABLE `scc`.`branch_member` ADD COLUMN `membershipNo` VARCHAR(255) NULL AFTER `address2`,
--    							  ADD UNIQUE INDEX `IDX_04` (`membershipNo` ASC);
ALTER TABLE `scc`.`branch_member` ADD COLUMN `examType` TINYINT NULL DEFAULT -1 AFTER `gender`;
ALTER TABLE `scc`.`branch_member` ADD COLUMN `bfTel` VARCHAR(45) NULL AFTER `name`;	
ALTER TABLE `scc`.`branch_member` ADD COLUMN `jobType` INT(20) NULL DEFAULT 0 AFTER `membershipNo`;
ALTER TABLE `scc`.`branch_member` ADD COLUMN `interestType` INT(20) NULL DEFAULT 0  AFTER `jobType`;    
ALTER TABLE `scc`.`branch_member` ADD COLUMN `addressDetail` VARCHAR(255) NULL AFTER `address2`;
ALTER TABLE `scc`.`branch_member` ADD COLUMN `addressType` INT(20) NULL AFTER `addressDetail`;    
ALTER TABLE `scc`.`branch_member` ADD COLUMN `appNo` VARCHAR(20) NULL;
ALTER TABLE `scc`.`branch_member` ADD COLUMN `expireYn` TINYINT NULL DEFAULT 0;

CREATE TABLE `scc`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `userId` VARCHAR(45) NULL,
  `name` VARCHAR(45) NULL,
  `password` VARCHAR(255) NULL,
  `role` VARCHAR(45) NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`userId` ASC),
  UNIQUE INDEX `IDX_02` (`name` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`branch_order` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `orderId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `orderDt` DATE NULL,
  `orderTm` TIME NULL,
  `orderNote` TEXT,
  `orderStatus` INT NULL DEFAULT -1,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `orderId` ASC),
  INDEX `IDX_02` (`branchId` ASC, `memberId` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_order` ADD COLUMN `orderDt` DATE NULL DEFAULT NULL AFTER `memberId`;
ALTER TABLE `scc`.`branch_order` ADD COLUMN `orderTm` TIME NULL DEFAULT NULL AFTER `orderDt`;


CREATE TABLE `scc`.`branch_reservation` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `orderId` VARCHAR(45) NULL,
  `reservationId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `deskId` VARCHAR(45) NULL,
  `reservationNum` INT DEFAULT 1,
  `deskStartDt` DATE NOT NULL,
  `deskStartTm` TIME NOT NULL,
  `deskEndDt` DATE NOT NULL,
  `deskEndTm` TIME NOT NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `reservationDt` DATE NULL,
  `reservationTm` TIME NULL,
  `reservationNote` TEXT,
  `reservationStatus` INT NULL DEFAULT -1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  `checkoutYn` TINYINT NULL DEFAULT 0
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `orderId` ASC, `reservationId` ASC),
  INDEX `IDX_02` (`branchId` ASC, `reservationId` ASC),
  INDEX `IDX_03` (`branchId` ASC, `deskId` ASC),
  INDEX `IDX_04` (`branchId` ASC, `deskStartDt` ASC, `deskEndDt` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_reservation` ADD COLUMN `reservationNum` INT DEFAULT 1 AFTER `deskId`;
ALTER TABLE `scc`.`branch_reservation`
    ADD COLUMN `orderId` VARCHAR(45) NULL DEFAULT NULL AFTER `branchId`,
    DROP INDEX `IDX_01` ,
    ADD UNIQUE INDEX `IDX_01` (`branchId` ASC, `orderId` ASC, `reservationId` ASC),
    DROP INDEX `IDX_02` ,
    ADD INDEX `IDX_02` (`branchId` ASC, `reservationId` ASC),
    CHANGE COLUMN `deskStartDt` `deskStartDt` DATE NOT NULL,
    CHANGE COLUMN `deskEndDt` `deskEndDt` DATE NOT NULL,
    ADD COLUMN `deskStartTm` TIME NULL AFTER `deskStartDt`,
    ADD COLUMN `deskEndTm` TIME NULL AFTER `deskEndDt`,
    ADD COLUMN `reservationDt` DATE NULL DEFAULT NULL AFTER `useYn`,
    ADD COLUMN `reservationTm` TIME NULL DEFAULT NULL AFTER `reservationDt`,
    ADD COLUMN `reservationStatus` INT NULL DEFAULT -1 AFTER `reservationNote`;	
ALTER TABLE `scc`.`branch_reservation` ADD COLUMN `checkoutYn` TINYINT NULL DEFAULT 0;

CREATE TABLE `scc`.`branch_pay` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `orderId` VARCHAR(45) NULL,
  `reservationId` VARCHAR(45) NULL,  
  `payId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `payDt` DATE NULL,
  `payTm` TIME NULL,  
  `payInOutType` TINYINT DEFAULT 20,
  `payType` TINYINT DEFAULT 0,
  `payStateType` TINYINT NOT NULL DEFAULT 10,
  `payAmount` INT DEFAULT 0,  
  `useYn` TINYINT NULL DEFAULT 1,    
  `payNote` TEXT,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `orderId` ASC, `payId` ASC),
  INDEX `IDX_03` (`branchId` ASC, `payDt` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_pay` ADD COLUMN `payStateType` TINYINT NOT NULL DEFAULT 10 AFTER `payType`;
ALTER TABLE `scc`.`branch_pay` ADD COLUMN `payInOutType` TINYINT NOT NULL DEFAULT 20 AFTER `payTm`;    
ALTER TABLE `scc`.`branch_pay` ADD COLUMN `reservationId` VARCHAR(45) NULL AFTER `orderId`;
ALTER TABLE `scc`.`branch_pay` ADD COLUMN `cancelDt` NULL DEFAULT NOW();

	    
    
CREATE TABLE `scc`.`branch_rental` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(50) NOT NULL,
  `rentalId` VARCHAR(45) NOT NULL,
  `rentalTagId` VARCHAR(50) NOT NULL,
  `memberId` VARCHAR(45) NULL,
  `rentalType` VARCHAR(50) NOT NULL,
  `rentalStateType` TINYINT NOT NULL DEFAULT 0,
  `useYn` TINYINT NOT NULL DEFAULT 1,    
  `rentalNote` TEXT,
  `rentalDt` DATE NULL,
  `rentalTm` TIME NULL,  
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `rentalId` ASC, `rentalTagId` ASC),
  INDEX `IDX_03` (`branchId` ASC, `insertDt` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_rental` CHANGE `rentalTagId` `goodsId` VARCHAR(50) NOT NULL;
ALTER TABLE `scc`.`branch_rental` DROP `rentalType`;

CREATE TABLE `scc`.`branch_pay` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `orderId` VARCHAR(45) NULL,
  `payId` VARCHAR(45) NULL,
  `memberId` VARCHAR(45) NULL,
  `payDt` DATE NULL,
  `payTm` TIME NULL,  
  `payInOutType` TINYINT DEFAULT 20,
  `payType` TINYINT DEFAULT 0,
  `payStateType` TINYINT NOT NULL DEFAULT 10,
  `payAmount` INT DEFAULT 0,  
  `useYn` TINYINT NULL DEFAULT 1,    
  `payNote` TEXT,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `orderId` ASC, `payId` ASC),
  INDEX `IDX_03` (`branchId` ASC, `payDt` ASC)
) DEFAULT CHARACTER SET utf8;

CREATE TABLE `scc`.`branch_expense` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `expenseId` VARCHAR(45) NULL,
  `expenseDt` DATE NULL,
  `expenseTm` TIME NULL,
  `expenseOption` INT NULL,
  `expenseGroup` INT NULL,
  `payInOutType` TINYINT DEFAULT 10,
  `payType` TINYINT DEFAULT 0,
  `expenseAmount` INT DEFAULT 0,
  `useYn` TINYINT NULL DEFAULT 1,
  `expenseNote` TEXT,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  `insertId` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `expenseId` ASC),
  INDEX `IDX_03` (`branchId` ASC, `expenseDt` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_expense` ADD COLUMN `payStateType` TINYINT NOT NULL DEFAULT 10 AFTER `payType`;
ALTER TABLE `scc`.`branch_expense` ADD COLUMN `expenseOption` int  NULL AFTER `expenseTm`;
ALTER TABLE `scc`.`branch_expense` ADD COLUMN `expenseGroup` int  NULL AFTER `expenseOption`;
ALTER TABLE `scc`.`branch_expense` ADD COLUMN `cancelDt` DATE NULL;
ALTER TABLE `scc`.`branch_expense` ADD COLUMN `cancelTm` TIME NULL;

CREATE TABLE `scc`.`goods`(
	`id` INT NOT NULL AUTO_INCREMENT,
	`goodsId` VARCHAR(45) NOT NULL,
	`branchId` VARCHAR(45) NOT NULL,
	`rentalType` VARCHAR(50) NOT NULL,
	`goodsNote` TEXT,
	`useYn` TINYINT DEFAULT '1',
	`insertDt` DATETIME NULL DEFAULT NOW(),
	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY(`id`)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`goods`
	ADD COLUMN `goodsId` VARCHAR(45) NOT NULL AFTER `id`;
ALTER TABLE `scc`.`goods`
	ADD COLUMN `useYn` TINYINT DEFAULT '1' AFTER `goodsNote`;
	
CREATE TABLE `scc`.`history` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `insertId` VARCHAR(45) NULL,
  `type` TINYINT NOT NULL,
  `memberId` VARCHAR(45) NULL,
  `orderId` VARCHAR(45) NULL,
  `reservationId` VARCHAR(45) NULL,
  `payId` VARCHAR(45) NULL,
  `roomId` VARCHAR(45) NULL,
  `deskId` VARCHAR(45) NULL,
  `userId` VARCHAR(45) NULL,
  `d` TEXT,
  PRIMARY KEY (`id`),
  INDEX `IDX_01` (`branchId` ASC, `insertDt` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`history`
    ADD COLUMN `orderId` VARCHAR(45) NULL DEFAULT NULL AFTER `memberId`;

    

CREATE TABLE `scc`.`history_sms` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NOT NULL,
  `smsDt` DATETIME NOT NULL,
  `userId` VARCHAR(45) NOT NULL,
  `fromNumber` VARCHAR(45) NOT NULL,
  `toNumber` VARCHAR(45) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `resultCode` varchar(5) NULL,
  `cmid` varchar(30) NULL,
  `reportCode` varchar(5) NULL,
  `umid` varchar(30) NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  `insertId` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_01` (`branchId`,`smsDt`,`userId`),
  INDEX `IDX_02` (`cmid` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`history_sms`
    ADD INDEX `IDX_02` (`cmid` ASC);


CREATE TABLE `scc`.`calendar` (
    `id` INT NOT NULL,
    `startDt` DATETIME NOT NULL,
    `endDt` DATETIME NOT NULL,
    `y` SMALLINT NOT NULL,
    `m` TINYINT NOT NULL,
    `d` TINYINT NOT NULL,
    `w` TINYINT NOT NULL,
    `holidayYn` TINYINT NULL DEFAULT 0,
    `calendarNote` TEXT,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `IDX_01` (startDt),
    UNIQUE INDEX `IDX_02` (y, m, d)
) DEFAULT CHARACTER SET utf8;

CREATE TABLE scc.branch_pre_reservation_setup(
 `branchId` VARCHAR(45) NOT NULL,
 `openDt` DATETIME,
 `useYn` TINYINT DEFAULT '1',
 `insertDt` DATETIME DEFAULT NOW(),
 `updateDt` DATETIME DEFAULT NOW(),
 CONSTRAINT pk_branch_pre_reservation_setup PRIMARY KEY (`branchId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='사전 예약 기능 사용 유무 등록 테이블';

CREATE TABLE scc.branch_pre_reservation(
 `id` INT NOT NULL AUTO_INCREMENT,
 `branchId` VARCHAR(45) NOT NULL,
 `preReservationId` VARCHAR(45) NOT NULL,
 `period` TINYINT DEFAULT '1' COMMENT '사용 기간(월)',
 `gender` TINYINT NULL DEFAULT -1 COMMENT '1:남자, 2:여자',
 `phone` VARCHAR(45) COMMENT '전화번호',
 `name` VARCHAR(45) COMMENT '이름',
 `email` VARCHAR(45) COMMENT '이메일',
 `birth` VARCHAR(12) COMMENT '생년월일',
 `memo` TEXT COMMENT '메모',
 `useYn` TINYINT DEFAULT '1',
 `insertDt` DATETIME DEFAULT NOW(),
 `updateDt` DATETIME DEFAULT NOW(),
 CONSTRAINT pk_branch_pre_reservation PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='사전 예약 데이터 등록';

ALTER TABLE `scc`.`branch_pre_reservation`
    ADD COLUMN `preReservationId` VARCHAR(45) NULL DEFAULT NULL AFTER `branchId`;

ALTER TABLE `scc`.`branch_pre_reservation`
    ADD COLUMN `useYn` TINYINT DEFAULT '1' NULL AFTER `memo`;

CREATE TABLE `scc`.`free_application` (
	`id` INT NOT NULL AUTO_INCREMENT,
    `branchId` VARCHAR(45) NULL,
	`freeApplicationId` VARCHAR(45) NULL,
    `startDt` DATE NOT NULL,
	`roomType` TINYINT NULL DEFAULT 10,
    `name` VARCHAR(45) NULL,
    `gender` TINYINT NULL DEFAULT 10,
    `birthDt` VARCHAR(12) NULL,
    `tel` VARCHAR(45) NULL,
    `school` VARCHAR(45) NULL,
  	`email` VARCHAR(45) NULL,
  	`cmpRoute` TINYINT NULL DEFAULT 10,
  	`useYn` TINYINT NULL DEFAULT 1,  
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`free_application` ADD COLUMN `roomType` TINYINT NOT NULL DEFAULT 10 AFTER `startDt`;
ALTER TABLE `scc`.`free_application` ADD COLUMN  `school` VARCHAR(45) NULL AFTER `tel`;
ALTER TABLE `scc`.`free_application` CHANGE COLUMN `cmpRoute` `cmpRoute` TINYINT NULL DEFAULT 10;
ALTER TABLE `scc`.`free_application` ADD COLUMN `freeApplicationId` VARCHAR(45) NULL AFTER `branchId`;
    
CREATE TABLE `scc`.`MembershipCard` (
	`id` INT NOT NULL AUTO_INCREMENT,
    `membershipId` VARCHAR(45) NULL,	
    `no` VARCHAR(20) NULL,
    `memberId` VARCHAR(45) NULL,
    `registerYn` TINYINT NULL DEFAULT 1,
    `registerBranchId` VARCHAR(45) NULL,
    `registerUserId` VARCHAR(45) NULL,
    `registerDt` DATE NOT NULL,    
  	`useYn` TINYINT NULL DEFAULT 1,  
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;    

CREATE TABLE `scc`.`branch_learning_time` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`branchId` VARCHAR(45) NOT NULL,
  	`memberId` VARCHAR(45) NOT NULL,
	`no` VARCHAR(20) NOT NULL,
	`role` TINYINT NULL,
	`startDt` DATE NULL,
	`endDt` DATE NULL,
	`learningTime` TIME NULL,
	`studyStatus` TINYINT NULL,
	`goalTime` INT(12) NULL,
	`useYn` TINYINT NOT NULL,
	`insertDt` DATETIME NULL DEFAULT NOW(),
	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id`),
	UNIQUE INDEX `IDX_01` (`id`, `branchId`, `memberId` asc )
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`branch_learning_time` DROP COLUMN `learningTimeId`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `memberId` VARCHAR(45) NULL AFTER `branchId`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `no` VARCHAR(20) NULL AFTER `memberId`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `role` TINYINT NULL AFTER `no`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `start` DATE NULL AFTER `role`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `endDt` DATE NULL AFTER `startDt`;
ALTER TABLE `scc`.`branch_learning_time` CHANGE `second` `learningTime`  VARCHAR(12) NULL;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `studyStatus`  TINYINT NULL AFTER `learningTime`;
ALTER TABLE `scc`.`branch_learning_time` ADD COLUMN `goalTime`  INT(12) NULL AFTER `studyStatus`;



CREATE TABLE `scc`.`alimtalk_template` (  
  `id` INT NOT NULL AUTO_INCREMENT,
  `template_type` VARCHAR(45) NOT NULL,
  `template_cd` VARCHAR(30) NOT NULL,
  `template_nm` VARCHAR(30) NULL,
  `template_message` VARCHAR(1000) NOT NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;

CREATE TABLE `scc`.`branch_auth_info` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `branchId` VARCHAR(45) NOT NULL,
  `arvApplyId` VARCHAR(45) NOT NULL,
  `authChannelCd` VARCHAR(30) NULL,  
  `authChannelDesc` VARCHAR(30) NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;

/*
    https://gist.github.com/bryhal/4129042
    http://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html#function_date-format
*/
DROP PROCEDURE IF EXISTS fill_calendar;
DELIMITER //
CREATE PROCEDURE fill_calendar(IN startdate DATE, IN stopdate DATE)
BEGIN
    DECLARE currentdate DATE;
    SET currentdate = startdate;
    WHILE currentdate <= stopdate DO
        INSERT INTO `scc`.`calendar` VALUES (
                        YEAR(currentdate)*10000 + MONTH(currentdate)*100 + DAY(currentdate),
                        currentdate,
                        ADDDATE(ADDDATE(currentdate, INTERVAL 1 DAY), INTERVAL -1 SECOND),
                        YEAR(currentdate),
                        MONTH(currentdate),
                        DAY(currentdate),
                        DAYOFWEEK(currentdate),
                        0,
                        '');
        SET currentdate = ADDDATE(currentdate, INTERVAL 1 DAY);
    END WHILE;
END
//
DELIMITER ;

TRUNCATE TABLE `scc`.`calendar`;
CALL fill_calendar('2016-01-01','2020-12-31');

CREATE TABLE `scc`.`sms_approve` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`branchId` VARCHAR(45) NULL,
    `memberId` VARCHAR(45) NULL,
    `no` VARCHAR(20) NULL,
	`enterexitYes` TINYINT NULL DEFAULT -1,
    `smsYes` TINYINT NULL DEFAULT -1,
    `promotionYes` TINYINT NULL DEFAULT 0,
    `utilYn` TINYINT NULL DEFAULT -1,
    `useYn` TINYINT NULL DEFAULT 1,
    `personalYn` TINYINT NULL DEFAULT -1,
    `mandatoryYn` TINYINT NULL DEFAULT -1,
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`sms_approve` ADD COLUMN `no` VARCHAR(20) NULL  AFTER `memberId`;
ALTER TABLE `scc`.`sms_approve` ADD COLUMN `personalYn` TINYINT NULL DEFAULT 1  AFTER `smsYes`;        
ALTER TABLE `scc`.`sms_approve` ADD COLUMN `utilYn` TINYINT NULL DEFAULT 1  AFTER `personalYn`;
ALTER TABLE `scc`.`sms_approve` ADD COLUMN `useYn` TINYINT NULL DEFAULT 1  AFTER `utilYn`;
ALTER TABLE `scc`.`sms_approve` ADD COLUMN `promotionYes` TINYINT NULL DEFAULT 0  AFTER `smsYes`;


CREATE TABLE `scc`.`statistics_sales_day` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `branchId` VARCHAR(45) NOT NULL,
    `salesDt` DATE NULL,
    `group` INT NULL,
    `option` INT NULL,
    `payInOutType` TINYINT DEFAULT 0,
    `payType` TINYINT DEFAULT 0,
    `payStateType` TINYINT NOT NULL DEFAULT 10,
    `amount` INT DEFAULT 0,
    `useYn` TINYINT NULL DEFAULT 1,
    `div_mons` TINYINT NULL DEFAULT 0,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),
    INDEX `IDX_01` (`branchId` ASC),
    INDEX `IDX_02` (`branchId` ASC, `salesDt` ASC)
) DEFAULT CHARACTER SET utf8;


/*
	앱 어드민 테이블
*/
CREATE TABLE `scc`.`app_admin_userinfo` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `branchId` VARCHAR(45) NOT NULL,
    `userId` VARCHAR(45) NOT NULL,
    `name` VARCHAR(45) NOT NULL,
    `auto` BOOLEAN NULL,
    `uuid` VARCHAR(45) NULL,
    `os` VARCHAR(45) NULL,
    `version` VARCHAR(45) NULL,
    `device` VARCHAR(45) NULL,
    `pushId` varchar(255) NULL,
    `pushYn` BOOLEAN NOT NULL DEFAULT TRUE,
    `useYn` TINYINT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),
  	UNIQUE INDEX `IDX_01` (`branchId` ASC, `userId` ASC),
    INDEX `IDX_02` (`branchId` ASC),
    INDEX `IDX_03` (`branchId` ASC, `userId` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_admin_userinfo` ADD COLUMN `pushYn` BOOLEAN NOT NULL DEFAULT TRUE AFTER `pushId`;
ALTER TABLE `scc`.`app_admin_userinfo` ADD COLUMN `auto` BOOLEAN NULL AFTER `name`;
ALTER TABLE `scc`.`app_admin_userinfo` ADD COLUMN `userId` VARCHAR(45) NOT NULL AFTER `branchId`;
ALTER TABLE `scc`.`app_admin_userinfo` CHANGE `pushId` `pushId`  VARCHAR(255) NULL;


CREATE TABLE `scc`.`app_admin_version` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `type` TINYINT NULL DEFAULT 0,
    `version` VARCHAR(45) NULL,
    `os` VARCHAR(45) NULL,
    `versionDesc` VARCHAR(45) NULL,
    `updateYn` BOOLEAN NOT NULL DEFAULT TRUE,
    `useYn` TINYINT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),
  	UNIQUE INDEX `IDX_01` (`version` ASC, `os` ASC, `type` ASC)    
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_admin_version` ADD COLUMN `type` TINYINT NULL DEFAULT 0 AFTER `id`;

CREATE TABLE `scc`.`app_admin_login_history` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `branchId` VARCHAR(45) NOT NULL,
    `userId` VARCHAR(45) NOT NULL,
    `no` VARCHAR(20) NULL,
    `logType` VARCHAR(45) NULL,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE app_admin_login_history ADD COLUMN `no` VARCHAR(20) after `userId`;


CREATE TABLE `scc`.`app_admin_notice` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `noticeId` VARCHAR(45) NOT NULL,
    `branchId` VARCHAR(45) NOT NULL,
    `title` VARCHAR(45) NULL,
    `content` VARCHAR(20000) NULL,
    `noticeDt` DATE NULL,
     `useYn` TINYINT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),
  	UNIQUE INDEX `IDX_01` (`noticeId` ASC)    
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_admin_notice` ADD COLUMN `noticeDt` DATE NULL AFTER `content`;
ALTER TABLE `scc`.`app_admin_notice` ADD COLUMN `useYn` TINYINT NULL DEFAULT 1 AFTER `noticeDt`;
    
    
CREATE TABLE `scc`.`app_student_member` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `appId` VARCHAR(45) NOT NULL,
    `no` VARCHAR(20) NULL,
    `mainBranchId` VARCHAR(45) NULL,
    `studentId` VARCHAR(45) NULL,
    `studentPw` VARCHAR(255) NULL,
    `studentName` VARCHAR(45) NULL,
    `role` TINYINT NULL,
    `gender` TINYINT NULL DEFAULT 0,
    `birthDt` VARCHAR(45) NULL,
    `bfTel` VARCHAR(45) NULL,
    `tel` VARCHAR(45) NOT NULL,
    `telParent` VARCHAR(45) NULL,
    `email` VARCHAR(45) NULL,
    `address1` VARCHAR(45) NULL,
    `address2` VARCHAR(45) NULL,
    `postcode` VARCHAR(6) NULL,
    `job` VARCHAR(15) NULL DEFAULT 0,
    `interest` VARCHAR(15) NULL DEFAULT 0,
    `cabinet` VARCHAR(45) NULL,
    `useYn` TINYINT NULL DEFAULT 1,
    `pushYn` TINYINT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
    `updateDt` DATETIME NULL DEFAULT NOW(),
    `imgUrl` VARCHAR(255) NULL,
    `transferYn` TINYINT NULL DEFAULT 0,
  	PRIMARY KEY (`id`, `appId`),
  	UNIQUE INDEX `IDX_01` (`id` asc, `appId` asc, `studentId` asc)
) AUTO_INCREMENT = 1000 DEFAULT CHARACTER SET utf8;
ALTER TABLE app_student_member AUTO_INCREMENT = 1000;

ALTER TABLE `scc`.`app_student_member` DROP COLUMN `memberId`;
ALTER TABLE `scc`.`app_student_member` DROP COLUMN `branchid`;
ALTER TABLE `scc`.`app_student_member` DROP COLUMN `studentNickname`;
ALTER TABLE `scc`.`app_student_member` DROP COLUMN `smsTel`;

ALTER TABLE `scc`.`app_student_member` MODIFY COLUMN `studentPw` VARCHAR(255) NULL;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `mainBranchId` VARCHAR(45) NULL AFTER `no`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `mainMemberId` VARCHAR(45) NULL AFTER `mainBranchId`;
ALTER TABLE `scc`.`app_student_member` CHANGE `bf_Tel` `bfTel` VARCHAR(45) NULL;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `telParent` VARCHAR(45) NULL AFTER `tel`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `email` VARCHAR(45) NULL AFTER `telParent`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `addressDetail` VARCHAR(255) NULL AFTER `address2`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `addressType` INT(20) NULL AFTER `addressDetail`;        
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `job` VARCHAR(15) DEFAULT 0 NULL AFTER `postcode`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `interest` VARCHAR(15) DEFAULT 0 NULL AFTER `job`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `cabinet` VARCHAR(45) NULL AFTER `interest`;
ALTER TABLE `scc`.`app_student_member` ADD COLUMN `checkoutYn` TINYINT DEFAULT 0 NULL AFTER `pushYn`;
    
    
CREATE TABLE `scc`.`app_parents_member` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `appId` VARCHAR(45) NOT NULL,
    `no` VARCHAR(20) NULL,
    `parentsId` VARCHAR(45) NULL,
    `parentsPw` VARCHAR(45) NULL,
    `parentsName` VARCHAR(45) NOT NULL,
	`role` TINYINT NOT NULL,
	`mainChildNo` VARCHAR(20) NULL, 
    `gender` TINYINT NOT NULL DEFAULT 0,
    `birthDt` VARCHAR(45) NULL,
    `bfTel` VARCHAR(45) NULL,
    `tel` VARCHAR(45) NOT NULL,
    `address1` VARCHAR(45) NOT NULL,
    `address2` VARCHAR(45) NOT NULL,
    `addressDetail` VARCHAR(255) NULL,
    `addressType` INT(20) NULL,
    `postcode` INT(6) NULL,
    `email` VARCHAR(45) NULL,
    `useYn` TINYINT NOT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
    `updateDt` DATETIME NULL DEFAULT NOW(),
    `imgUrl` VARCHAR(255) NULL, 
  	PRIMARY KEY (`id`, `appId`),
  	UNIQUE INDEX `IDX_01` (`id` asc, `appId` asc , `parentsId` asc)
) AUTO_INCREMENT = 1000 DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_parents_member` AUTO_INCREMENT = 1000;
ALTER TABLE `scc`.`app_parents_member` DROP COLUMN `memberId`;
ALTER TABLE `scc`.`app_parents_member` DROP COLUMN  `parentsNickname`;

ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `mainChildNo` VARCHAR(20) NULL AFTER `role`;
ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `bfTel` VARCHAR(45) NULL AFTER `birthDt`;
ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `postcode` INT(6) NULL AFTER `address2`;
ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `email` VARCHAR(45) NULL AFTER `postcode`;
ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `addressDetail` VARCHAR(255) NULL AFTER `address2`;
ALTER TABLE `scc`.`app_parents_member` ADD COLUMN `addressType` INT(20) NULL AFTER `addressDetail`;     
ALTER TABLE `scc`.`app_parents_member` CHANGE COLUMN  `parentsPw` `parentsPw` VARCHAR(255) NULL AFTER `parentsId`;


CREATE TABLE `scc`.`app_parents_safeservice` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`parentsAppId` VARCHAR(45) NOT NULL,
	`parentsName` VARCHAR(45) NOT NULL,
	`parentsNo` VARCHAR(20) NOT NULL,
	`parentsId` VARCHAR(45) NULL,
	`parentsTel` VARCHAR(45) NULL,
	`studentName` VARCHAR(45) NOT NULL,
	`studentNo` VARCHAR(20) NOT NULL,
	`studentId` VARCHAR(45) NOT NULL,
	`studentAppId` VARCHAR(45) NOT NULL,
	`studentTel` VARCHAR(45) NULL,
	`memberId` VARCHAR(45) NOT NULL,
	`startDt` DATE NULL,
	`status` TINYINT NULL DEFAULT 10,
	`useYn` TINYINT NOT NULL DEFAULT 1,
	`insertDt` DATETIME NULL DEFAULT NOW(),
	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id`),
	UNIQUE INDEX `IDX_01` (`id` asc, `parentsAppId` asc , `studentAppId` asc)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_parents_safeservice` DROP COLUMN `parentsMemberId`;

ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `status` TINYINT(4) null AFTER `studentAppId`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `branchId` VARCHAR(45) null AFTER `status`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `parentsNo` VARCHAR(20) NULL AFTER `parentsName`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `parentsId` VARCHAR(45) NULL AFTER `parentsNo`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `parentsTel` VARCHAR(45) NULL AFTER `parentsId`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `studentName` VARCHAR(45) NULL AFTER `parentsTel`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `studentNo` VARCHAR(20) NULL AFTER `studentName`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `studentTel` VARCHAR(45) NULL AFTER `studentNo`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `status` date NULL AFTER `studentId`;
ALTER TABLE `scc`.`app_parents_safeservice` ADD COLUMN `startDt` date NULL AFTER `status`;




CREATE TABLE `scc`.`sms_certify` ( 
	`id` INT NOT NULL AUTO_INCREMENT,
	`tel` VARCHAR(45) NOT NULL,
	`authNum` VARCHAR(6) NOT NULL,
	`insertDt` DATETIME NULL DEFAULT NOW(),
	`updateDt` DATETIME NULL DEFAULT NOW(),
	`description` TEXT NULL,
	`type` TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `IDX_01` (`id` asc )
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`app_seat_application` ( 
	`id` INT NOT NULL AUTO_INCREMENT,
	`applicationId` VARCHAR(45) NOT NULL,
	`appId` VARCHAR(45) NOT NULL,
	`no` VARCHAR(20) NOT NULL,
	`branchId` VARCHAR(45) NOT NULL,
	`startDt` DATE NOT NULL,
	`roomType` TINYINT NOT NULL DEFAULT 10,
	`role` TINYINT NOT NULL ,
	`name` VARCHAR(45) NOT NULL,
	`gender` TINYINT NOT NULL DEFAULT -1 ,
	`birthDt` VARCHAR(12) NULL,
	`tel` VARCHAR(45) NULL,
	`email` VARCHAR(45) NULL,
	`status` TINYINT NOT NULL,
	`type` TINYINT NOT NULL,
	`useYn` TINYINT NOT NULL DEFAULT 1,
	`insertDt` DATETIME NULL DEFAULT NOW(),
	`updateDt` DATETIME NULL DEFAULT NOW(),
	PRIMARY KEY (`id` , `applicationId`),
	UNIQUE INDEX `IDX_01` (`id` asc , `applicationId` asc )
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_seat_application` ADD COLUMN `branchId` VARCHAR(45) NOT NULL AFTER `appId`;
ALTER TABLE `scc`.`app_seat_application` ADD COLUMN `no` VARCHAR(20) NOT NULL AFTER `branchId`;
ALTER TABLE `scc`.`app_seat_application` ADD COLUMN `role` TINYINT NOT NULL AFTER `no`;

CREATE TABLE `scc`.`app_student_userinfo` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `no` INT(11) NOT NULL,
    `name` VARCHAR(45) NOT NULL,
    `auto` BOOLEAN NULL,
    `uuid` VARCHAR(45) NULL,
    `os` VARCHAR(45) NULL,
    `version` VARCHAR(45) NULL,
    `device` VARCHAR(45) NULL,
    `pushId` varchar(255) NULL,
    `pushYn` TINYINT NULL DEFAULT 1,
    `useYn` TINYINT NULL DEFAULT 1,
    `insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`, `no` ),
  	UNIQUE INDEX `IDX_01` (`id` ASC, `no` asc)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`app_student_userinfo` MODIFY COLUMN `pushId` varchar(255) NULL;

CREATE TABLE `scc`.`app_branch_manager` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `appId` VARCHAR(45) NULL,
  `no` VARCHAR(20) NULL, 
  `memberId` VARCHAR(45) NULL,
  `branchId` VARCHAR(45) NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`appId` ASC, `branchId` ASC),
  INDEX `IDX_02` (`branchId` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`statistics_entry_day` (
  `id` INT NOT NULL AUTO_INCREMENT,  
  `branchId` VARCHAR(45) NULL,
  `branchNm` VARCHAR(45) NULL,
  `no` VARCHAR(20) NULL,
  `memberId` VARCHAR(45) NULL,
  `memberNm` VARCHAR(45) NULL,
  `businessDt` DATE NULL,
  `time` INT NULL,
  `useYn` TINYINT NULL DEFAULT 1,
  `autoYn` TINYINT NULL DEFAULT 0,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `IDX_01` (`branchId` ASC, `memberId` ASC, `businessDt` ASC),
  INDEX `IDX_02` (`branchId` ASC)
) DEFAULT CHARACTER SET utf8;

ALTER TABLE `scc`.`statistics_entry_day` ADD COLUMN `autoYn` TINYINT NULL DEFAULT 0 AFTER `useYn`;


CREATE TABLE `scc`.`iphone_push_info` (
	`id` INT NOT NULL AUTO_INCREMENT,	
	`deviceId` VARCHAR(255) NULL, 
	`registerationId` VARCHAR(255) NULL, 
	`alarmYn` TINYINT NULL DEFAULT 1, 
	`agreeYn` TINYINT NULL DEFAULT 1, 
	`locationYn` TINYINT NULL DEFAULT 1, 
	`useYn` TINYINT NULL DEFAULT 1, 	
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),  	
  	INDEX `IDX_01` (`registerationId` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`android_push_info` (
	`id` INT NOT NULL AUTO_INCREMENT,	
	`deviceId` VARCHAR(255) NULL, 
	`registerationId` VARCHAR(255) NULL, 
	`alarmYn` TINYINT NULL DEFAULT 1, 
	`agreeYn` TINYINT NULL DEFAULT 1, 
	`locationYn` TINYINT NULL DEFAULT 1, 
	`useYn` TINYINT NULL DEFAULT 1, 	
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),  	
  	INDEX `IDX_01` (`registerationId` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`history_alimtalk` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `serial_number` VARCHAR(45) NULL, 
  `sender_key` VARCHAR(45) NULL, 
  `phone_number` VARCHAR(20) NULL, 
  `template_code` VARCHAR(45) NULL, 
  `message` VARCHAR(1000) NULL, 
  `response_method` VARCHAR(20) NULL, 
  `link_url` VARCHAR(255) NULL, 
  `result_code` VARCHAR(45) NULL, 
  `result_message` VARCHAR(45) NULL,
  `branchId` VARCHAR(45) NULL,
  `insertDt` DATETIME NULL DEFAULT NOW(),
  `updateDt` DATETIME NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`common_code` (
	`id` INT NOT NULL AUTO_INCREMENT,	
	`branchId` VARCHAR(45) NULL,
	`branchNm` VARCHAR(45) NULL,
	`codeType` VARCHAR(45) NULL,
	`code` VARCHAR(45) NULL,
	`codeNm` VARCHAR(45) NULL,
	`description` VARCHAR(255) NULL,
	`useYn` TINYINT NULL DEFAULT 1, 	
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),  	
  	INDEX `IDX_01` (`branchId` ASC)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE `scc`.`biostar_cookie` (
	`id` INT NOT NULL AUTO_INCREMENT,	
	`branchId` VARCHAR(45) NULL,
	`cookie` VARCHAR(255) NULL,	 	
  	`insertDt` DATETIME NULL DEFAULT NOW(),
  	`updateDt` DATETIME NULL DEFAULT NOW(),
  	PRIMARY KEY (`id`),  	
  	INDEX `IDX_01` (`branchId` ASC)
) DEFAULT CHARACTER SET utf8;

