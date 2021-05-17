package store.pengu.server.daos

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import org.jooq.types.ULong
import store.pengu.server.BadRequestException
import store.pengu.server.ConflictException
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.data.LocalProductPrice
import store.pengu.server.data.MatrixEntry
import store.pengu.server.data.Product
import store.pengu.server.data.ShoppingList
import store.pengu.server.data.productlists.ProductPantryListEntry
import store.pengu.server.data.productlists.ProductShoppingListEntry
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.CrowdProductPrices.CROWD_PRODUCT_PRICES
import store.pengu.server.db.pengustore.tables.LocalProductPrices.LOCAL_PRODUCT_PRICES
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.records.SuggestionsRecord
import store.pengu.server.routes.requests.ImageRequest
import java.net.URLEncoder
import java.nio.charset.Charset

class ProductDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    companion object {
        fun convertImageUrl(url: String, requestUrl: String): String {
            return if (url.startsWith("http")) {
                url
            } else {
                "$requestUrl/uploads/${URLEncoder.encode(url.split("/").last(), Charset.forName("utf-8"))}"
            }
        }

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
                convertImageUrl(this, requestUrl)
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

    fun addProductToPantryList(
        userId: Long,
        productId: Long,
        pantryId: Long,
        haveQty: Int,
        needQty: Int,
        create: DSLContext = dslContext
    ): ProductPantryListEntry {
        val product = getProduct(userId, productId, "", create) ?: throw NotFoundException("Product not found")

        val pantry = create.select()
            .from(PANTRIES)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.PANTRY_ID.eq(PANTRIES.ID))
            .where(PANTRIES_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .and(PANTRIES.ID.eq(ULong.valueOf(pantryId)))
            .fetchOne()?.map {
                PantryDao.getPantryInformation(it, create)
            } ?: throw NotFoundException("Pantry not found for this user")
        return try {
            create.insertInto(
                PANTRY_PRODUCTS,
                PANTRY_PRODUCTS.PANTRY_ID,
                PANTRY_PRODUCTS.PRODUCT_ID,
                PANTRY_PRODUCTS.HAVE_QTY,
                PANTRY_PRODUCTS.WANT_QTY
            )
                .values(ULong.valueOf(pantryId), ULong.valueOf(productId), haveQty, needQty)
                .returningResult()
                .fetchOne()?.map {
                    ProductPantryListEntry(
                        listId = pantry.id,
                        listName = pantry.name,
                        color = pantry.color,
                        amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                        amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY],
                        isShared = pantry.shared,
                        latitude = pantry.latitude,
                        longitude = pantry.longitude,
                    )
                } ?: throw ConflictException("Product already in pantry")
        } catch (e: Exception) {
            create.update(PANTRY_PRODUCTS)
                .set(PANTRY_PRODUCTS.HAVE_QTY, haveQty)
                .set(PANTRY_PRODUCTS.WANT_QTY, needQty)
                .where(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(pantryId)))
                .and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(productId)))
                .execute()
            return ProductPantryListEntry(
                listId = pantry.id,
                listName = pantry.name,
                color = pantry.color,
                amountAvailable = haveQty,
                amountNeeded = needQty,
                isShared = pantry.shared,
                latitude = pantry.latitude,
                longitude = pantry.longitude,
            )
        }
    }

    fun getMissingProductPantryList(
        userId: Long,
        pantryId: Long,
        requestUrl: String,
        create: DSLContext = dslContext
    ): List<Product> {
        return create.selectDistinct(PRODUCTS.asterisk())
            .from(PRODUCTS)
            .join(PRODUCTS_USERS).on(PRODUCTS_USERS.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(PRODUCTS_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .and(
                PRODUCTS.ID.notIn(
                    create.select(PANTRY_PRODUCTS.PRODUCT_ID)
                        .from(PANTRY_PRODUCTS)
                        .join(PANTRIES_USERS).on(PANTRIES_USERS.PANTRY_ID.eq(PANTRY_PRODUCTS.PANTRY_ID))
                        .join(PANTRIES).on(PANTRIES.ID.eq(PANTRY_PRODUCTS.PANTRY_ID))
                        .where(PANTRIES_USERS.USER_ID.eq(ULong.valueOf(userId)))
                        .and(PANTRIES.ID.eq(ULong.valueOf(pantryId)))
                )
            )
            .fetch().map {
                getProductInformation(userId, it, requestUrl, create)
            }
    }

    fun getProductPantryLists(
        userId: Long,
        productId: Long,
        create: DSLContext = dslContext
    ): List<ProductPantryListEntry> {
        return create.select()
            .from(PANTRIES)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.PANTRY_ID.eq(PANTRIES.ID))
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .where(PANTRIES_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .and(PANTRY_PRODUCTS.PRODUCT_ID.eq((ULong.valueOf(productId))))
            .fetch().map {
                ProductPantryListEntry(
                    listId = it[PANTRIES.ID].toLong(),
                    listName = it[PANTRIES.NAME],
                    color = it[PANTRIES.COLOR],
                    amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY],
                    isShared = create.fetchCount(PANTRIES_USERS.where(PANTRIES_USERS.PANTRY_ID.eq(it[PANTRIES.ID]))) > 1,
                    latitude = it[PANTRIES.LATITUDE],
                    longitude = it[PANTRIES.LONGITUDE],
                )
            }
    }

    fun addProductToShoppingList(
        userId: Long,
        productId: Long,
        shoppingListId: Long,
        price: Double,
        create: DSLContext = dslContext
    ): ProductShoppingListEntry {
        val product = getProduct(userId, productId, "", create) ?: throw NotFoundException("Product not found")
        val shoppingList = create.select()
            .from(SHOPPING_LIST)
            .join(SHOPPING_LIST_USERS).on(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(SHOPPING_LIST.ID))
            .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .and(SHOPPING_LIST.ID.eq(ULong.valueOf(shoppingListId)))
            .fetchOne()?.map {
                ShopDao.getShoppingListInformation(userId, it, create)
            } ?: throw NotFoundException("Shopping list not found for this user")

        return product.barcode?.run {
            addProductCrowdPrice(this, price, shoppingList, create)
        } ?: addProductLocalPrice(productId, price, shoppingList, create)
    }

    fun getProductShoppingLists(
        userId: Long,
        productId: Long,
        create: DSLContext = dslContext
    ): List<ProductShoppingListEntry> {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            val barcode =
                transaction.select(PRODUCTS.BARCODE).from(PRODUCTS).where(PRODUCTS.ID.eq(ULong.valueOf(productId)))
                    .fetchOne()?.map { it[PRODUCTS.BARCODE] }
            barcode?.let {
                transaction.select()
                    .from(SHOPPING_LIST)
                    .join(SHOPPING_LIST_USERS).on(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(SHOPPING_LIST.ID))
                    .join(CROWD_PRODUCT_PRICES).on(
                        CROWD_PRODUCT_PRICES.LATITUDE.eq(SHOPPING_LIST.LATITUDE).and(
                            CROWD_PRODUCT_PRICES.LONGITUDE.eq(SHOPPING_LIST.LONGITUDE)
                        )
                    )
                    .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
                    .and(CROWD_PRODUCT_PRICES.BARCODE.eq(it))
                    .fetch().map {
                        ProductShoppingListEntry(
                            listId = it[SHOPPING_LIST.ID].toLong(),
                            listName = it[SHOPPING_LIST.NAME],
                            color = it[SHOPPING_LIST.COLOR],
                            price = it[CROWD_PRODUCT_PRICES.PRICE],
                            latitude = it[SHOPPING_LIST.LATITUDE],
                            longitude = it[SHOPPING_LIST.LONGITUDE],
                        )
                    }
            } ?: transaction.select()
                .from(SHOPPING_LIST)
                .join(SHOPPING_LIST_USERS).on(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(SHOPPING_LIST.ID))
                .join(LOCAL_PRODUCT_PRICES).on(
                    LOCAL_PRODUCT_PRICES.LATITUDE.eq(SHOPPING_LIST.LATITUDE).and(
                        LOCAL_PRODUCT_PRICES.LONGITUDE.eq(SHOPPING_LIST.LONGITUDE)
                    )
                )
                .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
                .and(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
                .fetch().map {
                    ProductShoppingListEntry(
                        listId = it[SHOPPING_LIST.ID].toLong(),
                        listName = it[SHOPPING_LIST.NAME],
                        color = it[SHOPPING_LIST.COLOR],
                        price = it[LOCAL_PRODUCT_PRICES.PRICE],
                        latitude = it[SHOPPING_LIST.LATITUDE],
                        longitude = it[SHOPPING_LIST.LONGITUDE],
                    )
                }
        }
    }

    fun getProduct(userId: Long, productId: Long, requestUrl: String, create: DSLContext = dslContext): Product {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.ID.eq(ULong.valueOf(productId)))
            .fetchOne()?.map {
                getProductInformation(userId, it, requestUrl, create)
            } ?: throw NotFoundException("Product not found")
    }

    fun getProduct(userId: Long, barcode: String, requestUrl: String, create: DSLContext = dslContext): Product {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.BARCODE.eq(barcode))
            .fetchOne()?.map {
                getProductInformation(userId, it, requestUrl, create)
            } ?: throw NotFoundException("Product with barcode not found")
    }

    private fun getSuggestionEntries(
        userId: Long,
        barcode: String,
        tableField1: TableField<SuggestionsRecord, String>,
        tableField2: TableField<SuggestionsRecord, String>,
        create: DSLContext = dslContext
    ): List<MatrixEntry> {

        return create.select()
            .from(SUGGESTIONS)
            .where(SUGGESTIONS.USER_ID.eq(ULong.valueOf(userId)))
            .and(tableField1.eq(barcode))
            .fetch().map() {
                MatrixEntry(
                    id = it[SUGGESTIONS.USER_ID],
                    row_number = it[tableField1],
                    col_number = it[tableField2],
                    cell_val = it[SUGGESTIONS.CELL_VAL]
                )
            }
    }

    fun getProductSuggestion(userId: Long, barcode: String, requestUrl: String): Product {
        val rowEntries = getSuggestionEntries(userId, barcode, SUGGESTIONS.ROW_NUMBER, SUGGESTIONS.COL_NUMBER)
        val colEntries = getSuggestionEntries(userId, barcode, SUGGESTIONS.COL_NUMBER, SUGGESTIONS.ROW_NUMBER)

        val suggestions = rowEntries + colEntries
        if (suggestions.isEmpty()) throw NotFoundException("No suggestion found")

        val countSuggestions = suggestions.sumBy { it.cell_val }
        val higherSuggestion = suggestions.maxByOrNull { it.cell_val }

        if ((higherSuggestion!!.cell_val / countSuggestions) > 0.5) {
            return getProduct(userId, higherSuggestion.col_number, requestUrl)
        }
        throw NotFoundException("No suggestion found")
    }

    private fun addProductLocalPrice(
        productId: Long,
        price: Double,
        shoppingList: ShoppingList,
        create: DSLContext
    ): ProductShoppingListEntry {
        return try {
            create.insertInto(
                LOCAL_PRODUCT_PRICES,
                LOCAL_PRODUCT_PRICES.PRODUCT_ID,
                LOCAL_PRODUCT_PRICES.PRICE,
                LOCAL_PRODUCT_PRICES.LATITUDE,
                LOCAL_PRODUCT_PRICES.LONGITUDE
            )
                .values(ULong.valueOf(productId), price, shoppingList.latitude, shoppingList.longitude)
                .returningResult()
                .fetchOne()?.map {
                    ProductShoppingListEntry(
                        listId = shoppingList.id,
                        listName = shoppingList.name,
                        color = shoppingList.color,
                        price = price,
                        latitude = shoppingList.latitude,
                        longitude = shoppingList.longitude,
                    )
                } ?: throw ConflictException("Product already in shop")
        } catch (e: Exception) {
            create.update(LOCAL_PRODUCT_PRICES)
                .set(LOCAL_PRODUCT_PRICES.PRICE, price)
                .where(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
                .and(LOCAL_PRODUCT_PRICES.LATITUDE.eq(shoppingList.latitude))
                .and(LOCAL_PRODUCT_PRICES.LONGITUDE.eq(shoppingList.longitude))
                .execute()
            return ProductShoppingListEntry(
                listId = shoppingList.id,
                listName = shoppingList.name,
                color = shoppingList.color,
                price = price,
                latitude = shoppingList.latitude,
                longitude = shoppingList.longitude,
            )
        }
    }

    private fun addProductCrowdPrice(
        barcode: String,
        price: Double,
        shoppingList: ShoppingList,
        create: DSLContext
    ): ProductShoppingListEntry {
        return try {
            create.insertInto(
                CROWD_PRODUCT_PRICES,
                CROWD_PRODUCT_PRICES.BARCODE,
                CROWD_PRODUCT_PRICES.PRICE,
                CROWD_PRODUCT_PRICES.LATITUDE,
                CROWD_PRODUCT_PRICES.LONGITUDE
            )
                .values(barcode, price, shoppingList.latitude, shoppingList.longitude)
                .returningResult()
                .fetchOne()?.map {
                    ProductShoppingListEntry(
                        listId = shoppingList.id,
                        listName = shoppingList.name,
                        color = shoppingList.color,
                        price = price,
                        latitude = shoppingList.latitude,
                        longitude = shoppingList.longitude,
                    )
                } ?: throw ConflictException("Product already in shop")
        } catch (e: Exception) {
            create.update(CROWD_PRODUCT_PRICES)
                .set(CROWD_PRODUCT_PRICES.PRICE, price)
                .where(CROWD_PRODUCT_PRICES.BARCODE.eq(barcode))
                .and(CROWD_PRODUCT_PRICES.LATITUDE.eq(shoppingList.latitude))
                .and(CROWD_PRODUCT_PRICES.LONGITUDE.eq(shoppingList.longitude))
                .execute()
            return ProductShoppingListEntry(
                listId = shoppingList.id,
                listName = shoppingList.name,
                color = shoppingList.color,
                price = price,
                latitude = shoppingList.latitude,
                longitude = shoppingList.longitude,
            )
        }
    }

    fun getProductImages(
        userId: Long,
        productId: Long,
        requestUrl: String,
        create: DSLContext = dslContext
    ): List<String> {
        val product = getProduct(userId, productId, requestUrl, create)
        return product.barcode?.let { barcode ->
            create.select()
                .from(CROWD_PRODUCT_IMAGES)
                .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(barcode))
                .fetch().map {
                    val url = it[CROWD_PRODUCT_IMAGES.IMAGE_URL]
                    convertImageUrl(url, requestUrl)
                }
        } ?: create.select()
            .from(LOCAL_PRODUCT_IMAGES)
            .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(productId.let { ULong.valueOf(it) }))
            .fetch().map {
                val url = it[LOCAL_PRODUCT_IMAGES.IMAGE_URL]
                convertImageUrl(url, requestUrl)
            }
    }

    fun rateProduct(userId: Long, productId: Long, rating: Int, create: DSLContext = dslContext): List<Int> {
        val product = getProduct(userId, productId, "", create)
        if (product.barcode == null) {
            throw BadRequestException("Product without barcode cannot be rated")
        }

        try {
            create.insertInto(RATINGS, RATINGS.USER_ID, RATINGS.BARCODE, RATINGS.RATING)
                .values(ULong.valueOf(userId), product.barcode, rating)
                .execute()
        } catch (e: Exception) {
            if (rating == 0) {
                create.delete(RATINGS)
                    .where(RATINGS.USER_ID.eq(ULong.valueOf(userId)))
                    .and(RATINGS.BARCODE.eq(product.barcode))
                    .execute()
            } else {
                create.update(RATINGS)
                    .set(RATINGS.RATING, rating)
                    .where(RATINGS.USER_ID.eq(ULong.valueOf(userId)))
                    .and(RATINGS.BARCODE.eq(product.barcode))
                    .execute()
            }
        }

        return create.select()
            .from(RATINGS)
            .where(RATINGS.BARCODE.eq(product.barcode))
            .fetch().map {
                it[RATINGS.RATING]
            }
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
}