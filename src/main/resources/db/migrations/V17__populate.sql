-- Users
INSERT INTO USERS (username, email, password)
VALUES
       ("Tux", "Tux@pengu.com", "tux"),
       ("Pengu", "Pengu@pengu.com", "pengu"),
       ("Pinguim3", "Pinguim3@pengu.com", "pengu"),
       ("Pinguim4", "Pinguim4@pengu.com", "pengu"),
       ("Pinguim5", "Pinguim5@pengu.com", "pengu");

-- Pantries
INSERT INTO PANTRIES (code, name, latitude, longitude, color)
VALUES
       ("OGPANTRY", "OGPantry", 25.12, 21.32, "231 76 60"),
       ("TuxPantry", "TuxPantry", 56.45, 54.65, "46 204 112"),
       ("PenguPANTRY", "PenguPantry", 89.98, 87.87, "156 89 182"),
       ("Pantry4", "Pantry4", 59.95, 51.15, "231 76 60"),
       ("Pantry5", "Pantry5", 53.35, 57.75, "156 89 182");

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
       ("Couves"),
       ("Cebolas"),
       ("Pepinos");

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
       ("ice4510" , 1.69, 50.25, 150.25),
       ("ice4510" , 5.26, 50.25, 50.25),
       ("fish15468" , 1.13, 50.15, 50.15),
       ("fish15468" , 4.27, 50.25, 50.35),
       ("shirmp456564" , 6.99, 50.25, 50.25),
       ("seabird789987" , 8.89, 50.25, 50.25),
       ("crab159357" , 10.56, 50.25, 50.25 ),
       ("cebolas123", 69.420, 50.25, 150.25);

-- Local_Product_Prices
INSERT INTO LOCAL_PRODUCT_PRICES (product_id, price, latitude, longitude)
VALUES
        (6, 15.23, 50.25, 150.25),
        (7, 25.22, 50.25, 50.25);
-- Shopping_List
INSERT INTO SHOPPING_LIST (name, code, latitude, longitude, color)
VALUES
       ("OG Shopping List", "OGS", 50.25, 150.25, "230 125 34"),
       ("PenguSuper Shopping List", "PSL", 50.25, 50.25, "46 204 112"),
       ("TuxMarket Shopping List", "TSL", 50.25, 50.25, "189 195 199");

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
       (5, 5),
       (6, 1),
       (7, 1),
       (8, 1),
       (9, 1);

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
       (10, 60, 50.25, 150.25),
       (10, 80, 50.15, 150.15),
       (15, 90, 50.35, 150.35),
       (5, 40, 50.45, 150.45),
       (10, 60, 50.25, 50.25),
       (20, 90, 50.35, 50.35),
       (2, 10, 50.25, 150.25),
       (3, 12, 50.25, 150.25),
       (1, 2, 50.25, 150.25),
       (1, 4, 50.25, 150.25),
       (5, 20, 50.25, 150.25);

-- Local_Product_Images
INSERT INTO LOCAL_PRODUCT_IMAGES (product_id, image_url)
VALUES
    (1, "https://clearlakeiowa.com/wp-content/uploads/2019/11/ice-cubes-e1573665814265.jpg"),
    (2, "https://thumbs-prod.si-cdn.com/qXrJJ-l_jMrQbARjnToD0fi-Tsg=/800x600/filters:no_upscale()/https://public-media.si-cdn.com/filer/d6/93/d6939718-4e41-44a8-a8f3-d13648d2bcd0/c3npbx.jpg"),
    (3, "https://www.collinsdictionary.com/images/full/shrimp_74674555.jpg"),
    (4, "https://img.theweek.in/content/dam/week/webworld/feature/lifestyle/2017/july/worldseabirdday.jpg"),
    (5, "https://miro.medium.com/max/605/1*CXi3hdDzoMIvVi7woM05Bg@2x.jpeg"),
    (6, "https://media-manager.noticiasaominuto.com/1920/1582634326/naom_5e5514ead7999.jpg?crop_params=eyJsYW5kc2NhcGUiOnsiY3JvcFdpZHRoIjoyNDU1LCJjcm9wSGVpZ2h0IjoxMzgxLCJjcm9wWCI6MCwiY3JvcFkiOjU4fX0="),
    (7, "https://i.pinimg.com/originals/e9/47/e8/e947e87ca33e9a15ca82c516d96c52c7.jpg"),
    (8, "https://cdn1.newsplex.pt/fotos/2017/5/11/584236.jpg?type=Artigo"),
    (9, "https://belezaesaude.com/i/730/55/pepino.jpg");

-- Crowd_Product_Images
INSERT INTO CROWD_PRODUCT_IMAGES (barcode, image_url)
VALUES
    ("ice4510", "https://clearlakeiowa.com/wp-content/uploads/2019/11/ice-cubes-e1573665814265.jpg"),
    ("fish15468", "https://thumbs-prod.si-cdn.com/qXrJJ-l_jMrQbARjnToD0fi-Tsg=/800x600/filters:no_upscale()/https://public-media.si-cdn.com/filer/d6/93/d6939718-4e41-44a8-a8f3-d13648d2bcd0/c3npbx.jpg"),
    ("shirmp456564", "https://www.collinsdictionary.com/images/full/shrimp_74674555.jpg"),
    ("seabird789987", "https://img.theweek.in/content/dam/week/webworld/feature/lifestyle/2017/july/worldseabirdday.jpg"),
    ("crab159357", "https://miro.medium.com/max/605/1*CXi3hdDzoMIvVi7woM05Bg@2x.jpeg"),
    ("cebolas123", "https://media-manager.noticiasaominuto.com/1920/1582634326/naom_5e5514ead7999.jpg?crop_params=eyJsYW5kc2NhcGUiOnsiY3JvcFdpZHRoIjoyNDU1LCJjcm9wSGVpZ2h0IjoxMzgxLCJjcm9wWCI6MCwiY3JvcFkiOjU4fX0=");

-- Product Ratigns
INSERT INTO RATINGS (user_id, barcode, rating)
VALUES
    (1, "seabird789987", 2),
    (2, "seabird789987", 3),
    (1, "crab159357", 5),
    (2, "crab159357", 4),
    (3, "crab159357", 4),
    (4, "crab159357", 3),
    (5, "crab159357", 2);

-- Beacons queue noise
INSERT INTO BEACONS (num_items, latitude, longitude)
VALUES
    (15, 50.25, 150.25);