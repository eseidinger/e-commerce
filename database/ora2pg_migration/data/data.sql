
BEGIN;
ALTER TABLE categories DISABLE TRIGGER USER;
ALTER TABLE customers DISABLE TRIGGER USER;
ALTER TABLE orders DISABLE TRIGGER USER;
ALTER TABLE order_items DISABLE TRIGGER USER;
ALTER TABLE products DISABLE TRIGGER USER;

\i './data/CATEGORIES_data.sql'
\i './data/CUSTOMERS_data.sql'
\i './data/ORDERS_data.sql'
\i './data/ORDER_ITEMS_data.sql'
\i './data/PRODUCTS_data.sql'

COMMIT;

