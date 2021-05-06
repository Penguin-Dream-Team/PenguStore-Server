package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.data.*
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.PantryProducts
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.records.SuggestionsRecord
import store.pengu.server.routes.requests.CreateListRequest
import store.pengu.server.routes.requests.LeaveQueueRequest
import store.pengu.server.routes.requests.PriceRequest
import java.lang.Integer.MAX_VALUE


class ShopDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    companion object {
        fun getShoppingListInformation(it: Record, create: DSLContext): ShoppingList {
            return ShoppingList(
                id = it[SHOPPING_LIST.ID].toLong(),
                name = it[SHOPPING_LIST.NAME],
                code = it[SHOPPING_LIST.CODE],
                latitude = it[SHOPPING_LIST.LATITUDE],
                longitude = it[SHOPPING_LIST.LONGITUDE],
                color = it[SHOPPING_LIST.COLOR],
                shared = create.fetchCount(SHOPPING_LIST_USERS.where(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(it[SHOPPING_LIST.ID]))) > 1
            )
        }
    }

    fun listShops(userId: Long, create: DSLContext = dslContext): List<ShoppingList> {
        return create.select()
            .from(SHOPPING_LIST)
            .join(SHOPPING_LIST_USERS).on(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(SHOPPING_LIST.ID))
            .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .fetch().map {
                getShoppingListInformation(it, create)
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
                SHOPPING_LIST.NAME,
                SHOPPING_LIST.LATITUDE,
                SHOPPING_LIST.LONGITUDE,
                SHOPPING_LIST.COLOR
            )
                .values(listRequest.name, listRequest.latitude, listRequest.longitude, listRequest.color)
                .returningResult(
                    SHOPPING_LIST.ID,
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
                        shared = false
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

    fun getShoppingListByCode(code: String, create: DSLContext = dslContext): ShoppingList {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.CODE.eq(code))
            .fetchOne()?.map {
                getShoppingListInformation(it, create)
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

    fun updateShoppingList(shopping_list: ShoppingList, create: DSLContext = dslContext): Boolean {
        return create.update(SHOPPING_LIST)
            .set(SHOPPING_LIST.NAME, shopping_list.name)
            .set(SHOPPING_LIST.LATITUDE, shopping_list.latitude)
            .set(SHOPPING_LIST.LONGITUDE, shopping_list.longitude)
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(shopping_list.id)))
            .execute() == 1
    }

    fun getShoppingList(id: Long, create: DSLContext = dslContext): ShoppingList? {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                ShoppingList(
                    id = it[SHOPPING_LIST.ID].toLong(),
                    name = it[SHOPPING_LIST.NAME],
                    code = it[SHOPPING_LIST.CODE],
                    latitude = it[SHOPPING_LIST.LATITUDE],
                    longitude = it[SHOPPING_LIST.LONGITUDE],
                    color = it[SHOPPING_LIST.COLOR],
                    shared = create.fetchCount(SHOPPING_LIST_USERS.where(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(it[SHOPPING_LIST.ID]))) > 1
                )
            }
    }

    fun genShoppingList(
        user_id: Long,
        latitude: Double,
        longitude: Double,
        create: DSLContext = dslContext
    ): List<ProductInShoppingList> {
        // TODO Trocar estes valores pa coisas q facam sentido
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(USERS.ID.eq(ULong.valueOf(user_id)))
        condition = condition.and(PANTRY_PRODUCTS.WANT_QTY.ge(PANTRY_PRODUCTS.HAVE_QTY + 1))

        var condition3 = DSL.noCondition() // Alternatively, use trueCondition()
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LATITUDE.le(latitude + 0.0001))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(latitude - 0.0001))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(longitude + 0.0001))
        condition3 = condition3.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(longitude - 0.0001))

        var condition4 = DSL.noCondition() // Alternatively, use trueCondition()
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LATITUDE.le(latitude + 0.0001))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LATITUDE.ge(latitude - 0.0001))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LONGITUDE.le(longitude + 0.0001))
        condition4 = condition4.and(LOCAL_PRODUCT_PRICES.LONGITUDE.ge(longitude - 0.0001))

        val condition2 = condition3.or(condition4)

        condition = condition.and(condition2)

        val shoppingList = create.select()
            .from(USERS)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
            .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .join(PRODUCTS).on(PRODUCTS.ID.eq(PANTRY_PRODUCTS.PRODUCT_ID))
            .leftJoin(CROWD_PRODUCT_PRICES).on(CROWD_PRODUCT_PRICES.BARCODE.eq(PRODUCTS.BARCODE))
            .leftJoin(LOCAL_PRODUCT_PRICES).on(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(condition)
            .groupBy(PRODUCTS.ID)
            .fetch().map {
                ProductInShoppingList(
                    product_id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    amountAvailable = it[PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PANTRY_PRODUCTS.WANT_QTY],
                    price = price(it[PRODUCTS.BARCODE], it[CROWD_PRODUCT_PRICES.PRICE], it[LOCAL_PRODUCT_PRICES.PRICE]),
                    pantries = auxGetPantry(user_id, it[PRODUCTS.ID].toLong())
                )
            }

        return smartSorting(shoppingList)
    }

    fun updateSmartSortingEntries(productId: Long, remainingItems: List<Long>, create: DSLContext = dslContext): Boolean {
        remainingItems.forEach { itemId ->
            if (productId == itemId) return@forEach

            var smartSortingEntry = getSmartSortingEntry(productId, itemId)
            if (smartSortingEntry == null) smartSortingEntry = createSmartSortingEntry(productId, itemId)

            create.update(SMART_SORTING)
                .set(SMART_SORTING.CELL_VAL, smartSortingEntry.cell_val + 1)
                .where(SMART_SORTING.ROW_NUMBER.eq(smartSortingEntry.row_number))
                .and(SMART_SORTING.COL_NUMBER.eq(smartSortingEntry.col_number))
                .execute()
        }

        return true
    }

    private fun auxGetPantry(userId: Long, productId: Long, create: DSLContext = dslContext): List<Pantry> {
        var condition5 = DSL.noCondition() // Alternatively, use trueCondition()
        condition5 = condition5.and(USERS.ID.eq(ULong.valueOf(userId)))
        condition5 = condition5.and(PANTRY_PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(productId)))

        return create.select()
            .from(USERS)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
            .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .where(condition5)
            .fetch().map{
                Pantry(
                    id = it[Pantries.PANTRIES.ID].toLong(),
                    name = it[Pantries.PANTRIES.NAME],
                    code = it[Pantries.PANTRIES.CODE],
                    latitude = it[Pantries.PANTRIES.LATITUDE],
                    longitude = it[Pantries.PANTRIES.LONGITUDE],
                    productCount = create.fetchCount(PantryProducts.PANTRY_PRODUCTS.where(PantryProducts.PANTRY_PRODUCTS.PANTRY_ID.eq(it[Pantries.PANTRIES.ID]))),
                    color = it[Pantries.PANTRIES.COLOR],
                    shared = create.fetchCount(PANTRIES_USERS.where(PANTRIES_USERS.PANTRY_ID.eq(it[Pantries.PANTRIES.ID]))) > 1
                )
            }
    }

    private fun smartSorting(shoppingList: MutableList<ProductInShoppingList>): List<ProductInShoppingList> {
        return shoppingList.sortedWith { a, b ->
            val entry1 = getSmartSortingEntry(a.product_id, b.product_id)
            val entry2 = getSmartSortingEntry(b.product_id, a.product_id)

            val value1 = entry1?.cell_val ?: MAX_VALUE
            val value2 = entry2?.cell_val ?: MAX_VALUE

            println(entry1)
            println(entry2)
            println(value1)
            println(value2)

            value1 - value2
        }.reversed()
    }

    private fun getSmartSortingEntry(productId1: Long, productId2: Long, create: DSLContext = dslContext): MatrixEntry? {
        return create.select()
            .from(SMART_SORTING)
            .where(SMART_SORTING.ROW_NUMBER.eq(ULong.valueOf(productId1)))
            .and(SMART_SORTING.COL_NUMBER.eq(ULong.valueOf(productId2)))
            .fetchOne()?.map() {
                MatrixEntry(
                    row_number = it[SMART_SORTING.ROW_NUMBER],
                    col_number = it[SMART_SORTING.COL_NUMBER],
                    cell_val = it[SMART_SORTING.CELL_VAL]
                )
            }
    }

    private fun createSmartSortingEntry(productId1: Long, productId2: Long, create: DSLContext = dslContext): MatrixEntry {
        create.insertInto(SMART_SORTING, SMART_SORTING.ROW_NUMBER, SMART_SORTING.COL_NUMBER, SMART_SORTING.CELL_VAL)
            .values(ULong.valueOf(productId1), ULong.valueOf(productId2), 0)
            .execute()

        return getSmartSortingEntry(productId1, productId2)!!
    }

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

        val productPairs = getPairs(cart)
        productPairs.forEach { pair ->
            updateSuggestions(pair.first, pair.second)
        }

        return true
    }

    fun getProductSuggestion(productId: Long): Long {
        val rowEntries = getSuggestionEntries(productId, SUGGESTIONS.ROW_NUMBER, SUGGESTIONS.COL_NUMBER)
        val colEntries = getSuggestionEntries(productId, SUGGESTIONS.COL_NUMBER, SUGGESTIONS.ROW_NUMBER)

        val suggestions = rowEntries + colEntries
        val countSuggestions = suggestions.sumBy { it.cell_val }
        val higherSuggestion = suggestions.maxByOrNull { it.cell_val }?.col_number!!.toLong()

        if ((higherSuggestion / countSuggestions) > 0.5) return higherSuggestion
        return -1L
    }

    private fun getPairs(cart: List<Cart>): List<Pair<Long, Long>> {
        val pairs = mutableListOf<Pair<Long, Long>>()
        for (x in (cart.indices - 1)) {
            for (y in (x + 1 until cart.size)) {
                if (cart[x].product_id < cart[y].product_id) pairs.add(Pair(cart[x].product_id, cart[y].product_id))
                else pairs.add(Pair(cart[y].product_id, cart[x].product_id))
            }
        }

        return pairs
    }

    private fun updateSuggestions(productId1: Long, productId2: Long, create: DSLContext = dslContext): Int {
       var suggestionEntry = getSuggestionEntry(productId1, productId2)
        if (suggestionEntry == null) suggestionEntry = createSuggestionEntry(productId1, productId2)

        create.update(SUGGESTIONS)
            .set(SUGGESTIONS.CELL_VAL, suggestionEntry.cell_val + 1)
            .where(SUGGESTIONS.ROW_NUMBER.eq(suggestionEntry.row_number))
            .and(SUGGESTIONS.COL_NUMBER.eq(suggestionEntry.col_number))
            .execute()

        return suggestionEntry.cell_val + 1
    }

    private fun getSuggestionEntries(
        productId: Long,
        tableField1: TableField<SuggestionsRecord, ULong>,
        tableField2: TableField<SuggestionsRecord, ULong>,
        create: DSLContext = dslContext
    ): MutableList<MatrixEntry> {
        return create.select()
            .from(SUGGESTIONS)
            .where(tableField1.eq(ULong.valueOf(productId)))
            .fetch().map() {
                MatrixEntry(
                    row_number = it[tableField1],
                    col_number = it[tableField2],
                    cell_val = it[SUGGESTIONS.CELL_VAL]
                )
            }
    }

    private fun getSuggestionEntry(productId1: Long, productId2: Long, create: DSLContext = dslContext): MatrixEntry? {
        return create.select()
            .from(SUGGESTIONS)
            .where(SUGGESTIONS.ROW_NUMBER.eq(ULong.valueOf(productId1)))
            .and(SUGGESTIONS.COL_NUMBER.eq(ULong.valueOf(productId2)))
            .fetchOne()?.map() {
                MatrixEntry(
                    row_number = it[SUGGESTIONS.ROW_NUMBER],
                    col_number = it[SUGGESTIONS.COL_NUMBER],
                    cell_val = it[SUGGESTIONS.CELL_VAL]
                )
            }
    }

    private fun createSuggestionEntry(productId1: Long, productId2: Long, create: DSLContext = dslContext): MatrixEntry {
        create.insertInto(SUGGESTIONS, SUGGESTIONS.ROW_NUMBER, SUGGESTIONS.COL_NUMBER, SUGGESTIONS.CELL_VAL)
            .values(ULong.valueOf(productId1), ULong.valueOf(productId2), 0)
            .execute()

        return getSuggestionEntry(productId1, productId2)!!
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

    private fun getBeacon(latitude: Double, longitude: Double, create: DSLContext = dslContext): Beacon? {
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