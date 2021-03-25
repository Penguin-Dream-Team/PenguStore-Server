-- Users
INSERT INTO USERS (username, email, password)
VALUES
       ("Tux", "Tux@pengu.com", "tux"),
       ("Pengu", "Pengu@pengu.com", "pengu"),
       ("Pinguim3", "Pinguim3@pengu.com", "pengu"),
       ("Pinguim4", "Pinguim4@pengu.com", "pengu"),
       ("Pinguim5", "Pinguim5@pengu.com", "pengu");

-- Pantries
INSERT INTO PANTRIES (code, name)
VALUES
       ("OGPANTRY", "OGPantry"),
       ("TuxPantry", "TuxPantry"),
       ("PenguPANTRY", "PenguPantry"),
       ("Pantry4", "Pantry4"),
       ("Pantry5", "Pantry5");

-- Products
INSERT INTO PRODUCTS (barcode, review_score, review_number, name)
VALUES
       ("ice4510", 4.5, 10, "Ice"),
       ("fish15468", 5.0, 20, "Fish"),
       ("awds23156", NULL , 0, "Shrimp"),
       ("awds23156", 3.6, 10, "Seabird"),
       ("awds23156", 0.2, 14, "Crab");

-- Shops
INSERT INTO SHOPS (name, location_x, location_y)
VALUES
       ("OGShop", 420.69, 420.69),
       ("PenguSuper", 12.45, 27.56),
       ("TuxMarket", 15.39, 155.26),
       ("Shop4", 352.26, 451.12),
       ("Shop5", 123.32, 321.21);

-- Product_x_Pantry
INSERT INTO PRODUCT_X_PANTRY (pantry_id, product_id, have_qty, want_qty)
VALUES
       (1, 1, 2, 3),
       (1, 2, 3, 3),
       (1, 3, 1, 5),
       (2, 1, 2, 6),
       (2, 2, 3, 5);

-- Pantry_x_User
INSERT INTO PANTRY_X_USER (pantry_id, user_id)
VALUES
       (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2);

-- Shop_x_Product
INSERT INTO SHOP_X_PRODUCT (shop_id, product_id, price)
VALUES
       (1 , 1, 1.69),
       (1 , 2, 5.26),
       (1 , 3, 6.79),
       (2 , 1, 1.13),
       (2 , 2, 4.27),
       (3 , 3, 6.99),
       (4 , 4, 8.89),
       (5 , 5, 10.56);

-- Product_x_Image
-- TODO