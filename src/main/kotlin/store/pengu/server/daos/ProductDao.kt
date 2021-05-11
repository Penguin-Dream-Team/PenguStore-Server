package store.pengu.server.daos

import io.ktor.http.*
import org.jetbrains.annotations.NotNull
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.jooq.types.ULong
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.data.LocalProductPrice
import store.pengu.server.data.Product
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.CrowdProductPrices.CROWD_PRODUCT_PRICES
import store.pengu.server.db.pengustore.tables.LocalProductImages
import store.pengu.server.db.pengustore.tables.LocalProductPrices.LOCAL_PRODUCT_PRICES
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.ProductsUsers
import store.pengu.server.db.pengustore.tables.records.LocalProductImagesRecord
import store.pengu.server.routes.requests.ImageRequest

class ProductDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    companion object {
        fun price(barcode: String?, crowd_price: Double?, local_price: Double?): Double {
            return if (barcode != null)
                crowd_price ?: throw NotFoundException("Crowd Price not found")
            else
                local_price ?: throw NotFoundException("Local Price not found")
        }

        fun image(barcode: String?, id: Long, requestUrl: String, create: DSLContext): String? {
            val img = barcode?.let { bc ->
                create.select(CROWD_PRODUCT_IMAGES.IMAGE_URL)
                    .from(CROWD_PRODUCT_IMAGES)
                    .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(bc))
                    .limit(1)
                    .fetchOne()?.map { it[CROWD_PRODUCT_IMAGES.IMAGE_URL] }
            } ?: create.select(LOCAL_PRODUCT_IMAGES.IMAGE_URL)
                .from(LOCAL_PRODUCT_IMAGES)
                .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(ULong.valueOf(id)))
                .limit(1)
                .fetchOne()?.map { it[LOCAL_PRODUCT_IMAGES.IMAGE_URL] }
            return img?.run {
                if (this.startsWith("http")) this else "$requestUrl/$this"
            }
        }

        fun getProductInformation(userId: Long, it: Record, requestUrl: String, create: DSLContext): Product {
            var ratings = emptyList<Int>()
            var userRating = 0
            var productRating = 0f

            val id = it[PRODUCTS.ID].toLong()
            val barcode = it[PRODUCTS.BARCODE]
            barcode?.let {
                ratings = getProductRatings(it, create)
                userRating = getUserRating(userId, barcode, create)
                if (ratings.isNotEmpty()) {
                    productRating = ratings.sum().toFloat() / ratings.size
                }
            }

            return Product(
                id = id,
                name = it[PRODUCTS.NAME],
                barcode = barcode,
                productRating = productRating,
                userRating = userRating,
                ratings = ratings,
                image = image(barcode, id, requestUrl, create)
            )
        }

        private fun getUserRating(userId: Long, barcode: String, create: DSLContext): Int {
            return create.select()
                .from(RATINGS)
                .where(RATINGS.USER_ID.eq(ULong.valueOf(userId)))
                .and(RATINGS.BARCODE.eq(barcode))
                .fetchOne()?.map {
                    it[RATINGS.RATING]
                } ?: 0
        }

        private fun getProductRatings(barcode: String, create: DSLContext): List<Int> {
            return create.select()
                .from(RATINGS)
                .where(RATINGS.BARCODE.eq(barcode))
                .fetch().map {
                    it[RATINGS.RATING]
                }
        }
    }

    fun getAllProducts(userId: Long, requestUrl: String, create: DSLContext = dslContext): List<Product> {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            transaction.select()
                .from(PRODUCTS_USERS)
                .join(PRODUCTS).on(PRODUCTS.ID.eq(PRODUCTS_USERS.PRODUCT_ID))
                .where(PRODUCTS_USERS.USER_ID.eq(ULong.valueOf(userId)))
                .fetch().map {
                    getProductInformation(userId, it, requestUrl, transaction)
                }
        }
    }

    fun createProduct(
        userId: Long,
        name: String,
        barcode: String?,
        image: String?,
        requestUrl: String,
        create: DSLContext = dslContext
    ): Product {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            val product = transaction.insertInto(PRODUCTS, PRODUCTS.NAME, PRODUCTS.BARCODE)
                .values(name, barcode)
                .returningResult(PRODUCTS.ID, PRODUCTS.NAME, PRODUCTS.BARCODE)
                .fetchOne()?.map {
                    Product(
                        id = it[PRODUCTS.ID].toLong(),
                        name = it[PRODUCTS.NAME],
                        barcode = it[PRODUCTS.BARCODE],
                        productRating = 0f,
                        userRating = 0,
                        ratings = emptyList(),
                        image = null
                    )
                } ?: throw InternalServerErrorException("Could not create product")

            transaction.insertInto(PRODUCTS_USERS, PRODUCTS_USERS.PRODUCT_ID, PRODUCTS_USERS.USER_ID)
                .values(ULong.valueOf(product.id), ULong.valueOf(userId))
                .execute()

            val imageUrl = image?.let {
                if (barcode == null) {
                    createImage(
                        ULong.valueOf(product.id),
                        it,
                        LOCAL_PRODUCT_IMAGES,
                        LOCAL_PRODUCT_IMAGES.PRODUCT_ID,
                        LOCAL_PRODUCT_IMAGES.IMAGE_URL,
                        transaction
                    )
                } else {
                    createImage(
                        product.barcode,
                        it,
                        CROWD_PRODUCT_IMAGES,
                        CROWD_PRODUCT_IMAGES.BARCODE,
                        CROWD_PRODUCT_IMAGES.IMAGE_URL,
                        transaction
                    )
                }
            }

            product.copy(image = "$requestUrl/$imageUrl")
        }
    }

    private fun <T : Record, R> createImage(
        id: R,
        image: String,
        table: TableImpl<T>,
        idColumn: TableField<T, R>,
        imageColumn: TableField<T, String>,
        create: DSLContext
    ): String? {
        return create.insertInto(table, idColumn, imageColumn)
            .values(id, image)
            .returningResult(imageColumn)
            .fetchOne()?.map { it[imageColumn] }
    }

    /**
     * REWRTIE
     */


    // Products

    fun updateProduct(product: Product, create: DSLContext = dslContext): Boolean {
        return create.update(PRODUCTS)
            .set(PRODUCTS.NAME, product.name)
            .where(PRODUCTS.ID.eq(ULong.valueOf(product.id)))
            .execute() == 1
    }

    fun addBarcode(product: Product, create: DSLContext = dslContext): Boolean {
        val res = create.update(PRODUCTS)
            .set(PRODUCTS.BARCODE, product.barcode)
            .where(PRODUCTS.ID.eq(ULong.valueOf(product.id)))
            .execute() == 1

        val localPrices = create.select()
            .from(LOCAL_PRODUCT_PRICES)
            .where(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(product.id)))
            .fetch().map {
                LocalProductPrice(
                    product_id = it[LOCAL_PRODUCT_PRICES.PRODUCT_ID].toLong(),
                    price = it[LOCAL_PRODUCT_PRICES.PRICE],
                    latitude = it[LOCAL_PRODUCT_PRICES.LATITUDE],
                    longitude = it[LOCAL_PRODUCT_PRICES.LONGITUDE]
                )
            }

        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        val itr = localPrices.iterator()
        itr.forEach {
            condition = DSL.noCondition()
            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(product.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(it.latitude + 0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(it.latitude - 0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(it.longitude + 0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(it.longitude - 0.0001))

            val crowd_price = create.select()
                .from(CROWD_PRODUCT_PRICES)
                .where(condition)
                .fetchOne()?.map {
                    it[CROWD_PRODUCT_PRICES.PRICE]
                }

            if (crowd_price == null) {
                create.insertInto(
                    Tables.CROWD_PRODUCT_PRICES,
                    Tables.CROWD_PRODUCT_PRICES.BARCODE,
                    Tables.CROWD_PRODUCT_PRICES.PRICE,
                    Tables.CROWD_PRODUCT_PRICES.LATITUDE,
                    Tables.CROWD_PRODUCT_PRICES.LONGITUDE
                )
                    .values(product.barcode, it.price, it.latitude, it.longitude)
                    .execute() == 1
            }
        }

        return res
    }

    fun getProduct(userId: Long, productId: Long, create: DSLContext = dslContext): Product? {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.ID.eq(ULong.valueOf(productId)))
            .fetchOne()?.map {
                var ratings = mutableListOf<Int>()
                var userRating = -1
                var productRating = -1f

                if (it[PRODUCTS.BARCODE] != null) {
                    ratings = getProductRatings(it[PRODUCTS.BARCODE])
                    userRating = getUserRating(userId, it[PRODUCTS.BARCODE])
                    if (ratings.isNotEmpty())
                        productRating = ratings.sum().toFloat() / ratings.size
                }

                Product(
                    id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    productRating = productRating,
                    userRating = userRating,
                    ratings = ratings,
                    image = ""
                )
            }
    }

    private fun getProduct(userId: Long, barcode: String, create: DSLContext = dslContext): Product? {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.BARCODE.eq(barcode))
            .fetchOne()?.map {
                var ratings = mutableListOf<Int>()
                var userRating = -1
                var productRating = -1f

                if (it[PRODUCTS.BARCODE] != null) {
                    ratings = getProductRatings(it[PRODUCTS.BARCODE])
                    userRating = getUserRating(userId, it[PRODUCTS.BARCODE])
                    if (ratings.isNotEmpty())
                        productRating = ratings.sum().toFloat() / ratings.size
                }

                Product(
                    id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    productRating = productRating,
                    userRating = userRating,
                    ratings = ratings,
                    image = ""
                )
            }
    }

    fun addRating(userId: Long, barcode: String, userRating: Int, create: DSLContext = dslContext): Product {
        val product = getProduct(userId, barcode) ?: throw NotFoundException("Product with specified barcode not found")

        if (product.userRating != -1)
            create.update(RATINGS)
                .set(RATINGS.RATING, userRating)
                .where(RATINGS.USER_ID.eq(ULong.valueOf(userId)))
                .and(RATINGS.BARCODE.eq(barcode))
                .execute()
        else
            create.insertInto(RATINGS, RATINGS.USER_ID, RATINGS.BARCODE, RATINGS.RATING)
                .values(ULong.valueOf(userId), barcode, userRating)
                .execute()

        val ratings = getProductRatings(barcode)
        val productRating = ratings.sum().toFloat() / ratings.size

        return Product(
            id = product.id,
            name = product.name,
            barcode = barcode,
            productRating = productRating,
            userRating = userRating,
            ratings = ratings,
            image = ""
        )
    }

    // Aux
    private fun getUserRating(userId: Long, barcode: String, create: DSLContext = dslContext): Int {
        val userRating = create.select()
            .from(RATINGS)
            .where(RATINGS.USER_ID.eq(ULong.valueOf(userId)))
            .and(RATINGS.BARCODE.eq(barcode))
            .fetchOne()?.map {
                it[RATINGS.RATING]
            }

        return userRating ?: -1
    }

    private fun getProductRatings(barcode: String, create: DSLContext = dslContext): MutableList<Int> {
        return create.select()
            .from(RATINGS)
            .where(RATINGS.BARCODE.eq(barcode))
            .fetch().map {
                it[RATINGS.RATING]
            }
    }

    fun addImage(imageRequest: ImageRequest, create: DSLContext = dslContext): Boolean {
        if (imageRequest.barcode != null) {
            return create.insertInto(
                CROWD_PRODUCT_IMAGES,
                CROWD_PRODUCT_IMAGES.BARCODE, CROWD_PRODUCT_IMAGES.IMAGE_URL
            )
                .values(imageRequest.barcode, imageRequest.image_url)
                .execute() == 1

        } else {
            return create.insertInto(
                LOCAL_PRODUCT_IMAGES,
                LOCAL_PRODUCT_IMAGES.PRODUCT_ID, LOCAL_PRODUCT_IMAGES.IMAGE_URL
            )
                .values(imageRequest.product_id?.let { ULong.valueOf(it) }, imageRequest.image_url)
                .execute() == 1
        }
    }

    fun deleteImage(imageRequest: ImageRequest, create: DSLContext = dslContext): Boolean {
        return if (imageRequest.barcode != null) {
            create.delete(CROWD_PRODUCT_IMAGES)
                .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(imageRequest.barcode))
                .execute() == 1

        } else {
            create.delete(LOCAL_PRODUCT_IMAGES)
                .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(imageRequest.product_id?.let { ULong.valueOf(it) }))
                .execute() == 1
        }
    }

    fun getImageBarcode(barcode: String, create: DSLContext = dslContext): List<String> {
        return create.select()
            .from(CROWD_PRODUCT_IMAGES)
            .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(barcode))
            .fetch().map {
                it[CROWD_PRODUCT_IMAGES.IMAGE_URL]
            }
    }

    fun getImageProductId(productId: Long, create: DSLContext = dslContext): List<String> {
        return create.select()
            .from(LOCAL_PRODUCT_IMAGES)
            .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(productId.let { ULong.valueOf(it) }))
            .fetch().map {
                it[LOCAL_PRODUCT_IMAGES.IMAGE_URL]
            }
    }
}