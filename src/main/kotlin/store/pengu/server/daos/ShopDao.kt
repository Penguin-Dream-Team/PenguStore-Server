package store.pengu.server.daos

import org.jetbrains.annotations.NotNull
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.ProductInShop
import store.pengu.server.data.Shop
import store.pengu.server.data.Shop_x_Product
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.tables.PantryXUser
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.ShopXProduct.SHOP_X_PRODUCT
import store.pengu.server.db.pengustore.tables.Shops.SHOPS
import store.pengu.server.db.pengustore.tables.Users.USERS
import store.pengu.server.db.pengustore.tables.records.UsersRecord

class ShopDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun getShops(create: DSLContext = dslContext): List<Shop> {
        return create.select()
            .from(SHOPS)
            .fetch().map {
                Shop(
                    id = it[SHOPS.SHOP_ID].toLong(),
                    name = it[SHOPS.NAME],
                    location_x = it[SHOPS.LOCATION_X].toFloat(),
                    location_y = it[SHOPS.LOCATION_Y].toFloat()
                )
            }

    }

    fun getShop(id: Long, create: DSLContext = dslContext): Shop? {
        return create.select()
            .from(SHOPS)
            .where(SHOPS.SHOP_ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Shop(
                    id = it[SHOPS.SHOP_ID].toLong(),
                    name = it[SHOPS.NAME],
                    location_x = it[SHOPS.LOCATION_X].toFloat(),
                    location_y = it[SHOPS.LOCATION_Y].toFloat()
                )
            }
    }

    fun addShop(shop: Shop, create: DSLContext = dslContext): Boolean {
        return create.insertInto(SHOPS,
            SHOPS.NAME, SHOPS.LOCATION_X, SHOPS.LOCATION_Y)
            .values(shop.name, shop.location_x.toDouble(), shop.location_y.toDouble())
            .execute() == 1
    }

    fun updateShop(shop: Shop, create: DSLContext = dslContext): Boolean {
        return create.update(SHOPS)
            .set(SHOPS.NAME, shop.name)
            .set(SHOPS.LOCATION_X, shop.location_x.toDouble())
            .set(SHOPS.LOCATION_Y, shop.location_y.toDouble())
            .where(SHOPS.SHOP_ID.eq(ULong.valueOf(shop.id)))
            .execute() == 1
    }

    fun addProductToShop(shop_x_product: Shop_x_Product, create: DSLContext = dslContext): Boolean {
        return create.insertInto(SHOP_X_PRODUCT,
            SHOP_X_PRODUCT.SHOP_ID, SHOP_X_PRODUCT.PRODUCT_ID, SHOP_X_PRODUCT.PRICE)
            .values(shop_x_product.shop_id, shop_x_product.product_id, shop_x_product.price)
            .execute() == 1
    }

    fun updateProductShop(shop_x_product: Shop_x_Product, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOP_X_PRODUCT.SHOP_ID.eq(shop_x_product.shop_id))
        condition = condition.and(SHOP_X_PRODUCT.PRODUCT_ID.eq(shop_x_product.product_id))

        return create.update(SHOP_X_PRODUCT)
            .set(SHOP_X_PRODUCT.PRICE, shop_x_product.price)
            .where(condition)
            .execute() == 1
    }

    fun deleteProductShop(shop_x_product: Shop_x_Product, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOP_X_PRODUCT.SHOP_ID.eq(shop_x_product.shop_id))
        condition = condition.and(SHOP_X_PRODUCT.PRODUCT_ID.eq(shop_x_product.product_id))

        return create.delete(SHOP_X_PRODUCT)
            .where(condition)
            .execute() == 1
    }

    fun getShopProducts(shop_id: Long, create: DSLContext = dslContext): List<ProductInShop> {
        return create.select()
            .from(SHOPS)
            .join(SHOP_X_PRODUCT).using(SHOPS.SHOP_ID)
            .join(PRODUCTS).using(SHOP_X_PRODUCT.PRODUCT_ID)
            .where(SHOPS.SHOP_ID.eq(ULong.valueOf(shop_id)))
            .fetch().map {
                ProductInShop(
                    product_id = it[PRODUCTS.PRODUCT_ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    review_score = it[PRODUCTS.REVIEW_SCORE],
                    review_number = it[PRODUCTS.REVIEW_NUMBER],
                    price = it[SHOP_X_PRODUCT.PRICE]

                )
            }
    }

}