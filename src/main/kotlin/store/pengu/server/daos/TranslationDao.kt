package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import store.pengu.server.InternalServerErrorException
import store.pengu.server.db.pengustore.Tables.*
import store.pengu.server.googleTranslateAPI
import java.io.IOException

class TranslationDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun findTranslation(string: String, create: DSLContext = dslContext): String {
        val stringTranslation = create.select()
            .from(TRANSLATION)
            .where(TRANSLATION.STRING.eq(string))
            .fetch().map {
                it[TRANSLATION.TRANSLATION_]
            }

        if (stringTranslation.isNotEmpty()) return stringTranslation[0]
        try {
            val googleTranslation = googleTranslateAPI(string)
            println("Translated $string to $googleTranslation")

            create.insertInto(TRANSLATION, TRANSLATION.STRING, TRANSLATION.TRANSLATION_)
                .values(string, googleTranslation)
                .execute()

            return googleTranslation

        } catch (ex: IOException) {
            println(ex.message)
            return string

        } catch (ex: IllegalStateException) {
            println(ex.message)
            return string
        }
    }
}