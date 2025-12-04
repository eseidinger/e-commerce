SET client_encoding TO 'UTF8';
SET synchronous_commit TO off;

TRUNCATE TABLE products;

COPY products (product_id,product_name,category_id,price,stock_quantity,description,created_at) FROM STDIN;
1	Laptop	1	999.99	50	High-performance laptop	2025-12-04 19:01:25.931224
2	Smartphone	1	699.99	100	Latest model smartphone	2025-12-04 19:01:25.937137
3	T-Shirt	2	19.99	200	Cotton t-shirt	2025-12-04 19:01:25.937858
4	Jeans	2	49.99	150	Denim jeans	2025-12-04 19:01:25.938787
5	Python Programming	3	39.99	75	Learn Python programming	2025-12-04 19:01:25.939537
\.
