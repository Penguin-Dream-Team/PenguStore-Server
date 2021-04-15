package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.LeaveQueue
import store.pengu.server.NotFoundException
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.routes.requests.CartRequest
import store.pengu.server.routes.requests.LeaveQueueRequest
import store.pengu.server.routes.requests.PriceRequest

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
        condition = condition.and(PANTRY_PRODUCTS.WANT_QTY.ge(PANTRY_PRODUCTS.HAVE_QTY + 1))

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

        var condition2 = condition3.or(condition4)

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

    fun addPrice(price_request: PriceRequest, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        if (price_request.barcode != null) {

            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(price_request.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(price_request.latitude+1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude-1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude+1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude-1.0))

            val success = create.update(CROWD_PRODUCT_PRICES)
                .set(CROWD_PRODUCT_PRICES.PRICE, price_request.price)
                .where(condition)
                .execute() == 1

            return if (success)
                success
            else
                create.insertInto(CROWD_PRODUCT_PRICES,
                    CROWD_PRODUCT_PRICES.BARCODE, CROWD_PRODUCT_PRICES.PRICE, CROWD_PRODUCT_PRICES.LATITUDE, CROWD_PRODUCT_PRICES.LONGITUDE)
                    .values(price_request.barcode, price_request.price, price_request.latitude.toDouble(), price_request.longitude.toDouble())
                    .execute() == 1
        }

        else {
            val productId = price_request.product_id ?: throw NotFoundException("Product Id not provided")
            condition = condition.and(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(price_request.latitude+1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude-1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude+1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude-1.0))

            val success = create.update(LOCAL_PRODUCT_PRICES)
                .set(LOCAL_PRODUCT_PRICES.PRICE, price_request.price)
                .where(condition)
                .execute() == 1

            return if (success)
                success
            else
                create.insertInto(LOCAL_PRODUCT_PRICES,
                    LOCAL_PRODUCT_PRICES.PRODUCT_ID, LOCAL_PRODUCT_PRICES.PRICE, LOCAL_PRODUCT_PRICES.LATITUDE, LOCAL_PRODUCT_PRICES.LONGITUDE)
                    .values(ULong.valueOf(productId), price_request.price, price_request.latitude.toDouble(), price_request.longitude.toDouble())
                    .execute() == 1
        }

    }


    fun deletePrice(price_request: PriceRequest, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        if (price_request.barcode != null) {

            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(price_request.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(price_request.latitude+1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude-1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude+1.0))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude-1.0))

            return create.delete(CROWD_PRODUCT_PRICES)
                .where(condition)
                .execute() == 1
        }
        else {
            val productId = price_request.product_id ?: throw NotFoundException("Product Id not provided")
            condition = condition.and(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(price_request.latitude+1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude-1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude+1.0))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude-1.0))

            return create.delete(LOCAL_PRODUCT_PRICES)
                .where(condition)
                .execute() == 1
        }
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


    // Carts

    fun buyCart(cart: List<Cart>, create: DSLContext = dslContext): Boolean {
        val itr = cart.iterator()
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        itr.forEach {
            condition = DSL.noCondition()
            condition = condition.and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(it.product_id)))
            condition = condition.and(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(it.pantry_id)))

            create.update(PANTRY_PRODUCTS)
                .set(PANTRY_PRODUCTS.HAVE_QTY, PANTRY_PRODUCTS.HAVE_QTY + it.amount)
                .where(condition)
                .execute()
        }

        return true
    }


    // Queue

    fun joinQueue(latitude: Float, longitude: Float, num_items: Int, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(latitude+0.5))
        condition = condition.and(BEACONS.LATITUDE.ge(latitude-0.5))
        condition = condition.and(BEACONS.LONGITUDE.le(longitude+0.5))
        condition = condition.and(BEACONS.LONGITUDE.ge(longitude-0.5))
        val beacon = getBeacon(latitude, longitude)

        var res = false

        res = if (beacon != null) {
            create.update(BEACONS)
                .set(BEACONS.NUM_ITEMS, BEACONS.NUM_ITEMS + num_items)
                .where(condition)
                .execute() == 1
        } else {
            create.insertInto(BEACONS,
                BEACONS.NUM_ITEMS, BEACONS.LATITUDE, BEACONS.LONGITUDE)
                .values(num_items, latitude.toDouble(), longitude.toDouble())
                .execute() == 1

        }
        return res
    }

    fun leaveQueue(leaveQueueRequest: LeaveQueueRequest, create: DSLContext = dslContext) : Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(leaveQueueRequest.latitude+0.5))
        condition = condition.and(BEACONS.LATITUDE.ge(leaveQueueRequest.latitude-0.5))
        condition = condition.and(BEACONS.LONGITUDE.le(leaveQueueRequest.longitude+0.5))
        condition = condition.and(BEACONS.LONGITUDE.ge(leaveQueueRequest.longitude-0.5))

        create.insertInto(STATS,
            STATS.NUM_ITEMS, STATS.TIME, STATS.LATITUDE, STATS.LONGITUDE)
            .values(leaveQueueRequest.num_items, leaveQueueRequest.time, leaveQueueRequest.latitude.toDouble(), leaveQueueRequest.longitude.toDouble())
            .execute()

        return create.update(BEACONS)
            .set(BEACONS.NUM_ITEMS, BEACONS.NUM_ITEMS - leaveQueueRequest.num_items)
            .where(condition)
            .execute() == 1
    }

    /*
    fun timeQueue(latitude: Float, longitude: Float, create: DSLContext = dslContext): Int {

    }

     */


    // Aux

    fun connectShoppingList(shopping_list_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            SHOPPING_LIST_USERS,
            SHOPPING_LIST_USERS.SHOPPING_LIST_ID, SHOPPING_LIST_USERS.USER_ID
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

    fun getBeacon(latitude: Float, longitude: Float, create: DSLContext = dslContext): Beacon? {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(latitude+0.5))
        condition = condition.and(BEACONS.LATITUDE.ge(latitude-0.5))
        condition = condition.and(BEACONS.LONGITUDE.le(longitude+0.5))
        condition = condition.and(BEACONS.LONGITUDE.ge(longitude-0.5))

        return create.select()
            .from(BEACONS)
            .where(condition)
            .fetchOne()?.map {
                Beacon(
                    num_items = it[BEACONS.NUM_ITEMS],
                    latitude = it[BEACONS.LATITUDE].toFloat(),
                    longitude = it[BEACONS.LONGITUDE].toFloat()
                )
            }
    }
}