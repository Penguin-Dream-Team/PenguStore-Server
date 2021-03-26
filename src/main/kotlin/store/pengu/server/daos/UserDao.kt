package store.pengu.server.daos

import org.jetbrains.annotations.NotNull
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.Pantry
import store.pengu.server.data.Pantry_x_User
import store.pengu.server.data.ProductInPantry
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Pantries.PANTRIES
import store.pengu.server.db.pengustore.tables.PantryXUser.PANTRY_X_USER
import store.pengu.server.db.pengustore.tables.ProductXPantry
import store.pengu.server.db.pengustore.tables.Products
import store.pengu.server.db.pengustore.tables.Users.USERS
import store.pengu.server.db.pengustore.tables.records.UsersRecord

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
                    name = it[Pantries.PANTRIES.NAME]
                )
            }
    }

    fun addPantryToUser(pantry_x_user: Pantry_x_User, create: DSLContext = dslContext): Boolean {
        return  create.insertInto(PANTRY_X_USER,
            PANTRY_X_USER.PANTRY_ID, PANTRY_X_USER.USER_ID)
            .values(pantry_x_user.pantry_id, pantry_x_user.user_id)
            .execute() == 1
    }

    fun deletePantryUser(pantry_x_user: Pantry_x_User, create: DSLContext = dslContext): Boolean {
        var condition = DSL.noCondition() // Alternatively, use trueCondition()
        condition = condition.and(PANTRY_X_USER.PANTRY_ID.eq(pantry_x_user.pantry_id))
        condition = condition.and(PANTRY_X_USER.USER_ID.eq(pantry_x_user.user_id))

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
                    name = it[PANTRIES.NAME]
                )
            }
    }


}