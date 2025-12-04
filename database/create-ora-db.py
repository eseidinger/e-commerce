import oracledb

# Database connection parameters
username = "system"
password = "admin123"
dsn = "localhost:1521/XEPDB1"

try:
    # Establish connection as system user
    connection = oracledb.connect(user=username, password=password, dsn=dsn)
    cursor = connection.cursor()
    
    print("Creating e-commerce schema and user...")
    
    # Create user/schema for e-commerce
    try:
        cursor.execute("DROP USER ecommerce CASCADE")
        print("Dropped existing ecommerce user")
    except oracledb.DatabaseError:
        pass
    
    cursor.execute("""
        CREATE USER ecommerce 
        IDENTIFIED BY ecommerce123
        DEFAULT TABLESPACE USERS
        TEMPORARY TABLESPACE TEMP
        QUOTA UNLIMITED ON USERS
    """)
    
    # Grant necessary privileges
    cursor.execute("GRANT CONNECT, RESOURCE TO ecommerce")
    cursor.execute("GRANT CREATE VIEW TO ecommerce")
    
    print("User 'ecommerce' created successfully")
    
    # Close system connection and reconnect as ecommerce user
    cursor.close()
    connection.close()
    
    print("Connecting as ecommerce user...")
    connection = oracledb.connect(user="ecommerce", password="ecommerce123", dsn=dsn)
    cursor = connection.cursor()
    
    print("Creating e-commerce database tables...")
    
    # Drop tables if they exist (in reverse order due to foreign keys)
    drop_tables = [
        "DROP TABLE order_items CASCADE CONSTRAINTS",
        "DROP TABLE orders CASCADE CONSTRAINTS",
        "DROP TABLE products CASCADE CONSTRAINTS",
        "DROP TABLE categories CASCADE CONSTRAINTS",
        "DROP TABLE customers CASCADE CONSTRAINTS",
        "DROP VIEW customer_orders",
        "DROP VIEW product_sales"
    ]
    
    for drop_stmt in drop_tables:
        try:
            cursor.execute(drop_stmt)
        except oracledb.DatabaseError:
            pass
    
    # Create tables
    cursor.execute("""
        CREATE TABLE customers (
            customer_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            first_name VARCHAR2(50) NOT NULL,
            last_name VARCHAR2(50) NOT NULL,
            email VARCHAR2(100) UNIQUE NOT NULL,
            phone VARCHAR2(20),
            address VARCHAR2(200),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """)
    
    cursor.execute("""
        CREATE TABLE categories (
            category_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            category_name VARCHAR2(100) NOT NULL,
            description VARCHAR2(500)
        )
    """)
    
    cursor.execute("""
        CREATE TABLE products (
            product_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            product_name VARCHAR2(200) NOT NULL,
            category_id NUMBER,
            price NUMBER(10,2) NOT NULL,
            stock_quantity NUMBER DEFAULT 0,
            description VARCHAR2(1000),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
        )
    """)
    
    cursor.execute("""
        CREATE TABLE orders (
            order_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            customer_id NUMBER NOT NULL,
            order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            total_amount NUMBER(10,2),
            status VARCHAR2(20) DEFAULT 'pending',
            CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        )
    """)
    
    cursor.execute("""
        CREATE TABLE order_items (
            order_item_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            order_id NUMBER NOT NULL,
            product_id NUMBER NOT NULL,
            quantity NUMBER NOT NULL,
            unit_price NUMBER(10,2) NOT NULL,
            CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
            CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(product_id)
        )
    """)
    
    # Create views
    cursor.execute("""
        CREATE VIEW customer_orders AS
        SELECT c.customer_id, c.first_name, c.last_name, c.email,
               o.order_id, o.order_date, o.total_amount, o.status
        FROM customers c
        JOIN orders o ON c.customer_id = o.customer_id
    """)
    
    cursor.execute("""
        CREATE VIEW product_sales AS
        SELECT p.product_id, p.product_name, cat.category_name,
               SUM(oi.quantity) as total_sold,
               SUM(oi.quantity * oi.unit_price) as total_revenue
        FROM products p
        LEFT JOIN order_items oi ON p.product_id = oi.product_id
        LEFT JOIN categories cat ON p.category_id = cat.category_id
        GROUP BY p.product_id, p.product_name, cat.category_name
    """)
    
    print("Tables and views created successfully!")
    
    # Insert sample data
    print("Inserting sample data...")
    
    # Categories
    cursor.execute("INSERT INTO categories (category_name, description) VALUES ('Electronics', 'Electronic devices and accessories')")
    cursor.execute("INSERT INTO categories (category_name, description) VALUES ('Clothing', 'Apparel and fashion items')")
    cursor.execute("INSERT INTO categories (category_name, description) VALUES ('Books', 'Physical and digital books')")
    
    # Customers
    cursor.execute("INSERT INTO customers (first_name, last_name, email, phone, address) VALUES ('John', 'Doe', 'john.doe@email.com', '555-0101', '123 Main St, New York, NY')")
    cursor.execute("INSERT INTO customers (first_name, last_name, email, phone, address) VALUES ('Jane', 'Smith', 'jane.smith@email.com', '555-0102', '456 Oak Ave, Los Angeles, CA')")
    cursor.execute("INSERT INTO customers (first_name, last_name, email, phone, address) VALUES ('Bob', 'Johnson', 'bob.johnson@email.com', '555-0103', '789 Pine Rd, Chicago, IL')")
    
    # Products
    cursor.execute("INSERT INTO products (product_name, category_id, price, stock_quantity, description) VALUES ('Laptop', 1, 999.99, 50, 'High-performance laptop')")
    cursor.execute("INSERT INTO products (product_name, category_id, price, stock_quantity, description) VALUES ('Smartphone', 1, 699.99, 100, 'Latest model smartphone')")
    cursor.execute("INSERT INTO products (product_name, category_id, price, stock_quantity, description) VALUES ('T-Shirt', 2, 19.99, 200, 'Cotton t-shirt')")
    cursor.execute("INSERT INTO products (product_name, category_id, price, stock_quantity, description) VALUES ('Jeans', 2, 49.99, 150, 'Denim jeans')")
    cursor.execute("INSERT INTO products (product_name, category_id, price, stock_quantity, description) VALUES ('Python Programming', 3, 39.99, 75, 'Learn Python programming')")
    
    # Orders
    cursor.execute("INSERT INTO orders (customer_id, total_amount, status) VALUES (1, 1019.98, 'completed')")
    cursor.execute("INSERT INTO orders (customer_id, total_amount, status) VALUES (2, 749.98, 'shipped')")
    cursor.execute("INSERT INTO orders (customer_id, total_amount, status) VALUES (3, 69.98, 'pending')")
    
    # Order items
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 1, 1, 999.99)")
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (1, 3, 1, 19.99)")
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 2, 1, 699.99)")
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (2, 4, 1, 49.99)")
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 3, 2, 19.99)")
    cursor.execute("INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (3, 5, 1, 39.99)")
    
    # Commit changes
    connection.commit()
    print("Sample data inserted successfully!")
    
    # Query the views to verify
    print("\nCustomer Orders:")
    cursor.execute("SELECT * FROM customer_orders")
    for row in cursor.fetchall():
        print(row)
    
    print("\nProduct Sales:")
    cursor.execute("SELECT * FROM product_sales")
    for row in cursor.fetchall():
        print(row)
    
except oracledb.DatabaseError as e:
    print(f"Database error: {e}")
finally:
    if cursor:
        cursor.close()
    if connection:
        connection.close()
    print("\nDatabase connection closed.")