INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Grocery', 'Packaged food and daily essentials'),
('Stationery', 'Office and school supplies'),
('Furniture', 'Home and office furniture');

INSERT INTO products (name, sku, category_id, description, quantity, low_stock_threshold, unit_price, supplier, created_at, updated_at) VALUES
('Wireless Mouse', 'ELEC-001', 1, 'Ergonomic 2.4GHz wireless mouse', 45, 15, 499.00, 'Logitech Distributors', NOW(), NOW()),
('USB-C Charger 20W', 'ELEC-002', 1, 'Fast charging USB-C wall adapter', 8, 10, 799.00, 'Anker India', NOW(), NOW()),
('Bluetooth Headphones', 'ELEC-003', 1, 'Over-ear noise cancelling headphones', 0, 5, 2499.00, 'Boat Lifestyle', NOW(), NOW()),
('Basmati Rice 5kg', 'GRO-001', 2, 'Premium long-grain basmati rice', 120, 20, 650.00, 'Local Wholesale Mart', NOW(), NOW()),
('Sunflower Oil 1L', 'GRO-002', 2, 'Refined sunflower cooking oil', 6, 15, 180.00, 'Local Wholesale Mart', NOW(), NOW()),
('A4 Paper Ream (500 sheets)', 'STA-001', 3, '70 GSM white copier paper', 60, 20, 320.00, 'JK Paper Supplies', NOW(), NOW()),
('Ballpoint Pen (Box of 10)', 'STA-002', 3, 'Blue ink ballpoint pens', 3, 10, 120.00, 'Cello Distributors', NOW(), NOW()),
('Office Chair - Mesh Back', 'FUR-001', 4, 'Adjustable height ergonomic office chair', 12, 5, 5999.00, 'Featherlite Ltd', NOW(), NOW()),
('Study Table', 'FUR-002', 4, 'Compact wooden study table', 0, 5, 3499.00, 'Nilkamal Ltd', NOW(), NOW());