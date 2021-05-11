package store.pengu.server.daos

import org.jooq.*
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables.PANTRIES_USERS
import store.pengu.server.db.pengustore.Tables.PRODUCTS_USERS
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantryProducts.PANTRY_PRODUCTS
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.routes.requests.CreateListRequest
import store.pengu.server.routes.requests.PantryRequest

class PantryDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    companion object {
        private fun generateCode(): String {
            val charPool = (('A'..'Z') + ('0'..'9')).toMutableList()

            return (1..8)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        fun getPantryInformation(it: Record, create: DSLContext): Pantry {
            return Pantry(
                id = it[PANTRIES.ID].toLong(),
                name = it[PANTRIES.NAME],
                code = it[PANTRIES.CODE],
                latitude = it[PANTRIES.LATITUDE],
                longitude = it[PANTRIES.LONGITUDE],
                productCount = create.fetchCount(
                    create.select()
                        .from(PANTRIES)
                        .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
                        .where(PANTRIES.ID.eq(it[PANTRIES.ID]))
                ),
                color = it[PANTRIES.COLOR],
                shared = create.fetchCount(PANTRIES_USERS.where(PANTRIES_USERS.PANTRY_ID.eq(it[PANTRIES.ID]))) > 1
            )
        }
    }

    fun listPantries(userId: Long, create: DSLContext = dslContext): List<Pantry> {
        return create.select()
            .from(PANTRIES)
            .join(PANTRIES_USERS).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .where(PANTRIES_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .fetch().map {
                Pantry(
                    id = it[PANTRIES.ID].toLong(),
                    name = it[PANTRIES.NAME],
                    code = it[PANTRIES.CODE],
                    latitude = it[PANTRIES.LATITUDE],
                    longitude = it[PANTRIES.LONGITUDE],
                    productCount = create.fetchCount(PANTRY_PRODUCTS.where(PANTRY_PRODUCTS.PANTRY_ID.eq(it[PANTRIES.ID]))),
                    color = it[PANTRIES.COLOR],
                    shared = create.fetchCount(PANTRIES_USERS.where(PANTRIES_USERS.PANTRY_ID.eq(it[PANTRIES.ID]))) > 1
                )
            }
    }

    fun createPantry(listRequest: CreateListRequest, userId: Long, create: DSLContext = dslContext): Pantry {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            val pantry = transaction.insertInto(
                PANTRIES,
                PANTRIES.CODE,
                PANTRIES.NAME,
                PANTRIES.LATITUDE,
                PANTRIES.LONGITUDE,
                PANTRIES.COLOR
            )
                .values(
                    generateCode(),
                    listRequest.name,
                    listRequest.latitude,
                    listRequest.longitude,
                    listRequest.color
                )
                .returningResult(
                    PANTRIES.ID,
                    PANTRIES.CODE,
                    PANTRIES.NAME,
                    PANTRIES.LATITUDE,
                    PANTRIES.LONGITUDE,
                    PANTRIES.COLOR
                )
                .fetchOne()?.map {
                    Pantry(
                        id = it[PANTRIES.ID].toLong(),
                        name = it[PANTRIES.NAME],
                        code = it[PANTRIES.CODE],
                        latitude = it[PANTRIES.LATITUDE],
                        longitude = it[PANTRIES.LONGITUDE],
                        productCount = 0,
                        color = it[PANTRIES.COLOR],
                        shared = false
                    )
                } ?: throw InternalServerErrorException("An error occurred")

            try {
                transaction.insertInto(PANTRIES_USERS, PANTRIES_USERS.USER_ID, PANTRIES_USERS.PANTRY_ID)
                    .values(ULong.valueOf(userId), ULong.valueOf(pantry.id))
                    .execute()
            } catch (e: Exception) {
                throw InternalServerErrorException("An error occurred")
            }

            pantry
        }
    }

    fun getPantryByCode(code: String, create: DSLContext = dslContext): Pantry {
        return create.select()
            .from(PANTRIES)
            .where(PANTRIES.CODE.eq(code))
            .fetchOne()?.map {
                getPantryInformation(it, create)
            } ?: throw NotFoundException("Pantry with specified code not found")
    }

    fun addUserToPantry(pantryId: Long, userId: Long, create: DSLContext = dslContext) {
        create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)

            try {
                transaction.insertInto(PANTRIES_USERS, PANTRIES_USERS.USER_ID, PANTRIES_USERS.PANTRY_ID)
                    .values(ULong.valueOf(userId), ULong.valueOf(pantryId))
                    .execute()
            } catch (e: DataAccessException) {
                throw InternalServerErrorException("An error occurred")
            }

            val products = transaction.select(PANTRY_PRODUCTS.PRODUCT_ID)
                .from(PANTRY_PRODUCTS)
                .where(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(pantryId)))
                .fetch().map {
                    it[PANTRY_PRODUCTS.PRODUCT_ID]
                }

            products.forEach {
                try {
                    transaction.insertInto(PRODUCTS_USERS, PRODUCTS_USERS.USER_ID, PRODUCTS_USERS.PRODUCT_ID)
                        .values(ULong.valueOf(userId), it)
                        .onDuplicateKeyIgnore()
                        .execute()
                } catch (e: DataAccessException) {
                    throw InternalServerErrorException("An error occurred")
                }
            }
        }
    }

    fun userHasPantry(pantryId: Long, userId: Long, create: DSLContext = dslContext): Boolean {
        return create.fetchExists(
            PANTRIES_USERS.where(
                PANTRIES_USERS.PANTRY_ID.eq(ULong.valueOf(pantryId)).and(
                    PANTRIES_USERS.USER_ID.eq(ULong.valueOf(userId))
                )
            )
        )
    }

    fun getPantryProducts(pantryId: Long, requestUrl: String, create: DSLContext = dslContext): List<ProductInPantry> {
        return create.select()
            .from(PANTRIES)
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
            .where(PANTRIES.ID.eq((ULong.valueOf(pantryId))))
            .fetch().map {
                ProductInPantry(
                    id = it[PRODUCTS.ID].toLong(),
                    listId = it[PANTRIES.ID].toLong(),
                    barcode = it[PRODUCTS.BARCODE],
                    name = it[PRODUCTS.NAME],
                    amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY],
                    image = ProductDao.image(
                        barcode = it[PRODUCTS.BARCODE],
                        id = it[PRODUCTS.ID].toLong(),
                        requestUrl,
                        create
                    )
                )
            }
    }


    /**
     *
     */

    fun updatePantry(pantry: PantryRequest, create: DSLContext = dslContext): Boolean {
        return create.update(PANTRIES)
            .set(PANTRIES.CODE, pantry.code)
            .set(PANTRIES.NAME, pantry.name)
            .set(PANTRIES.LATITUDE, pantry.latitude)
            .set(PANTRIES.LONGITUDE, pantry.longitude)
            .where(PANTRIES.ID.eq(ULong.valueOf(pantry.id)))
            .execute() == 1
    }

    fun getPantry(id: Long, create: DSLContext = dslContext): Pantry? {
        return create.select()
            .from(PANTRIES)
            .where(PANTRIES.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                getPantryInformation(it, create)
            }
    }


    // Pantry Products

    fun addPantryProduct(pantryProduct: PantryProduct, create: DSLContext = dslContext): Boolean {
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

    fun updatePantryProduct(pantryProduct: PantryProduct, create: DSLContext = dslContext): Boolean {
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

}