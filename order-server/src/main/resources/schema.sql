SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema inbobwetrust
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `inbobwetrust` DEFAULT CHARACTER SET utf8 ;
USE `inbobwetrust` ;

-- -----------------------------------------------------
-- Table `inbobwetrust`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`customer` (
  `id` VARCHAR(20) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `tel` VARCHAR(13) NOT NULL,
  `email` VARCHAR(45) NULL,
  `address` VARCHAR(45) NULL,
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`shop`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`shop` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `tel` VARCHAR(13) NOT NULL,
  `address` VARCHAR(100) NOT NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT 'READY',
  `owner_name` VARCHAR(20) NOT NULL,
  `photo` BLOB NULL,
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  `work_start_time` DATETIME NULL,
  `work_end_time` DATETIME NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`menu` (
  `id` BIGINT(20) NOT NULL,
  `shop_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `price` BIGINT(20) NOT NULL DEFAULT 0,
  `description` VARCHAR(200) NULL,
  `photo` BLOB NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_menu_shop1_idx` (`shop_id` ASC) VISIBLE,
  CONSTRAINT `fk_menu_shop1`
    FOREIGN KEY (`shop_id`)
    REFERENCES `inbobwetrust`.`shop` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`agency`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`agency` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`rider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`rider` (
  `id` VARCHAR(20) NOT NULL,
  `agency_id` BIGINT(20) NOT NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT 'READY',
  `name` VARCHAR(20) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `tel` VARCHAR(13) NOT NULL,
  `email` VARCHAR(45) NULL,
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rider_agency1_idx` (`agency_id` ASC) VISIBLE,
  CONSTRAINT `fk_rider_agency1`
    FOREIGN KEY (`agency_id`)
    REFERENCES `inbobwetrust`.`agency` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`order` (
  `id` BIGINT(20) NOT NULL,
  `customer_id` VARCHAR(20) NOT NULL,
  `shop_id` BIGINT(20) NOT NULL,
  `rider_id` VARCHAR(20) NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT 'PAYMENT_READY',
  `payment_type` VARCHAR(45) NOT NULL DEFAULT 'CASH',
  `payment_price` BIGINT(20) NOT NULL DEFAULT 0,
  `address` VARCHAR(45) NOT NULL,
  `cumstomer_request` VARCHAR(45) NULL,
  `reg_date` DATETIME NOT NULL,
  `proc_date` DATETIME NOT NULL,
  `order_status_last_updated` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_order_user1_idx` (`customer_id` ASC) VISIBLE,
  INDEX `fk_order_rider1_idx` (`rider_id` ASC) VISIBLE,
  INDEX `fk_order_shop1_idx` (`shop_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_user1`
    FOREIGN KEY (`customer_id`)
    REFERENCES `inbobwetrust`.`customer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_rider1`
    FOREIGN KEY (`rider_id`)
    REFERENCES `inbobwetrust`.`rider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_shop1`
    FOREIGN KEY (`shop_id`)
    REFERENCES `inbobwetrust`.`shop` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`order_menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`order_menu` (
  `menu_id` BIGINT(20) NOT NULL,
  `order_id` BIGINT(20) NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`menu_id`, `order_id`),
  INDEX `fk_order_menu_menu1_idx` (`menu_id` ASC) VISIBLE,
  INDEX `fk_order_menu_order1_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_menu_menu1`
    FOREIGN KEY (`menu_id`)
    REFERENCES `inbobwetrust`.`menu` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_menu_order1`
    FOREIGN KEY (`order_id`)
    REFERENCES `inbobwetrust`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `inbobwetrust`.`delivery`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`delivery` (
  `order_id` BIGINT(20) NOT NULL,
  `rider_id` VARCHAR(20) NOT NULL,
  `wanted_pickup_time` DATETIME NOT NULL,
  `estimated_cooking_time` DATETIME NOT NULL,
  `estimated_delivery_finish_time` DATETIME NOT NULL,
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  INDEX `fk_delivery_order1_idx` (`order_id` ASC) VISIBLE,
  INDEX `fk_delivery_rider1_idx` (`rider_id` ASC) VISIBLE,
  PRIMARY KEY (`rider_id`, `order_id`),
  CONSTRAINT `fk_delivery_order1`
    FOREIGN KEY (`order_id`)
    REFERENCES `inbobwetrust`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_delivery_rider1`
    FOREIGN KEY (`rider_id`)
    REFERENCES `inbobwetrust`.`rider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
