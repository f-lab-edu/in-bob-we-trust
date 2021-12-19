INSERT INTO tbl_agency
(endpoint)
VALUES('http://agency1.com');

INSERT INTO tbl_shop
(endpoint)
VALUES('http://shop1.com');

INSERT INTO tbl_rider (agency_id) VALUES (1);

INSERT INTO tbl_order
(customer_id, shop_id, order_status, address, phone_number)
VALUES(1, 1, 'COMPLETE', 'addreess....', '01032801');


INSERT INTO tbl_delivery
(order_id, rider_id,agency_id,  order_status, pickup_time, finish_time)
VALUES(1, 1, 1, 'COMPLETE','2021-07-26 22:08:15', '2021-07-26 23:08:15');

