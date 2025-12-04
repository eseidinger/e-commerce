SET client_encoding TO 'UTF8';
SET synchronous_commit TO off;

TRUNCATE TABLE customers;

COPY customers (customer_id,first_name,last_name,email,phone,address,created_at) FROM STDIN;
1	John	Doe	john.doe@email.com	555-0101	123 Main St, New York, NY	2025-12-04 19:01:25.927061
2	Jane	Smith	jane.smith@email.com	555-0102	456 Oak Ave, Los Angeles, CA	2025-12-04 19:01:25.927879
3	Bob	Johnson	bob.johnson@email.com	555-0103	789 Pine Rd, Chicago, IL	2025-12-04 19:01:25.928543
\.
