/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import store.pengu.server.db.pengustore.tables.Pantries;
import store.pengu.server.db.pengustore.tables.PantryXUser;
import store.pengu.server.db.pengustore.tables.ProductXImage;
import store.pengu.server.db.pengustore.tables.ProductXPantry;
import store.pengu.server.db.pengustore.tables.Products;
import store.pengu.server.db.pengustore.tables.ShopXProduct;
import store.pengu.server.db.pengustore.tables.Shops;
import store.pengu.server.db.pengustore.tables.Users;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>pantries</code>.
     */
    public final Pantries PANTRIES = Pantries.PANTRIES;

    /**
     * The table <code>pantry_x_user</code>.
     */
    public final PantryXUser PANTRY_X_USER = PantryXUser.PANTRY_X_USER;

    /**
     * The table <code>product_x_image</code>.
     */
    public final ProductXImage PRODUCT_X_IMAGE = ProductXImage.PRODUCT_X_IMAGE;

    /**
     * The table <code>product_x_pantry</code>.
     */
    public final ProductXPantry PRODUCT_X_PANTRY = ProductXPantry.PRODUCT_X_PANTRY;

    /**
     * The table <code>products</code>.
     */
    public final Products PRODUCTS = Products.PRODUCTS;

    /**
     * The table <code>shop_x_product</code>.
     */
    public final ShopXProduct SHOP_X_PRODUCT = ShopXProduct.SHOP_X_PRODUCT;

    /**
     * The table <code>shops</code>.
     */
    public final Shops SHOPS = Shops.SHOPS;

    /**
     * The table <code>users</code>.
     */
    public final Users USERS = Users.USERS;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Pantries.PANTRIES,
            PantryXUser.PANTRY_X_USER,
            ProductXImage.PRODUCT_X_IMAGE,
            ProductXPantry.PRODUCT_X_PANTRY,
            Products.PRODUCTS,
            ShopXProduct.SHOP_X_PRODUCT,
            Shops.SHOPS,
            Users.USERS);
    }
}
