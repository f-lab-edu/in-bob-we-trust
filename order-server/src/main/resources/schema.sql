
-- -----------------------------------------------------
-- Schema inbobwetrust
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema inbobwetrust
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `inbobwetrust` DEFAULT CHARACTER SET utf8 ;
USE `inbobwetrust` ;

-- -----------------------------------------------------
-- Table `inbobwetrust`.`customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`customer` (
  `id` BIGINT(20) UNSIGNED NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `password` VARCHAR(20) NOT NULL,
  `tel` VARCHAR(13) NOT NULL,
  `email` VARCHAR(45) NULL,
  `address` VARCHAR(45) NULL COMMENT '\n',
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`))


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



-- -----------------------------------------------------
-- Table `inbobwetrust`.`menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`menu` (
  `id` BIGINT(20) NOT NULL,
  `shop_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `price` INT NOT NULL,
  `description` VARCHAR(200) NULL,
  `photo` BLOB NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_menu_shop_idx` (`shop_id` ASC) VISIBLE,
  CONSTRAINT `fk_menu_shop`
    FOREIGN KEY (`shop_id`)
    REFERENCES `inbobwetrust`.`shop` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



-- -----------------------------------------------------
-- Table `inbobwetrust`.`agency`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`agency` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))



-- -----------------------------------------------------
-- Table `inbobwetrust`.`rider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`rider` (
  `id` VARCHAR(20) NOT NULL,
  `agency_id` BIGINT(20) NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  `name` VARCHAR(20) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `tel` VARCHAR(13) NOT NULL,
  `email` VARCHAR(45) NULL,
  `reg_date` DATETIME NOT NULL,
  `mod_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_rider_agency_idx` (`agency_id` ASC) VISIBLE,
  CONSTRAINT `fk_rider_agency`
    FOREIGN KEY (`agency_id`)
    REFERENCES `inbobwetrust`.`agency` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)


-- -----------------------------------------------------
-- Table `inbobwetrust`.`order_table`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`order_table` (
  `id` VARCHAR(20) NOT NULL,
  `customer_id` VARCHAR(20) NOT NULL,
  `shop_id` BIGINT(20) NOT NULL,
  `rider_id` VARCHAR(20) NULL,
  `status` VARCHAR(45) NOT NULL DEFAULT 'PAYMENT_READY',
  `payment_type` VARCHAR(45) NOT NULL DEFAULT 'CASH',
  `payment_price` BIGINT(20) NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `cumstomer_request` VARCHAR(45) NULL,
  `reg_date` DATETIME NOT NULL,
  `proc_date` DATETIME NOT NULL,
  `order_status_last_updated` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_order_table_user_idx` (`customer_id` ASC) VISIBLE,
  INDEX `fk_order_table_rider_idx` (`rider_id` ASC) VISIBLE,
  INDEX `fk_order_table_shop_idx` (`shop_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_table_user`
    FOREIGN KEY (`customer_id`)
    REFERENCES `inbobwetrust`.`customer` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_table_rider`
    FOREIGN KEY (`rider_id`)
    REFERENCES `inbobwetrust`.`rider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_table_shop`
    FOREIGN KEY (`shop_id`)
    REFERENCES `inbobwetrust`.`shop` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



-- -----------------------------------------------------
-- Table `inbobwetrust`.`order_menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inbobwetrust`.`order_menu` (
  `menu_id` BIGINT(20) NOT NULL,
  `order_id` BIGINT(20) NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`menu_id`, `order_table_id`),
  INDEX `fk_order_menu_menu_idx` (`menu_id` ASC) VISIBLE,
  INDEX `fk_order_menu_order_table_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `fk_order_menu_menu`
    FOREIGN KEY (`menu_id`)
    REFERENCES `inbobwetrust`.`menu` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_menu_order_table`
    FOREIGN KEY (`order_id`)
    REFERENCES `inbobwetrust`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)



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
  INDEX `fk_delivery_order_idx` (`order_id` ASC) VISIBLE,
  INDEX `fk_delivery_rider_idx` (`rider_id` ASC) VISIBLE,
  PRIMARY KEY (`rider_id`, `order_id`),
  CONSTRAINT `fk_delivery_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `inbobwetrust`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_delivery_rider`
    FOREIGN KEY (`rider_id`)
    REFERENCES `inbobwetrust`.`rider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
