package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.Pantry
import store.pengu.server.data.Pantry_x_User
import store.pengu.server.data.ProductInPantry
import store.pengu.server.data.Product_x_Pantry
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantryXUser
import store.pengu.server.db.pengustore.tables.ProductXPantry.PRODUCT_X_PANTRY
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS

class PantryDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun getPantries(create: DSLContext = dslContext): List<Pantry> {
        return create.select()
            .from(PANTRIES)
            .fetch().map {
                Pantry(
                    id = it[PANTRIES.PANTRY_ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat()
                )
            }

    }

    fun getPantry(id: Long, create: DSLContext = dslContext): Pantry? {
        return create.select()
            .from(PANTRIES)
            .where(PANTRIES.PANTRY_ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Pantry(
                    id = it[PANTRIES.PANTRY_ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat()
                )
            }
    }

    fun addPantry(pantry: Pantry, create: DSLContext = dslContext): Boolean {
        return create.insertInto(PANTRIES,
                PANTRIES.CODE, PANTRIES.NAME, PANTRIES.LATITUDE, PANTRIES.LONGITUDE)
            .values(pantry.code, pantry.name, pantry.latitude.toDouble(), pantry.longitude.toDouble())
            .execute() == 1
    }

    fun updatePantry(pantry: Pantry, create: DSLContext = dslContext): Boolean {
        return create.update(PANTRIES)
            .set(PANTRIES.CODE, pantry.code)
            .set(PANTRIES.NAME, pantry.name)
            .set(PANTRIES.LATITUDE, pantry.latitude.toDouble())
            .set(PANTRIES.LONGITUDE, pantry.longitude.toDouble())
            .where(PANTRIES.PANTRY_ID.eq(ULong.valueOf(pantry.id)))
            .execute() == 1
    }

    fun addProductToPantry(product_x_pantry: Product_x_Pantry, create: DSLContext = dslContext): Boolean {
        return create.insertInto(PRODUCT_X_PANTRY,
            PRODUCT_X_PANTRY.PRODUCT_ID, PRODUCT_X_PANTRY.PANTRY_ID, PRODUCT_X_PANTRY.WANT_QTY, PRODUCT_X_PANTRY.HAVE_QTY)
            .values(product_x_pantry.product_id, product_x_pantry.pantry_id, product_x_pantry.want_qty, product_x_pantry.have_qty)
            .execute() == 1
    }

    fun updateProductPantry(product_x_pantry: Product_x_Pantry, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PRODUCT_X_PANTRY.PRODUCT_ID.eq(product_x_pantry.product_id))
        condition = condition.and(PRODUCT_X_PANTRY.PANTRY_ID.eq(product_x_pantry.pantry_id))

        return create.update(PRODUCT_X_PANTRY)
            .set(PRODUCT_X_PANTRY.WANT_QTY, product_x_pantry.want_qty)
            .set(PRODUCT_X_PANTRY.HAVE_QTY, product_x_pantry.have_qty)
            .where(condition)
            .execute() == 1
    }

    fun deleteProductPantry(product_x_pantry: Product_x_Pantry, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PRODUCT_X_PANTRY.PRODUCT_ID.eq(product_x_pantry.product_id))
        condition = condition.and(PRODUCT_X_PANTRY.PANTRY_ID.eq(product_x_pantry.pantry_id))

        return create.delete(PRODUCT_X_PANTRY)
            .where(condition)
            .execute() == 1
    }

    fun getProductsInPantry (pantry_id: Long, create: DSLContext = dslContext): List<ProductInPantry> {
        return create.select()
            .from(PANTRIES)
            .join(PRODUCT_X_PANTRY).using(PANTRIES.PANTRY_ID)
            .join(PRODUCTS).using(PRODUCT_X_PANTRY.PRODUCT_ID)
            .where(PANTRIES.PANTRY_ID.eq((ULong.valueOf(pantry_id))))
            .fetch().map {
                ProductInPantry(
                    productId = it[PRODUCTS.PRODUCT_ID].toLong(),
                    pantryId = it[PANTRIES.PANTRY_ID].toLong(),
                    barcode = it[PRODUCTS.BARCODE],
                    name = it[PRODUCTS.NAME],
                    reviewNumber = it[PRODUCTS.REVIEW_NUMBER],
                    reviewScore = it[PRODUCTS.REVIEW_SCORE],
                    amountAvailable = it[PRODUCT_X_PANTRY.HAVE_QTY],
                    amountNeeded = it[PRODUCT_X_PANTRY.WANT_QTY]
                )
            }
    }

    fun addPantryToUser(pantry_x_user: Pantry_x_User, create: DSLContext = dslContext): Boolean {
        return  create.insertInto(
            PantryXUser.PANTRY_X_USER,
            PantryXUser.PANTRY_X_USER.PANTRY_ID, PantryXUser.PANTRY_X_USER.USER_ID)
            .values(pantry_x_user.pantryId, pantry_x_user.userId)
            .execute() == 1
    }

    fun getPantryByCode(code: String, create: DSLContext = dslContext): Pantry? {
        return create.select()
            .from(Pantries.PANTRIES)
            .where(Pantries.PANTRIES.CODE.eq(code))
            .fetchOne()?.map {
                Pantry(
                    id = it[Pantries.PANTRIES.PANTRY_ID].toLong(),
                    code = it[Pantries.PANTRIES.CODE],
                    name = it[Pantries.PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat()
                )
            }
    }
}