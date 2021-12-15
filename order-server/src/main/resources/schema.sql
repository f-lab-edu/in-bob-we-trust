CREATE SCHEMA IF NOT EXISTS `order_server` DEFAULT CHARACTER SET utf8 ;
USE `order_server` ;

-- -----------------------------------------------------
-- Table `tbl_agency`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_agency` (
  `id` BIGINT(20) GENERATED ALWAYS AS (),
  `endpoint` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)


-- -----------------------------------------------------
-- Table `tbl_shop`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_shop` (
  `id` BIGINT(20) GENERATED ALWAYS AS (),
  `endpoint` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)

-- -----------------------------------------------------
-- Table `tbl_rider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_rider` (
  `id` BIGINT(20) GENERATED ALWAYS AS (),
  `agency_id` BIGINT(20) NOT NULL,
  INDEX `fk_table1_tbl_agency_idx` (`agency_id` ASC) VISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  CONSTRAINT `fk_table1_tbl_agency`
    FOREIGN KEY (`agency_id`)
    REFERENCES `tbl_agency` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)

-- -----------------------------------------------------
-- Table `tbl_delivery`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_delivery` (
  `id` BIGINT(20) GENERATED ALWAYS AS (),
  `order_id` BIGINT(20) NOT NULL,
  `rider_id` BIGINT(20) NULL,
  `agency_id` BIGINT(20) NULL,
  `pickup_time` DATETIME NULL,
  `finish_time` DATETIME NULL,
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_tbl_delivery_tbl_rider1_idx` (`rider_id` ASC) VISIBLE,
  CONSTRAINT `fk_tbl_delivery_tbl_rider1`
    FOREIGN KEY (`rider_id`)
    REFERENCES `rider` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)


-- -----------------------------------------------------
-- Table `tbl_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_order` (
  `id` BIGINT(20) GENERATED ALWAYS AS (),
  `customer_id` BIGINT(20) NOT NULL,
  `shop_id` BIGINT(20) NOT NULL,
  `delivery_id` BIGINT(20) NULL,
  `order_status` VARCHAR(50) NULL,
  `address` VARCHAR(1000) NULL,
  `phone_number` VARCHAR(200) NULL,
  `created_at` DATETIME NULL,
  `updated_at` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `fk_tbl_order_tbl_delivery1_idx` (`delivery_id` ASC) VISIBLE,
  INDEX `fk_tbl_order_tbl_shop1_idx` (`shop_id` ASC) VISIBLE,
  CONSTRAINT `fk_tbl_order_tbl_delivery1`
    FOREIGN KEY (`delivery_id`)
    REFERENCES `tbl_delivery` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tbl_order_tbl_shop1`
    FOREIGN KEY (`shop_id`)
    REFERENCES `tbl_shop` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)


-- -----------------------------------------------------
-- Table `tbl_menu`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_menu` (
)



-- -----------------------------------------------------
-- Table `tbl_customer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tbl_customer` (

)
