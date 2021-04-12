package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.NotFoundException
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS

class ShopDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    // Shopping Lists

    fun addShoppingList(shopping_list: Shopping_list, create: DSLContext = dslContext): Shopping_list? {
        return create.insertInto(SHOPPING_LIST,
            SHOPPING_LIST.NAME, SHOPPING_LIST.LATITUDE, SHOPPING_LIST.LONGITUDE)
            .values(shopping_list.name, shopping_list.latitude.toDouble(), shopping_list.longitude.toDouble())
            .returningResult(SHOPPING_LIST.ID, SHOPPING_LIST.NAME, SHOPPING_LIST.LATITUDE, SHOPPING_LIST.LONGITUDE)
            .fetchOne()?.map {
                Shopping_list(
                    id = it[SHOPPING_LIST.ID].toLong(),
                    name = it[SHOPPING_LIST.NAME],
                    latitude = it[SHOPPING_LIST.LATITUDE].toFloat(),
                    longitude = it[SHOPPING_LIST.LONGITUDE].toFloat()
                )
            }
    }

    fun updateShoppingList(shopping_list: Shopping_list, create: DSLContext = dslContext): Boolean {
        return create.update(SHOPPING_LIST)
            .set(SHOPPING_LIST.NAME, shopping_list.name)
            .set(SHOPPING_LIST.LATITUDE, shopping_list.latitude.toDouble())
            .set(SHOPPING_LIST.LONGITUDE, shopping_list.longitude.toDouble())
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(shopping_list.id)))
            .execute() == 1
    }


    fun getShoppingList(id: Long, create: DSLContext = dslContext): Shopping_list? {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Shopping_list(
                    id = it[SHOPPING_LIST.ID].toLong(),
                    name = it[SHOPPING_LIST.NAME],
                    latitude = it[SHOPPING_LIST.LATITUDE].toFloat(),
                    longitude = it[SHOPPING_LIST.LONGITUDE].toFloat()
                )
            }
    }

    fun genShoppingList(user_id: Long, latitude: Float, longitude: Float, create: DSLContext = dslContext): List<ProductInShoppingList> {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(USERS.ID.eq(ULong.valueOf(user_id)))

        var condition2 = DSL.noCondition() // Alternatively, use trueCondition()

        var condition3 = DSL.noCondition() // Alternatively, use trueCondition()
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LATITUDE.le(latitude + 0.5))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(latitude - 0.5))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(longitude + 0.5))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(longitude - 0.5))

        var condition4 = DSL.noCondition() // Alternatively, use trueCondition()
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(latitude + 0.5))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(latitude - 0.5))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(longitude + 0.5))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(longitude - 0.5))

        condition2 = condition3.or(condition4)

        condition = condition.and(condition2)

        return create.select()
            .from(USERS)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
            .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
            .leftJoin(CROWD_PRODUCT_PRICES).on(CROWD_PRODUCT_PRICES.BARCODE.eq(PRODUCTS.BARCODE))
            .leftJoin(LOCAL_PRODUCT_PRICES).on(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(condition)
            .fetch().map {
                ProductInShoppingList(
                    product_id = it[PRODUCTS.ID].toLong(),
                    pantry_id = it[PANTRIES.ID].toLong(),
                    product_name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY],
                    price = price(it[PRODUCTS.BARCODE], it[CROWD_PRODUCT_PRICES.PRICE], it[LOCAL_PRODUCT_PRICES.PRICE])
                )
            }
    }

    // Prices

    fun addPrice(crowd_Product_Price: Crowd_Product_Price, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(crowd_Product_Price.barcode))
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(crowd_Product_Price.latitude+1.0))
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(crowd_Product_Price.latitude-1.0))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(crowd_Product_Price.longitude+1.0))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(crowd_Product_Price.longitude-1.0))

        val sucess = create.update(CROWD_PRODUCT_PRICES)
            .set(CROWD_PRODUCT_PRICES.PRICE, crowd_Product_Price.price)
            .where(condition)
            .execute() == 1

        return if (sucess)
            sucess
        else
            create.insertInto(CROWD_PRODUCT_PRICES,
                CROWD_PRODUCT_PRICES.BARCODE, CROWD_PRODUCT_PRICES.PRICE, CROWD_PRODUCT_PRICES.LATITUDE, CROWD_PRODUCT_PRICES.LONGITUDE)
                .values(crowd_Product_Price.barcode, crowd_Product_Price.price, crowd_Product_Price.latitude.toDouble(), crowd_Product_Price.longitude.toDouble())
                .execute() == 1
    }


    fun deletePrice(crowd_Product_Price: Crowd_Product_Price, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(crowd_Product_Price.barcode))
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.eq(crowd_Product_Price.latitude.toDouble()))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.eq(crowd_Product_Price.longitude.toDouble()))

        return create.delete(CROWD_PRODUCT_PRICES)
            .where(condition)
            .execute() == 1
    }

    fun getShopPrices(latitude: Float, longitude: Float, create: DSLContext = dslContext): List<ProductInShop> {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(latitude+0.5))
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(latitude-0.5))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(longitude+0.5))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(longitude-0.5))

        return create.select()
            .from(CROWD_PRODUCT_PRICES)
            .join(PRODUCTS).on(PRODUCTS.BARCODE.eq(CROWD_PRODUCT_PRICES.BARCODE))
            .where(condition)
            .fetch().map {
                ProductInShop(
                    product_id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    price = it[CROWD_PRODUCT_PRICES.PRICE]

                )
            }
    }


    // Aux

    fun connectShoppingList(shopping_list_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            Tables.SHOPPING_LIST_USERS,
            Tables.SHOPPING_LIST_USERS.SHOPPING_LIST_ID, Tables.SHOPPING_LIST_USERS.USER_ID
        )
            .values(ULong.valueOf(shopping_list_id), ULong.valueOf(user_id))
            .execute() == 1
    }



    fun price(barcode: String?, crowd_price: Double?, local_price:Double? ): Double {
        return if (barcode != null)
            crowd_price ?: throw NotFoundException("Crowd Price not found")
        else
            local_price ?: throw NotFoundException("Local Price not found")
    }
}