package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.*
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantryXUser.PANTRY_X_USER
import store.pengu.server.db.pengustore.tables.ProductXPantry.PRODUCT_X_PANTRY
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.ShopXProduct.SHOP_X_PRODUCT
import store.pengu.server.db.pengustore.tables.ShoppingList.SHOPPING_LIST
import store.pengu.server.db.pengustore.tables.Shops.SHOPS
import store.pengu.server.db.pengustore.tables.Users.USERS

class UserDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun getUsers(create: DSLContext = dslContext): List<User> {
        return create.select()
            .from(USERS)
            .fetch().map {
                User(
                    id = it[USERS.USER_ID].toLong(),
                    username = it[USERS.USERNAME],
                    email = it[USERS.EMAIL],
                    password = it[USERS.PASSWORD]
                )
            }

    }

    fun getUser(id: Long, create: DSLContext = dslContext): User? {
        return create.select()
            .from(USERS)
            .where(USERS.USER_ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                User(
                    id = it[USERS.USER_ID].toLong(),
                    username = it[USERS.USERNAME],
                    email = it[USERS.EMAIL],
                    password = it[USERS.PASSWORD]
                )
            }
    }

    fun addUser(user: User, create: DSLContext = dslContext): Boolean {
        return create.insertInto(USERS,
                USERS.USERNAME, USERS.EMAIL, USERS.PASSWORD)
            .values(user.username, user.email, user.password)
            .execute() == 1
    }

    fun updateUser(user: User, create: DSLContext = dslContext): Boolean {
        return create.update(USERS)
            .set(USERS.USERNAME, user.username)
            .set(USERS.EMAIL, user.email)
            .set(USERS.PASSWORD, user.password)
            .where(USERS.USER_ID.eq(ULong.valueOf(user.id)))
            .execute() == 1
    }

    fun loginUser(user: User, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(USERS.USERNAME.eq(user.username))
        condition = condition.and(USERS.PASSWORD.eq(user.password))

        val db = create.select()
            .from(USERS)
            .where(condition)
            .fetchOne()?.map {
                User(
                    id = it[USERS.USER_ID].toLong(),
                    username = it[USERS.USERNAME],
                    email = it[USERS.EMAIL],
                    password = it[USERS.PASSWORD]
                )
            }
        return db != null
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

    fun addPantryToUser(pantry_x_user: Pantry_x_User, create: DSLContext = dslContext): Boolean {
        return  create.insertInto(PANTRY_X_USER,
            PANTRY_X_USER.PANTRY_ID, PANTRY_X_USER.USER_ID)
            .values(pantry_x_user.userId, pantry_x_user.pantryId)
            .execute() == 1
    }

    fun deletePantryUser(pantry_x_user: Pantry_x_User, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PANTRY_X_USER.PANTRY_ID.eq(pantry_x_user.pantryId))
        condition = condition.and(PANTRY_X_USER.USER_ID.eq(pantry_x_user.userId))

        return create.delete(PANTRY_X_USER)
            .where(condition)
            .execute() == 1
    }

    fun getUserPantries (user_id: Long, create: DSLContext = dslContext): List<Pantry> {
        return create.select()
            .from(USERS)
            .join(PANTRY_X_USER).using(USERS.USER_ID)
            .join(PANTRIES).using(PANTRY_X_USER.PANTRY_ID)
            .where(USERS.USER_ID.eq(ULong.valueOf(user_id)))
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


    fun generateShoppingList (user_id: Long, create: DSLContext = dslContext): List<ProductInPantry> {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(USERS.USER_ID.eq(ULong.valueOf(user_id)))
        condition = condition.and(PRODUCT_X_PANTRY.HAVE_QTY.lessThan(PRODUCT_X_PANTRY.WANT_QTY))

        return create.select()
            .from(USERS)
            .join(PANTRY_X_USER).using(USERS.USER_ID)
            .join(PANTRIES).using(PANTRY_X_USER.PANTRY_ID)
            .join(PRODUCT_X_PANTRY).using(PANTRIES.PANTRY_ID)
            .join(PRODUCTS).using(PRODUCT_X_PANTRY.PRODUCT_ID)
            .where(condition)
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

    fun addShoppingList (shopping_list: Shopping_list, create: DSLContext = dslContext): Boolean {
        return create.insertInto(SHOPPING_LIST,
            SHOPPING_LIST.SHOP_ID, SHOPPING_LIST.USER_ID, SHOPPING_LIST.NAME)
            .values(shopping_list.shopId, shopping_list.userId, shopping_list.name)
            .execute() == 1
    }

    fun updateShopppingList (shopping_list: Shopping_list, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOPPING_LIST.SHOP_ID.eq(shopping_list.shopId))
        condition = condition.and(SHOPPING_LIST.USER_ID.eq(shopping_list.userId))

        return create.update(SHOPPING_LIST)
            .set(SHOPPING_LIST.SHOP_ID, shopping_list.shopId)
            .set(SHOPPING_LIST.USER_ID, shopping_list.userId)
            .set(SHOPPING_LIST.NAME, shopping_list.name)
            .where(condition)
            .execute() == 1
    }

    fun deleteShoppingList (shopping_list: Shopping_list, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOPPING_LIST.SHOP_ID.eq(shopping_list.shopId))
        condition = condition.and(SHOPPING_LIST.USER_ID.eq(shopping_list.userId))

        return create.delete(SHOPPING_LIST)
            .where(condition)
            .execute() == 1
    }

    fun getShoppingLists (user_id: Long, create: DSLContext = dslContext): List<Shopping_list> {
        return create.select()
            .from(SHOPPING_LIST)
            .where(SHOPPING_LIST.USER_ID.eq(user_id))
            .fetch().map {
                Shopping_list(
                    shopId = it[SHOPPING_LIST.SHOP_ID],
                    userId = it[SHOPPING_LIST.USER_ID],
                    name = it[SHOPPING_LIST.NAME]
                )
            }
    }

    fun getShoppingList(user_id: Long, shop_id: Long, create: DSLContext = dslContext): List<ProductInShoppingList> {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOPPING_LIST.SHOP_ID.eq(shop_id))
        condition = condition.and(SHOPPING_LIST.USER_ID.eq(user_id))
        condition = condition.and(PRODUCT_X_PANTRY.HAVE_QTY.lessThan(PRODUCT_X_PANTRY.WANT_QTY))

        return create.select()
            .from(SHOPPING_LIST)
            .join(SHOPS).using(SHOPPING_LIST.SHOP_ID)
            .join(SHOP_X_PRODUCT).using(SHOPPING_LIST.SHOP_ID)
            .join(PRODUCTS).using(SHOP_X_PRODUCT.PRODUCT_ID)
            .join(PANTRY_X_USER).using(SHOPPING_LIST.USER_ID)
            .join(PRODUCT_X_PANTRY).using(PANTRY_X_USER.PANTRY_ID)
            .where(condition)
            .fetch().map {
                ProductInShoppingList(
                    product_id = it[PRODUCTS.PRODUCT_ID].toLong(),
                    pantry_id = it[PANTRY_X_USER.PANTRY_ID],
                    shop_id = it[SHOPS.SHOP_ID].toLong(),
                    product_name = it[PRODUCTS.NAME],
                    shop_name = it[SHOPS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    reviewNumber = it[PRODUCTS.REVIEW_NUMBER],
                    reviewScore = it[PRODUCTS.REVIEW_SCORE],
                    amountAvailable = it[PRODUCT_X_PANTRY.HAVE_QTY],
                    amountNeeded = it[PRODUCT_X_PANTRY.WANT_QTY],
                    price = it[SHOP_X_PRODUCT.PRICE]
                )
            }
    }

}