package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.NotFoundException
import store.pengu.server.data.ShoppingList
import store.pengu.server.data.UserList
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.routes.responses.lists.UserListType

class ListDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun findNearbyShoppingList(
        userId: Long,
        latitude: Double,
        longitude: Double,
        create: DSLContext = dslContext
    ): ShoppingList {
        val (listType, list) = findNearbyList(userId, latitude, longitude, create)
        if (listType != UserListType.SHOPPING_LIST) {
            throw NotFoundException("No list found nearby the specified location")
        }
        return list as ShoppingList
    }

    fun findNearbyList(
        userId: Long,
        latitude: Double,
        longitude: Double,
        create: DSLContext = dslContext
    ): Pair<UserListType, UserList> {
        return create.select()
            .from(SHOPPING_LIST_USERS)
            .join(SHOPPING_LIST).on(SHOPPING_LIST.ID.eq(SHOPPING_LIST_USERS.SHOPPING_LIST_ID))
            .where(SHOPPING_LIST_USERS.USER_ID.eq(ULong.valueOf(userId)))
            .and(getNearbyCondition(SHOPPING_LIST.LATITUDE, latitude, SHOPPING_LIST.LONGITUDE, longitude))
            .fetchAny()?.map {
                UserListType.SHOPPING_LIST to ShopDao.getShoppingListInformation(userId, it, create)
            } ?: create.select()
            .from(USERS)
            .join(PANTRIES_USERS).on(PANTRIES_USERS.USER_ID.eq(USERS.ID))
            .join(PANTRIES).on(PANTRIES.ID.eq(PANTRIES_USERS.PANTRY_ID))
            .where(USERS.ID.eq(ULong.valueOf(userId)))
            .and(getNearbyCondition(PANTRIES.LATITUDE, latitude, PANTRIES.LONGITUDE, longitude))
            .fetchAny()?.map {
                UserListType.PANTRY to PantryDao.getPantryInformation(it, create)
            } ?: throw NotFoundException("No list found nearby the specified location")
    }

    companion object {
        fun <R : Record> getNearbyCondition(
            latitudeColumn: TableField<R, Double>,
            latitude: Double,
            longitudeColumn: TableField<R, Double>,
            longitude: Double,
            offset: Double = 0.0001
        ): Condition {
            var condition = DSL.noCondition()
            condition = condition.and(latitudeColumn.le(latitude + offset))
            condition = condition.and(latitudeColumn.ge(latitude - offset))
            condition = condition.and(longitudeColumn.le(longitude + offset))
            condition = condition.and(longitudeColumn.ge(longitude - offset))
            return condition
        }
    }
}