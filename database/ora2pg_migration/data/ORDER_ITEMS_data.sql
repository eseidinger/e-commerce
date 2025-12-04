SET client_encoding TO 'UTF8';
SET synchronous_commit TO off;

TRUNCATE TABLE order_items;

COPY order_items (order_item_id,order_id,product_id,quantity,unit_price) FROM STDIN;
1	1	1	1	999.99
2	1	3	1	19.99
3	2	2	1	699.99
4	2	4	1	49.99
5	3	3	2	19.99
6	3	5	1	39.99
\.
