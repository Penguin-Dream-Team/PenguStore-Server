package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.Product
import store.pengu.server.data.Product_x_Image
import store.pengu.server.data.User
import store.pengu.server.db.pengustore.tables.ProductXImage.PRODUCT_X_IMAGE
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.Users.USERS
import store.pengu.server.db.pengustore.tables.records.UsersRecord

class ProductDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    fun getProducts(create: DSLContext = dslContext): List<Product> {
        return create.select()
            .from(PRODUCTS)
            .fetch().map {
                Product(
                    id = it[PRODUCTS.PRODUCT_ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    review_score = it[PRODUCTS.REVIEW_SCORE].toDouble(),
                    review_number = it[PRODUCTS.REVIEW_NUMBER]
                )
            }

    }

    fun getProduct(id: Long, create: DSLContext = dslContext): Product? {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Product(
                    id = it[PRODUCTS.PRODUCT_ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE],
                    review_score = it[PRODUCTS.REVIEW_SCORE].toDouble(),
                    review_number = it[PRODUCTS.REVIEW_NUMBER]
                )
            }
    }

    fun addProduct(product: Product, create: DSLContext = dslContext): Boolean {
        return create.insertInto(PRODUCTS,
                PRODUCTS.NAME, PRODUCTS.BARCODE, PRODUCTS.REVIEW_SCORE, PRODUCTS.REVIEW_NUMBER)
            .values(product.name, product.barcode, product.review_score, product.review_number)
            .execute() == 1
    }

    fun updateProduct(product: Product, create: DSLContext = dslContext): Boolean {
        return create.update(PRODUCTS)
            .set(PRODUCTS.NAME, product.name)
            .set(PRODUCTS.BARCODE, product.barcode)
            .set(PRODUCTS.REVIEW_SCORE, product.review_score)
            .set(PRODUCTS.REVIEW_NUMBER, product.review_number)
            .where(PRODUCTS.PRODUCT_ID.eq(ULong.valueOf(product.id)))
            .execute() == 1
    }

    /*
    fun addImageToProduct(product_x_image: Product_x_Image, create: DSLContext = dslContext): Boolean {
        return create.insertInto(PRODUCT_X_IMAGE,
            PRODUCT_X_IMAGE.PRODUCT_ID, PRODUCT_X_IMAGE.IMAGE)
            .values(product_x_image.product_id, product_x_image.image)
            .execute() == 1
    }

     */

}