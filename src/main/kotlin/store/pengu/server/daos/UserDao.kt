package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.ForbiddenException
import store.pengu.server.NotFoundException
import store.pengu.server.application.RefreshToken
import store.pengu.server.data.*
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantriesUsers.PANTRIES_USERS
import store.pengu.server.db.pengustore.tables.ShoppingList.SHOPPING_LIST
import store.pengu.server.db.pengustore.tables.ShoppingListUsers.SHOPPING_LIST_USERS
import store.pengu.server.db.pengustore.tables.Users.USERS
import store.pengu.server.routes.requests.LoginRequest
import store.pengu.server.routes.responses.GuestLoginResponse
import store.pengu.server.utils.PasswordUtils
import java.security.SecureRandom

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
    ): Boolean {
        val id = ULong.valueOf(userId.toLong())
        return create.update(USERS).set(USERS.ID, id).apply {
            if (!username.isNullOrBlank()) {
                set(USERS.USERNAME, username)
            }
            if (!password.isNullOrBlank()) {
                set(USERS.PASSWORD, password)
            }
        }
            .where(USERS.ID.eq(id))
            .execute() == 1
    }

    fun loginUser(username: String, password: String, create: DSLContext = dslContext): RefreshToken {
        return create.select(USERS.ID)
            .from(USERS)
            .where(USERS.USERNAME.eq(username))
            .and(USERS.PASSWORD.eq(password))
            .fetchOne()?.map {
                RefreshToken(it[USERS.ID].toInt())
            } ?: throw ForbiddenException("Account credentials are not correct")
    }

    fun registerGuestUser(username: String, create: DSLContext = dslContext): GuestLoginResponse {
        return create.insertInto(USERS, USERS.USERNAME, USERS.PASSWORD)
            .values(username, PasswordUtils.generatePassword())
            .returningResult(USERS.ID, USERS.PASSWORD)
            .fetchOne()?.map {
                val token = RefreshToken(it[USERS.ID].toInt())
                GuestLoginResponse(password = it[USERS.PASSWORD], token.token, token.refreshToken)
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
                    latitude = it[PANTRIES.LATITUDE].toFloat(),
                    longitude = it[PANTRIES.LONGITUDE].toFloat(),
                    product_num = 0
                )
            }
    }

    fun connectPantryToUser(pantry_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            PANTRIES_USERS,
            PANTRIES_USERS.PANTRY_ID, PANTRIES_USERS.USER_ID
        )
            .values(ULong.valueOf(pantry_id), ULong.valueOf(user_id))
            .execute() == 1
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

    fun getShoppingLists(user_id: Long, create: DSLContext = dslContext): List<Shopping_list> {
        return create.select()
            .from(SHOPPING_LIST_USERS)
            .join(SHOPPING_LIST).on(SHOPPING_LIST.ID.eq(SHOPPING_LIST_USERS.SHOPPING_LIST_ID))
            .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(user_id)))
            .fetch().map {
                Shopping_list(
                    id = it[SHOPPING_LIST.ID].toLong(),
                    name = it[SHOPPING_LIST.NAME],
                    latitude = it[SHOPPING_LIST.LATITUDE].toFloat(),
                    longitude = it[SHOPPING_LIST.LONGITUDE].toFloat()
                )
            }
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