package store.pengu.server.daos

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.ForbiddenException
import store.pengu.server.InternalServerErrorException
import store.pengu.server.NotFoundException
import store.pengu.server.application.LoggedUser
import store.pengu.server.data.Pantry
import store.pengu.server.data.Product
import store.pengu.server.data.ProductInPantry
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantriesUsers.PANTRIES_USERS
import store.pengu.server.db.pengustore.tables.PantryProducts
import store.pengu.server.db.pengustore.tables.Products
import store.pengu.server.db.pengustore.tables.ShoppingListUsers.SHOPPING_LIST_USERS
import store.pengu.server.db.pengustore.tables.Users.USERS
import store.pengu.server.routes.responses.GuestLoginResponse
import store.pengu.server.utils.PasswordUtils

class UserDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    // User Login
    fun getProfile(id: Int, create: DSLContext = dslContext): User {
        return create.select()
            .from(USERS)
            .where(USERS.ID.eq(ULong.valueOf(id.toLong())))
            .fetchOne()?.map {
                User(
                    username = it[USERS.USERNAME],
                    email = it[USERS.EMAIL]
                )
            } ?: throw NotFoundException("Profile not found")
    }

    fun updateUser(
        userId: Int,
        username: String?,
        email: String?,
        password: String?,
        create: DSLContext = dslContext
    ): User {
        val id = ULong.valueOf(userId.toLong())
        val success = create.update(USERS).set(USERS.ID, id).apply {
            if (!username.isNullOrBlank()) {
                set(USERS.USERNAME, username)
            }
            if (!password.isNullOrBlank()) {
                set(USERS.PASSWORD, password)
            }
            if (!email.isNullOrBlank()) {
                set(USERS.EMAIL, email)
            }
        }
            .where(USERS.ID.eq(id))
            .execute() == 1
        if (success) {
            return getProfile(userId)
        } else {
            throw InternalServerErrorException("Could not update user")
        }
    }

    fun loginUser(username: String, password: String, create: DSLContext = dslContext): LoggedUser {
        return create.select(USERS.ID)
            .from(USERS)
            .where(USERS.USERNAME.eq(username))
            .and(USERS.PASSWORD.eq(password))
            .fetchOne()?.map {
                LoggedUser(it[USERS.ID].toInt())
            } ?: throw ForbiddenException("Account credentials are not correct")
    }

    fun registerGuestUser(username: String, create: DSLContext = dslContext): GuestLoginResponse {
        return create.insertInto(USERS, USERS.USERNAME, USERS.PASSWORD)
            .values(username, PasswordUtils.generatePassword())
            .returningResult(USERS.ID, USERS.PASSWORD)
            .fetchOne()?.map {
                val token = LoggedUser(it[USERS.ID].toInt())
                GuestLoginResponse(password = it[USERS.PASSWORD], token.token)
            } ?: throw ForbiddenException("Error registering new account")
    }


    // User Pantry

    fun getPantryByCode(code: String, create: DSLContext = dslContext): Pantry? {
        return create.select()
            .from(PANTRIES)
            .where(PANTRIES.CODE.eq(code))
            .fetchOne()?.map {
                Pantry(
                    id = it[PANTRIES.ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE],
                    longitude = it[PANTRIES.LONGITUDE],
                    productCount = 0,
                    color = it[PANTRIES.COLOR],
                    false
                )
            }
    }

    fun connectPantryToUser(pantry_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        var res = create.insertInto(
            PANTRIES_USERS,
            PANTRIES_USERS.PANTRY_ID, PANTRIES_USERS.USER_ID
        )
            .values(ULong.valueOf(pantry_id), ULong.valueOf(user_id))
            .execute() == 1

        var pantryProducts = create.select()
            .from(PANTRIES)
            .join(PantryProducts.PANTRY_PRODUCTS).on(PantryProducts.PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
            .join(Products.PRODUCTS).on(Products.PRODUCTS.ID.eq(PantryProducts.PANTRY_PRODUCTS.PRODUCT_ID))
            .where(PANTRIES.ID.eq((ULong.valueOf(pantry_id))))
            .fetch().map {
                ProductInPantry(
                    productId = it[Products.PRODUCTS.ID].toLong(),
                    pantryId = it[PANTRIES.ID].toLong(),
                    barcode = it[Products.PRODUCTS.BARCODE],
                    name = it[Products.PRODUCTS.NAME],
                    amountAvailable = it[PantryProducts.PANTRY_PRODUCTS.HAVE_QTY],
                    amountNeeded = it[PantryProducts.PANTRY_PRODUCTS.WANT_QTY]
                )
            }

        var itr = pantryProducts.iterator()
        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        itr.forEach {
            condition = DSL.noCondition()
            condition = condition.and(PRODUCTS_USERS.USER_ID.eq(ULong.valueOf(user_id)))
            condition = condition.and(PRODUCTS_USERS.PRODUCT_ID.eq(ULong.valueOf(it.productId)))

            var product = create.select()
                .from(PRODUCTS_USERS)
                .where(condition)
                .fetchOne()?.map {
                    it[PRODUCTS_USERS.PRODUCT_ID]
                }

            if (product == null) {
                connectProduct(it.productId, user_id)
            }
        }

        return res
    }

    fun disconnectPantryUser(pantry_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PANTRIES_USERS.PANTRY_ID.eq(ULong.valueOf(pantry_id)))
        condition = condition.and(PANTRIES_USERS.USER_ID.eq(ULong.valueOf(user_id)))

        return create.delete(PANTRIES_USERS)
            .where(condition)
            .execute() == 1
    }

    fun getUserPantries(user_id: Long, create: DSLContext = dslContext): List<Pantry> {
        return create.select()
            .from(USERS)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
            .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .where(USERS.ID.eq(ULong.valueOf(user_id)))
            .fetch().map {
                Pantry(
                    id = it[PANTRIES.ID].toLong(),
                    code = it[PANTRIES.CODE],
                    name = it[PANTRIES.NAME],
                    latitude = it[PANTRIES.LATITUDE],
                    longitude = it[PANTRIES.LONGITUDE],
                    productCount = create.fetchCount(
                        DSL.select()
                            .from(PANTRIES)
                            .join(PANTRY_PRODUCTS).on(PANTRY_PRODUCTS.PANTRY_ID.eq(PANTRIES.ID))
                            .where(PANTRIES.ID.eq(it[PANTRIES.ID]))
                    ),
                    color = it[PANTRIES.COLOR],
                    false
                )
            }
    }


    // User Shopping List

    fun connectShoppingList(shopping_list_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            SHOPPING_LIST_USERS,
            SHOPPING_LIST_USERS.SHOPPING_LIST_ID, SHOPPING_LIST_USERS.USER_ID
        )
            .values(ULong.valueOf(shopping_list_id), ULong.valueOf(user_id))
            .execute() == 1
    }

    fun disconnectShoppingList(shopping_list_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(SHOPPING_LIST_USERS.SHOPPING_LIST_ID.eq(ULong.valueOf(shopping_list_id)))
        condition = condition.and(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(user_id)))

        return create.delete(SHOPPING_LIST_USERS)
            .where(condition)
            .execute() == 1
    }


    // User Products

    fun connectProduct(product_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            PRODUCTS_USERS,
            PRODUCTS_USERS.PRODUCT_ID, PRODUCTS_USERS.USER_ID
        )
            .values(ULong.valueOf(product_id), ULong.valueOf(user_id))
            .execute() == 1
    }

    fun disconnectProduct(product_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PRODUCTS_USERS.PRODUCT_ID.eq(ULong.valueOf(product_id)))
        condition = condition.and(PRODUCTS_USERS.USER_ID.eq(ULong.valueOf(user_id)))

        return create.delete(PRODUCTS_USERS)
            .where(condition)
            .execute() == 1
    }

    fun getProducts(user_id: Long, create: DSLContext = dslContext): List<Product> {
        return create.select()
            .from(PRODUCTS_USERS)
            .join(PRODUCTS).on(PRODUCTS.ID.eq(PRODUCTS_USERS.PRODUCT_ID))
            .where(PRODUCTS_USERS.USER_ID.eq(ULong.valueOf(user_id)))
            .fetch().map {
                Product(
                    id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE]
                )
            }
    }
}