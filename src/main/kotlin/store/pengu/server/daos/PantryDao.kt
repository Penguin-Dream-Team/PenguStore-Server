package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantriesUsers
import store.pengu.server.db.pengustore.tables.PantryProducts.PANTRY_PRODUCTS
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.routes.requests.PantryRequest
import kotlin.reflect.jvm.internal.impl.resolve.constants.ULongValue

class PantryDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    // Pantries

    fun addPantry(pantry: PantryRequest, create: DSLContext = dslContext): Pantry? {
        val charPool = (('A'..'Z') + ('0'..'9')).toMutableList()

        val randomString = (1..8)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        return create.insertInto(
            PANTRIES,
            PANTRIES.CODE, PANTRIES.NAME, PANTRIES.LATITUDE, PANTRIES.LONGITUDE
        )
            .values(randomString, pantry.name, pantry.latitude.toDouble(), pantry.longitude.toDouble())
            .returningResult(PANTRIES.ID, PANTRIES.CODE, PANTRIES.NAME, PANTRIES.LATITUDE, PANTRIES.LONGITUDE)
            .fetchOne()?.map {
                Pantry(
                    id = it[PANTRIES.ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat(),
                    product_num = 0
                )
            }
    }

    fun updatePantry(pantry: PantryRequest, create: DSLContext = dslContext): Boolean {
        return create.update(PANTRIES)
            .set(PANTRIES.CODE, pantry.code)
            .set(PANTRIES.NAME, pantry.name)
            .set(PANTRIES.LATITUDE, pantry.latitude.toDouble())
            .set(PANTRIES.LONGITUDE, pantry.longitude.toDouble())
            .where(PANTRIES.ID.eq(ULong.valueOf(pantry.id)))
            .execute() == 1
    }

    fun getPantry(id: Long, create: DSLContext = dslContext): Pantry? {
        return create.select()
            .from(PANTRIES)
            .where(PANTRIES.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Pantry(
                    id = it[PANTRIES.ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat(),
                    product_num = create.fetchCount(
                        DSL.select()
                            .from(PANTRIES)
                            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
                            .where(PANTRIES.ID.eq(it[PANTRIES.ID]))
                    )
                )
            }
    }


    // Pantry Products

    fun addPantryProduct(pantryProduct: Pantry_Product, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            PANTRY_PRODUCTS,
            PANTRY_PRODUCTS.PANTRY_ID,
            PANTRY_PRODUCTS.PRODUCT_ID,
            PANTRY_PRODUCTS.WANT_QTY,
            PANTRY_PRODUCTS.HAVE_QTY
        )
            .values(
                ULong.valueOf(pantryProduct.pantry_id),
                ULong.valueOf(pantryProduct.product_id),
                pantryProduct.want_qty,
                pantryProduct.have_qty
            )
            .execute() == 1
    }

    fun updatePantryProduct(pantryProduct: Pantry_Product, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(pantryProduct.product_id)))
        condition = condition.and(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(pantryProduct.pantry_id)))

        return create.update(PANTRY_PRODUCTS)
            .set(PANTRY_PRODUCTS.WANT_QTY, pantryProduct.want_qty)
            .set(PANTRY_PRODUCTS.HAVE_QTY, pantryProduct.have_qty)
            .where(condition)
            .execute() == 1
    }

    fun deletePantryProduct(pantry_id: Long, product_id: Long, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(product_id)))
        condition = condition.and(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(pantry_id)))

        return create.delete(PANTRY_PRODUCTS)
            .where(condition)
            .execute() == 1
    }

    fun getPantryProducts(pantry_id: Long, create: DSLContext = dslContext): List<ProductInPantry> {
        return create.select()
            .from(PANTRIES)
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
            .where(PANTRIES.ID.eq((ULong.valueOf(pantry_id))))
            .fetch().map {
                ProductInPantry(
                    productId = it[PRODUCTS.ID].toLong(),
                    pantryId = it[PANTRIES.ID].toLong(),
                    barcode = it[PRODUCTS.BARCODE],
                    name = it[PRODUCTS.NAME],
                    amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY]
                )
            }
    }

    // Aux

    fun connectPantryToUser(pantry_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            PantriesUsers.PANTRIES_USERS,
            PantriesUsers.PANTRIES_USERS.PANTRY_ID, PantriesUsers.PANTRIES_USERS.USER_ID
        )
            .values(ULong.valueOf(pantry_id), ULong.valueOf(user_id))
            .execute() == 1
    }

}