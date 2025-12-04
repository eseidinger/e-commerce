SET client_encoding TO 'UTF8';
SET synchronous_commit TO off;

TRUNCATE TABLE orders;

COPY orders (order_id,customer_id,order_date,total_amount,status) FROM STDIN;
1	1	2025-12-04 19:01:25.941886	1019.98	completed
2	2	2025-12-04 19:01:25.945938	749.98	shipped
3	3	2025-12-04 19:01:25.946566	69.98	pending
\.
