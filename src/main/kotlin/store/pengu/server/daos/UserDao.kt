package store.pengu.server.daos

import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL
import store.pengu.server.data.User
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
                    id = it[USERS.USERID],
                    name = it[USERS.NAME]
                )
            }

    }
}