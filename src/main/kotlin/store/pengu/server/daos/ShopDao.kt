package store.pengu.server.daos

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.records.SuggestionsRecord
import store.pengu.server.routes.requests.CreateListRequest
import store.pengu.server.routes.requests.LeaveQueueRequest
import store.pengu.server.routes.requests.PriceRequest

class ShopDao(
    conf: Configuration,
    val productDao: ProductDao
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

        fun getShoppingListInformation(userId: Long, it: Record, create: DSLContext): ShoppingList {
            return ShoppingList(
                id = it[SHOPPING_LIST.ID].toLong(),
                name = it[SHOPPING_LIST.NAME],
                code = it[SHOPPING_LIST.CODE],
                latitude = it[SHOPPING_LIST.LATITUDE],
                longitude = it[SHOPPING_LIST.LONGITUDE],
                color = it[SHOPPING_LIST.COLOR],
                shared = create.fetchCount(SHOPPING_LIST_USERS.where(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(it[SHOPPING_LIST.ID]))) > 1,
                productCount = countShoppingListProducts(
                    userId,
                    it[SHOPPING_LIST.LATITUDE],
                    it[SHOPPING_LIST.LONGITUDE],
                    create
                )
            )
        }

        fun countShoppingListProducts(
            userId: Long,
            latitude: Double,
            longitude: Double,
            create: DSLContext
        ): Int {
            return create.transactionResult { configuration ->
                val transaction = DSL.using(configuration)

                val condition = ListDao.getNearbyCondition(
                    CROWD_PRODUCT_PRICES.LATITUDE,
                    latitude,
                    CROWD_PRODUCT_PRICES.LONGITUDE,
                    longitude
                ).or(
                    ListDao.getNearbyCondition(
                        LOCAL_PRODUCT_PRICES.LATITUDE,
                        latitude,
                        LOCAL_PRODUCT_PRICES.LONGITUDE,
                        longitude
                    )
                )

                transaction.fetchCount(
                    transaction.select()
                        .from(USERS)
                        .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
                        .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
                        .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
                        .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
                        .leftJoin(CROWD_PRODUCT_PRICES).on(CROWD_PRODUCT_PRICES.BARCODE.eq(PRODUCTS.BARCODE))
                        .leftJoin(LOCAL_PRODUCT_PRICES).on(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(PRODUCTS.ID))
                        .where(USERS.ID.eq(ULong.valueOf(userId)))
                        .and(PANTRY_PRODUCTS.WANT_QTY.gt(0))
                        .and(condition)
                        .groupBy(PRODUCTS.ID)
                )
            }
        }

    }

    fun listShops(userId: Long, create: DSLContext = dslContext): List<ShoppingList> {
        return create.select()
            .from(SHOPPING_LIST)
            .join(SHOPPING_LIST_USERS).on(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(SHOPPING_LIST.ID))
            .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .fetch().map {
                getShoppingListInformation(userId, it, create)
            }
    }

    fun createShoppingList(
        listRequest: CreateListRequest,
        userId: Long,
        create: DSLContext = dslContext
    ): ShoppingList {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)
            val shoppingList = transaction.insertInto(
                SHOPPING_LIST,
                SHOPPING_LIST.CODE,
                SHOPPING_LIST.NAME,
                SHOPPING_LIST.LATITUDE,
                SHOPPING_LIST.LONGITUDE,
                SHOPPING_LIST.COLOR
            )
                .values(
                    generateCode(),
                    listRequest.name,
                    listRequest.latitude,
                    listRequest.longitude,
                    listRequest.color
                )
                .returningResult(
                    SHOPPING_LIST.ID,
                    SHOPPING_LIST.CODE,
                    SHOPPING_LIST.NAME,
                    SHOPPING_LIST.LATITUDE,
                    SHOPPING_LIST.LONGITUDE,
                    SHOPPING_LIST.COLOR
                )
                .fetchOne()?.map {
                    ShoppingList(
                        id = it[SHOPPING_LIST.ID].toLong(),
                        name = it[SHOPPING_LIST.NAME],
                        code = it[SHOPPING_LIST.CODE],
                        latitude = it[SHOPPING_LIST.LATITUDE],
                        longitude = it[SHOPPING_LIST.LONGITUDE],
                        color = it[SHOPPING_LIST.COLOR],
                        shared = false,
                        productCount = countShoppingListProducts(
                            userId,
                            it[SHOPPING_LIST.LATITUDE],
                            it[SHOPPING_LIST.LONGITUDE],
                            transaction
                        )
                    )
                } ?: throw InternalServerErrorException("An error occurred")

            try {
                transaction.insertInto(
                    SHOPPING_LIST_USERS,
                    SHOPPING_LIST_USERS.USER_ID,
                    SHOPPING_LIST_USERS.SHOPPING_LIST_ID
                )
                    .values(ULong.valueOf(userId), ULong.valueOf(shoppingList.id))
                    .execute()
            } catch (e: Exception) {
                throw InternalServerErrorException("An error occurred")
            }

            shoppingList
        }
    }

    fun getShoppingListByCode(userId: Long, code: String, create: DSLContext = dslContext): ShoppingList {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.CODE.eq(code))
            .fetchOne()?.map {
                getShoppingListInformation(userId, it, create)
            } ?: throw NotFoundException("Shopping List with specified code not found")
    }

    fun addUserToShoppingList(shoppingListId: Long, userId: Long, create: DSLContext = dslContext) {
        try {
            create.insertInto(SHOPPING_LIST_USERS, SHOPPING_LIST_USERS.USER_ID, SHOPPING_LIST_USERS.SHOPPING_LIST_ID)
                .values(ULong.valueOf(userId), ULong.valueOf(shoppingListId))
                .execute()
        } catch (e: Exception) {
            throw InternalServerErrorException("An error occurred")
        }
    }

    fun userHasShoppingList(shoppingListId: Long, userId: Long, create: DSLContext = dslContext): Boolean {
        return create.fetchExists(
            SHOPPING_LIST_USERS.where(
                SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)).and(
                    SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId))
                )
            )
        )
    }

    fun getShoppingListProducts(
        shopId: Long,
        userId: Long,
        requestUrl: String,
        create: DSLContext = dslContext
    ): List<ProductInShoppingList> {
        return create.transactionResult { configuration ->
            val transaction = DSL.using(configuration)

            val shop = getShoppingList(userId, shopId, transaction)
            val condition = ListDao.getNearbyCondition(
                CROWD_PRODUCT_PRICES.LATITUDE,
                shop.latitude,
                CROWD_PRODUCT_PRICES.LONGITUDE,
                shop.longitude
            ).or(
                ListDao.getNearbyCondition(
                    LOCAL_PRODUCT_PRICES.LATITUDE,
                    shop.latitude,
                    LOCAL_PRODUCT_PRICES.LONGITUDE,
                    shop.longitude
                )
            )

            transaction.select()
                .from(USERS)
                .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
                .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
                .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
                .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
                .leftJoin(CROWD_PRODUCT_PRICES).on(CROWD_PRODUCT_PRICES.BARCODE.eq(PRODUCTS.BARCODE))
                .leftJoin(LOCAL_PRODUCT_PRICES).on(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(PRODUCTS.ID))
                .where(USERS.ID.eq(ULong.valueOf(userId)))
                .and(PANTRY_PRODUCTS.WANT_QTY.gt(0))
                .and(condition)
                .groupBy(PRODUCTS.ID)
                .fetch().map {
                    val pantries = productDao.getProductPantryLists(userId, it[PRODUCTS.ID].toLong(), create)
                            .filter { p -> p.amountNeeded > 0 }
                    ProductInShoppingList(
                        id = it[PRODUCTS.ID].toLong(),
                        listId = shopId,
                        name = it[PRODUCTS.NAME],
                        barcode = it[PRODUCTS.BARCODE],
                        amountAvailable = pantries.sumOf { productPantryListEntry -> productPantryListEntry.amountAvailable },
                        amountNeeded = pantries.sumOf { productPantryListEntry -> productPantryListEntry.amountNeeded },
                        price = ProductDao.price(
                            it[PRODUCTS.BARCODE],
                            it[CROWD_PRODUCT_PRICES.PRICE],
                            it[LOCAL_PRODUCT_PRICES.PRICE]
                        ),
                        image = ProductDao.image(
                            barcode = it[PRODUCTS.BARCODE],
                            id = it[PRODUCTS.ID].toLong(),
                            requestUrl,
                            transaction
                        ),
                        pantries = pantries
                    )
                }.run { smartSorting(shopId, this) }
        }
    }

    fun updateShoppingList(shopping_list: ShoppingList, create: DSLContext = dslContext): Boolean {
        return create.update(SHOPPING_LIST)
            .set(SHOPPING_LIST.NAME, shopping_list.name)
            .set(SHOPPING_LIST.LATITUDE, shopping_list.latitude)
            .set(SHOPPING_LIST.LONGITUDE, shopping_list.longitude)
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(shopping_list.id)))
            .execute() == 1
    }

    private fun getShoppingList(userId: Long, id: Long, create: DSLContext = dslContext): ShoppingList {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                getShoppingListInformation(userId, it, create)
            } ?: throw NotFoundException("Shopping List with specified code not found")
    }

    fun updateSmartSortingEntries(
        shoppingListId: Long,
        barcode: String,
        remainingItems: List<String>,
        create: DSLContext = dslContext
    ): Boolean {
        remainingItems.forEach { item ->
            if (barcode == item) return@forEach

            var smartSortingEntry = getSmartSortingEntry(shoppingListId, barcode, item)
            if (smartSortingEntry == null) smartSortingEntry = createSmartSortingEntry(shoppingListId, barcode, item)

            create.update(SMART_SORTING)
                .set(SMART_SORTING.CELL_VAL, smartSortingEntry.cell_val + 1)
                .where(SMART_SORTING.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)))
                .and(SMART_SORTING.ROW_NUMBER.eq(smartSortingEntry.row_number))
                .and(SMART_SORTING.COL_NUMBER.eq(smartSortingEntry.col_number))
                .execute()
        }

        return true
    }

    private fun smartSorting(
        shoppingListId: Long,
        shoppingList: MutableList<ProductInShoppingList>
    ): List<ProductInShoppingList> {
        return shoppingList.sortedWith { a, b ->
            var entry1 = MatrixEntry(ULong.valueOf(-1), "", "", 0)
            var entry2 = MatrixEntry(ULong.valueOf(-1), "", "", 0)

            if (a.barcode != null && b.barcode != null) {
                entry1 = getSmartSortingEntry(shoppingListId, a.barcode, b.barcode) ?: MatrixEntry(
                    ULong.valueOf(-1),
                    "",
                    "",
                    0
                )
                entry2 = getSmartSortingEntry(shoppingListId, b.barcode, a.barcode) ?: MatrixEntry(
                    ULong.valueOf(-1),
                    "",
                    "",
                    0
                )
            }

            return@sortedWith if (a.barcode != null && b.barcode != null) entry2.cell_val - entry1.cell_val
            else if (a.barcode != null && b.barcode == null) -1
            else if (b.barcode != null && a.barcode == null) 1
            else 0
        }
    }

    private fun getSmartSortingEntry(
        shoppingListId: Long,
        productBarcode1: String,
        productBarcode2: String,
        create: DSLContext = dslContext
    ): MatrixEntry? {
        return create.select()
            .from(SMART_SORTING)
            .where(SMART_SORTING.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)))
            .and(SMART_SORTING.ROW_NUMBER.eq(productBarcode1))
            .and(SMART_SORTING.COL_NUMBER.eq(productBarcode2))
            .fetchOne()?.map {
                MatrixEntry(
                    shopping_list_id = it[SMART_SORTING.SHOPPING_LIST_ID],
                    row_number = it[SMART_SORTING.ROW_NUMBER],
                    col_number = it[SMART_SORTING.COL_NUMBER],
                    cell_val = it[SMART_SORTING.CELL_VAL]
                )
            }
    }

    private fun createSmartSortingEntry(
        shoppingListId: Long,
        productBarcode1: String,
        productBarcode2: String,
        create: DSLContext = dslContext
    ): MatrixEntry {
        create.insertInto(
            SMART_SORTING,
            SMART_SORTING.SHOPPING_LIST_ID,
            SMART_SORTING.ROW_NUMBER,
            SMART_SORTING.COL_NUMBER,
            SMART_SORTING.CELL_VAL
        )
            .values(ULong.valueOf(shoppingListId), productBarcode1, productBarcode2, 0)
            .execute()

        return getSmartSortingEntry(shoppingListId, productBarcode1, productBarcode2)!!
    }

    /**
     *
     */

    // Prices
    fun addPrice(price_request: PriceRequest, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        if (price_request.barcode != null) {
            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(price_request.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(price_request.latitude + 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude - 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude + 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude - 0.0002))

            val success = create.update(CROWD_PRODUCT_PRICES)
                .set(CROWD_PRODUCT_PRICES.PRICE, price_request.price)
                .where(condition)
                .execute() == 1

            return if (success)
                success
            else
                create.insertInto(
                    CROWD_PRODUCT_PRICES,
                    CROWD_PRODUCT_PRICES.BARCODE,
                    CROWD_PRODUCT_PRICES.PRICE,
                    CROWD_PRODUCT_PRICES.LATITUDE,
                    CROWD_PRODUCT_PRICES.LONGITUDE
                )
                    .values(
                        price_request.barcode,
                        price_request.price,
                        price_request.latitude,
                        price_request.longitude
                    )
                    .execute() == 1
        } else {
            val productId = price_request.product_id ?: throw NotFoundException("Product Id not provided")
            condition = condition.and(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(price_request.latitude + 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude - 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude + 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude - 0.0002))

            val success = create.update(LOCAL_PRODUCT_PRICES)
                .set(LOCAL_PRODUCT_PRICES.PRICE, price_request.price)
                .where(condition)
                .execute() == 1

            return if (success)
                success
            else
                create.insertInto(
                    LOCAL_PRODUCT_PRICES,
                    LOCAL_PRODUCT_PRICES.PRODUCT_ID,
                    LOCAL_PRODUCT_PRICES.PRICE,
                    LOCAL_PRODUCT_PRICES.LATITUDE,
                    LOCAL_PRODUCT_PRICES.LONGITUDE
                )
                    .values(
                        ULong.valueOf(productId),
                        price_request.price,
                        price_request.latitude,
                        price_request.longitude
                    )
                    .execute() == 1
        }

    }

    fun deletePrice(price_request: PriceRequest, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        if (price_request.barcode != null) {
            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(price_request.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(price_request.latitude + 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude - 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude + 0.0002))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude - 0.0002))

            return create.delete(CROWD_PRODUCT_PRICES)
                .where(condition)
                .execute() == 1
        } else {
            val productId = price_request.product_id ?: throw NotFoundException("Product Id not provided")
            condition = condition.and(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(productId)))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(price_request.latitude + 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(price_request.latitude - 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(price_request.longitude + 0.0002))
            condition = condition.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(price_request.longitude - 0.0002))

            return create.delete(LOCAL_PRODUCT_PRICES)
                .where(condition)
                .execute() == 1
        }
    }

    fun getShopPrices(latitude: Double, longitude: Double, create: DSLContext = dslContext): List<ProductInShop> {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(latitude + 0.0001))
        condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(latitude - 0.0001))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(longitude + 0.0001))
        condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(longitude - 0.0001))

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
    fun buyCart(shoppingListId: Long, cart: List<Cart>, create: DSLContext = dslContext): Boolean {
        val itr = cart.iterator()
        val cartProductsBarcode = mutableListOf<String>()
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        itr.forEach {
            condition = DSL.noCondition()
            condition = condition.and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(it.product_id)))
            condition = condition.and(PANTRY_PRODUCTS.PANTRY_ID.eq(ULong.valueOf(it.pantry_id)))

            cartProductsBarcode.add(
                create.select()
                    .from(PRODUCTS)
                    .where(PRODUCTS.ID.eq(ULong.valueOf(it.product_id)))
                    .fetchOne()?.map() {
                        it[PRODUCTS.BARCODE]
                    }!!
            )

            create.update(PANTRY_PRODUCTS)
                .set(PANTRY_PRODUCTS.HAVE_QTY, PANTRY_PRODUCTS.HAVE_QTY + it.amount)
                .where(condition)
                .execute()
        }

        val productPairs = getPairs(cartProductsBarcode)
        productPairs.forEach { pair ->
            updateSuggestions(shoppingListId, pair.first, pair.second)
        }

        return true
    }

    fun getProductSuggestion(shoppingListId: Long, barcode: String): String {
        val rowEntries = getSuggestionEntries(shoppingListId, barcode, SUGGESTIONS.ROW_NUMBER, SUGGESTIONS.COL_NUMBER)
        val colEntries = getSuggestionEntries(shoppingListId, barcode, SUGGESTIONS.COL_NUMBER, SUGGESTIONS.ROW_NUMBER)

        val suggestions = rowEntries + colEntries
        if (suggestions.isEmpty()) throw NotFoundException("No suggestion found")

        val countSuggestions = suggestions.sumBy { it.cell_val }
        val higherSuggestion = suggestions.maxByOrNull { it.cell_val }

        if ((higherSuggestion!!.cell_val / countSuggestions) > 0.5) return higherSuggestion.col_number
        throw NotFoundException("No suggestion found")
    }

    private fun getPairs(cart: List<String>): List<Pair<String, String>> {
        val pairs = mutableListOf<Pair<String, String>>()
        for (x in (cart.indices - 1)) {
            for (y in (x + 1 until cart.size)) {
                if (cart[x] < cart[y]) pairs.add(Pair(cart[x], cart[y]))
                else pairs.add(Pair(cart[y], cart[x]))
            }
        }

        return pairs
    }

    private fun updateSuggestions(
        shoppingListId: Long,
        productBarcode1: String,
        productBarcode2: String,
        create: DSLContext = dslContext
    ): Int {
        var suggestionEntry = getSuggestionEntry(shoppingListId, productBarcode1, productBarcode2)
        if (suggestionEntry == null) suggestionEntry =
            createSuggestionEntry(shoppingListId, productBarcode1, productBarcode2)

        create.update(SUGGESTIONS)
            .set(SUGGESTIONS.CELL_VAL, suggestionEntry.cell_val + 1)
            .where(SUGGESTIONS.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)))
            .and(SUGGESTIONS.ROW_NUMBER.eq(suggestionEntry.row_number))
            .and(SUGGESTIONS.COL_NUMBER.eq(suggestionEntry.col_number))
            .execute()

        return suggestionEntry.cell_val + 1
    }

    private fun getSuggestionEntries(
        shoppingListId: Long,
        barcode: String,
        tableField1: TableField<SuggestionsRecord, String>,
        tableField2: TableField<SuggestionsRecord, String>,
        create: DSLContext = dslContext
    ): MutableList<MatrixEntry> {
        return create.select()
            .from(SUGGESTIONS)
            .where(SUGGESTIONS.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)))
            .and(tableField1.eq(barcode))
            .fetch().map() {
                MatrixEntry(
                    shopping_list_id = it[SUGGESTIONS.SHOPPING_LIST_ID],
                    row_number = it[tableField1],
                    col_number = it[tableField2],
                    cell_val = it[SUGGESTIONS.CELL_VAL]
                )
            }
    }

    private fun getSuggestionEntry(
        shoppingListId: Long,
        productBarcode1: String,
        productBarcode2: String,
        create: DSLContext = dslContext
    ): MatrixEntry? {
        return create.select()
            .from(SUGGESTIONS)
            .where(SUGGESTIONS.SHOPPING_LIST_ID.eq(ULong.valueOf(shoppingListId)))
            .and(SUGGESTIONS.ROW_NUMBER.eq(productBarcode1))
            .and(SUGGESTIONS.COL_NUMBER.eq(productBarcode2))
            .fetchOne()?.map() {
                MatrixEntry(
                    shopping_list_id = it[SUGGESTIONS.SHOPPING_LIST_ID],
                    row_number = it[SUGGESTIONS.ROW_NUMBER],
                    col_number = it[SUGGESTIONS.COL_NUMBER],
                    cell_val = it[SUGGESTIONS.CELL_VAL]
                )
            }
    }

    private fun createSuggestionEntry(
        shoppingListId: Long,
        productBarcode1: String,
        productBarcode2: String,
        create: DSLContext = dslContext
    ): MatrixEntry {
        create.insertInto(
            SUGGESTIONS,
            SUGGESTIONS.SHOPPING_LIST_ID,
            SUGGESTIONS.ROW_NUMBER,
            SUGGESTIONS.COL_NUMBER,
            SUGGESTIONS.CELL_VAL
        )
            .values(ULong.valueOf(shoppingListId), productBarcode1, productBarcode2, 0)
            .execute()

        return getSuggestionEntry(shoppingListId, productBarcode1, productBarcode2)!!
    }

    // Queue
    fun joinQueue(latitude: Double, longitude: Double, num_items: Int, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(latitude + 0.0001))
        condition = condition.and(BEACONS.LATITUDE.ge(latitude - 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.le(longitude + 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.ge(longitude - 0.0001))
        val beacon = getBeacon(latitude, longitude)

        var res = false

        res = if (beacon != null) {
            create.update(BEACONS)
                .set(BEACONS.NUM_ITEMS, BEACONS.NUM_ITEMS + num_items)
                .where(condition)
                .execute() == 1
        } else {
            create.insertInto(
                BEACONS,
                BEACONS.NUM_ITEMS, BEACONS.LATITUDE, BEACONS.LONGITUDE
            )
                .values(num_items, latitude, longitude)
                .execute() == 1

        }
        return res
    }

    fun leaveQueue(leaveQueueRequest: LeaveQueueRequest, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(leaveQueueRequest.latitude + 0.0001))
        condition = condition.and(BEACONS.LATITUDE.ge(leaveQueueRequest.latitude - 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.le(leaveQueueRequest.longitude + 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.ge(leaveQueueRequest.longitude - 0.0001))

        create.insertInto(
            STATS,
            STATS.NUM_ITEMS, STATS.TIME, STATS.LATITUDE, STATS.LONGITUDE
        )
            .values(
                leaveQueueRequest.num_items,
                leaveQueueRequest.time,
                leaveQueueRequest.latitude,
                leaveQueueRequest.longitude
            )
            .execute()

        return create.update(BEACONS)
            .set(BEACONS.NUM_ITEMS, BEACONS.NUM_ITEMS - leaveQueueRequest.num_items)
            .where(condition)
            .execute() == 1
    }

    fun timeQueue(latitude: Double, longitude: Double, create: DSLContext = dslContext): Int {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(STATS.LATITUDE.le(latitude + 0.0001))
        condition = condition.and(STATS.LATITUDE.ge(latitude - 0.0001))
        condition = condition.and(STATS.LONGITUDE.le(longitude + 0.0001))
        condition = condition.and(STATS.LONGITUDE.ge(longitude - 0.0001))

        val points = create.select()
            .from(STATS)
            .where(condition)
            .fetch().map {
                Point(
                    x = it[STATS.NUM_ITEMS],
                    y = it[STATS.TIME]
                )
            }

        condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(latitude + 0.0001))
        condition = condition.and(BEACONS.LATITUDE.ge(latitude - 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.le(longitude + 0.0001))
        condition = condition.and(BEACONS.LONGITUDE.ge(longitude - 0.0001))

        val num_items = create.select()
            .from(BEACONS)
            .where(condition)
            .fetchOne()?.map {
                it[BEACONS.NUM_ITEMS]
            } ?: return 0

        val n = points.size

        // first pass
        var sumx = 0.0
        var sumy = 0.0
        var sumx2 = 0.0
        for (i in 0 until n) {
            sumx += points[i].x
            sumx2 += points[i].x * points[i].x
            sumy += points[i].y
        }
        val xbar: Double = sumx / n
        val ybar: Double = sumy / n

        // second pass: compute summary statistics
        var xxbar = 0.0
        var yybar = 0.0
        var xybar = 0.0
        for (i in 0 until n) {
            xxbar += (points[i].x - xbar) * (points[i].x - xbar)
            yybar += (points[i].y - ybar) * (points[i].y - ybar)
            xybar += (points[i].x - xbar) * (points[i].y - ybar)
        }
        val slope = xybar / xxbar
        val intercept = ybar - slope * xbar

        return (intercept + slope * num_items).toInt()

    }


    // Aux
    fun price(barcode: String?, crowd_price: Double?, local_price: Double?): Double {
        return if (barcode != null)
            crowd_price ?: throw NotFoundException("Crowd Price not found")
        else
            local_price ?: throw NotFoundException("Local Price not found")
    }

    fun getBeacon(latitude: Double, longitude: Double, create: DSLContext = dslContext): Beacon? {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(BEACONS.LATITUDE.le(latitude + 0.5))
        condition = condition.and(BEACONS.LATITUDE.ge(latitude - 0.5))
        condition = condition.and(BEACONS.LONGITUDE.le(longitude + 0.5))
        condition = condition.and(BEACONS.LONGITUDE.ge(longitude - 0.5))

        return create.select()
            .from(BEACONS)
            .where(condition)
            .fetchOne()?.map {
                Beacon(
                    num_items = it[BEACONS.NUM_ITEMS],
                    latitude = it[BEACONS.LATITUDE],
                    longitude = it[BEACONS.LONGITUDE]
                )
            }
    }
}