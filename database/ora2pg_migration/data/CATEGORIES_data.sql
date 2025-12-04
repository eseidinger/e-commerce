SET client_encoding TO 'UTF8';
SET synchronous_commit TO off;

TRUNCATE TABLE categories;

COPY categories (category_id,category_name,description) FROM STDIN;
1	Electronics	Electronic devices and accessories
2	Clothing	Apparel and fashion items
3	Books	Physical and digital books
\.
