-- Users
INSERT INTO USERS (username, email, password)
VALUES
       ("Tux", "Tux@pengu.com", "tux"),
       ("Pengu", "Pengu@pengu.com", "pengu"),
       ("Pinguim3", "Pinguim3@pengu.com", "pengu"),
       ("Pinguim4", "Pinguim4@pengu.com", "pengu"),
       ("Pinguim5", "Pinguim5@pengu.com", "pengu"),
       ("", "", "");

-- Pantries
INSERT INTO PANTRIES (code, name, latitude, longitude)
VALUES
       ("OGPANTRY", "OGPantry", 125.12, 321.32),
       ("TuxPantry", "TuxPantry", 456.45, 654.65),
       ("PenguPANTRY", "PenguPantry", 789.98, 987.87),
       ("Pantry4", "Pantry4", 159.95, 951.15),
       ("Pantry5", "Pantry5", 753.35, 357.75);

-- Products with barcode
INSERT INTO PRODUCTS (barcode, name)
VALUES
       ("ice4510", "Ice"),
       ("fish15468", "Fish"),
       ("shirmp456564", "Shrimp"),
       ("seabird789987", "Seabird"),
       ("crab159357", "Crab");

-- Products without barcode
INSERT INTO PRODUCTS (name)
VALUES
       ("Batatas"),
       ("Couves");

-- Pantry_Products
INSERT INTO PANTRY_PRODUCTS (pantry_id, product_id, have_qty, want_qty)
VALUES
       (1, 1, 2, 3),
       (1, 2, 3, 3),
       (1, 3, 1, 5),
       (1, 6, 1, 2),
       (1, 7, 1, 3),
       (2, 1, 2, 6),
       (2, 2, 3, 5);

-- Pantry_Users
INSERT INTO PANTRIES_USERS (pantry_id, user_id)
VALUES
       (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2);

-- Crowd_Product_Prices
INSERT INTO CROWD_PRODUCT_PRICES (barcode, price, latitude, longitude)
VALUES
       ("ice4510" , 1.69, 150.25, 150.25),
       ("ice4510" , 5.26, 250.25, 250.25),
       ("ice4510" , 6.79,  350.25, 350.25),
       ("fish15468" , 1.13, 150.15, 150.15),
       ("fish15468" , 4.27, 250.25, 250.35),
       ("shirmp456564" , 6.99, 350.25, 350.25),
       ("seabird789987" , 8.89, 450.25, 450.25),
       ("crab159357" , 10.56, 550.25, 550.25 );

-- Local_Product_Prices
INSERT INTO LOCAL_PRODUCT_PRICES (product_id, price, latitude, longitude)
VALUES
        (6, 15.23, 150.25, 150.25),
        (7, 25.22, 250.25, 250.25);
-- Shopping_List
INSERT INTO SHOPPING_LIST (name, latitude, longitude)
VALUES
       ("OG Shopping List", 150.25, 150.25),
       ("PenguSuper Shopping List", 250.25, 250.25),
       ("TuxMarket Shopping List", 350.25, 350.25);

-- Product_Users
INSERT INTO PRODUCTS_USERS(product_id, user_id)
VALUES
       (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2),
       (3, 1),
       (3, 2),
       (3, 3),
       (4, 1),
       (4, 4),
       (5, 1),
       (5, 5);

-- Shopping_List_Users
INSERT INTO SHOPPING_LIST_USERS (shopping_list_id,user_id)
VALUES
       (1, 1),
       (2, 1),
       (3, 1),
       (1, 2),
       (1, 3);

-- Stats
INSERT INTO STATS (num_items, time, latitude, longitude)
VALUES
       (10, 60, 150.25, 150.25),
       (10, 80, 150.15, 150.15),
       (15, 90, 150.35, 150.35),
       (5, 40, 150.45, 150.45),
       (10, 60, 250.25, 250.25),
       (20, 90, 250.35, 250.35);

-- Crowd_Product_Images
-- Local_Product_Images
-- TODO
