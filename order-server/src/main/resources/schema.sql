-- -----------------------------------------------------
-- Table tbl_agency
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tbl_agency (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  endpoint VARCHAR(300) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (id)
);
-- -----------------------------------------------------
-- Table tbl_shop
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tbl_shop (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  endpoint VARCHAR(300) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (id)
);
-- -----------------------------------------------------
-- Table tbl_rider
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tbl_rider (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  agency_id BIGINT(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE(id),
  FOREIGN KEY (agency_id) REFERENCES tbl_agency (id)
);
-- -----------------------------------------------------
-- Table tbl_delivery
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tbl_delivery (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  order_id BIGINT(20) NOT NULL,
  rider_id BIGINT(20) NULL,
  agency_id BIGINT(20) NULL,
  pickup_time DATETIME NULL,
  finish_time DATETIME NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE (id),
  FOREIGN KEY (rider_id) REFERENCES tbl_rider(id)
);
-- -----------------------------------------------------
-- Table tbl_order
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tbl_order (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  customer_id BIGINT(20) NOT NULL,
  shop_id BIGINT(20) NOT NULL,
  delivery_id BIGINT(20) NULL,
  order_status VARCHAR(50) NULL,
  address VARCHAR(1000) NULL,
  phone_number VARCHAR(200) NULL,
  created_at DATETIME NULL,
  updated_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE (id),
  FOREIGN KEY (delivery_id) REFERENCES tbl_delivery (id),
  FOREIGN KEY (shop_id) REFERENCES tbl_shop (id)
);

CREATE TABLE IF NOT EXISTS tbl_sample(
  id BIGINT(20) ,
  UNIQUE(id)
);
