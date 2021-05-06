/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import store.pengu.server.db.pengustore.tables.Beacons;
import store.pengu.server.db.pengustore.tables.CrowdProductImages;
import store.pengu.server.db.pengustore.tables.CrowdProductPrices;
import store.pengu.server.db.pengustore.tables.LocalProductImages;
import store.pengu.server.db.pengustore.tables.LocalProductPrices;
import store.pengu.server.db.pengustore.tables.Pantries;
import store.pengu.server.db.pengustore.tables.PantriesUsers;
import store.pengu.server.db.pengustore.tables.PantryProducts;
import store.pengu.server.db.pengustore.tables.Products;
import store.pengu.server.db.pengustore.tables.ProductsUsers;
import store.pengu.server.db.pengustore.tables.Ratings;
import store.pengu.server.db.pengustore.tables.ShoppingList;
import store.pengu.server.db.pengustore.tables.ShoppingListUsers;
import store.pengu.server.db.pengustore.tables.Stats;
import store.pengu.server.db.pengustore.tables.Suggestions;
import store.pengu.server.db.pengustore.tables.Translation;
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
     * The table <code>beacons</code>.
     */
    public final Beacons BEACONS = Beacons.BEACONS;

    /**
     * The table <code>crowd_product_images</code>.
     */
    public final CrowdProductImages CROWD_PRODUCT_IMAGES = CrowdProductImages.CROWD_PRODUCT_IMAGES;

    /**
     * The table <code>crowd_product_prices</code>.
     */
    public final CrowdProductPrices CROWD_PRODUCT_PRICES = CrowdProductPrices.CROWD_PRODUCT_PRICES;

    /**
     * The table <code>local_product_images</code>.
     */
    public final LocalProductImages LOCAL_PRODUCT_IMAGES = LocalProductImages.LOCAL_PRODUCT_IMAGES;

    /**
     * The table <code>local_product_prices</code>.
     */
    public final LocalProductPrices LOCAL_PRODUCT_PRICES = LocalProductPrices.LOCAL_PRODUCT_PRICES;

    /**
     * The table <code>pantries</code>.
     */
    public final Pantries PANTRIES = Pantries.PANTRIES;

    /**
     * The table <code>pantries_users</code>.
     */
    public final PantriesUsers PANTRIES_USERS = PantriesUsers.PANTRIES_USERS;

    /**
     * The table <code>pantry_products</code>.
     */
    public final PantryProducts PANTRY_PRODUCTS = PantryProducts.PANTRY_PRODUCTS;

    /**
     * The table <code>products</code>.
     */
    public final Products PRODUCTS = Products.PRODUCTS;

    /**
     * The table <code>products_users</code>.
     */
    public final ProductsUsers PRODUCTS_USERS = ProductsUsers.PRODUCTS_USERS;

    /**
     * The table <code>ratings</code>.
     */
    public final Ratings RATINGS = Ratings.RATINGS;

    /**
     * The table <code>shopping_list</code>.
     */
    public final ShoppingList SHOPPING_LIST = ShoppingList.SHOPPING_LIST;

    /**
     * The table <code>shopping_list_users</code>.
     */
    public final ShoppingListUsers SHOPPING_LIST_USERS = ShoppingListUsers.SHOPPING_LIST_USERS;

    /**
     * The table <code>stats</code>.
     */
    public final Stats STATS = Stats.STATS;

    /**
     * The table <code>suggestions</code>.
     */
    public final Suggestions SUGGESTIONS = Suggestions.SUGGESTIONS;

    /**
     * The table <code>translation</code>.
     */
    public final Translation TRANSLATION = Translation.TRANSLATION;

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
            Beacons.BEACONS,
            CrowdProductImages.CROWD_PRODUCT_IMAGES,
            CrowdProductPrices.CROWD_PRODUCT_PRICES,
            LocalProductImages.LOCAL_PRODUCT_IMAGES,
            LocalProductPrices.LOCAL_PRODUCT_PRICES,
            Pantries.PANTRIES,
            PantriesUsers.PANTRIES_USERS,
            PantryProducts.PANTRY_PRODUCTS,
            Products.PRODUCTS,
            ProductsUsers.PRODUCTS_USERS,
            Ratings.RATINGS,
            ShoppingList.SHOPPING_LIST,
            ShoppingListUsers.SHOPPING_LIST_USERS,
            Stats.STATS,
            Suggestions.SUGGESTIONS,
            Translation.TRANSLATION,
            Users.USERS);
    }
}
