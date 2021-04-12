package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.Pantry
import store.pengu.server.data.Product
import store.pengu.server.db.pengustore.tables.Pantries
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.ProductsUsers

class ProductDao(
    conf: Configuration
) {
    private val dslContext = DSL.using(conf)

    // Products

    fun addProduct(product: Product, create: DSLContext = dslContext): Product? {
        return  create.insertInto(PRODUCTS,
                PRODUCTS.NAME, PRODUCTS.BARCODE)
                .values(product.name, product.barcode)
                .returningResult(PRODUCTS.ID, PRODUCTS.NAME, PRODUCTS.BARCODE)
                .fetchOne()?.map {
                    Product(
                        id = it[PRODUCTS.ID].toLong(),
                        name = it[PRODUCTS.NAME],
                        barcode = it[PRODUCTS.BARCODE]
                    )
                }
    }

    fun updateProduct(product: Product, create: DSLContext = dslContext): Boolean {
        return create.update(PRODUCTS)
                .set(PRODUCTS.NAME, product.name)
                .where(PRODUCTS.ID.eq(ULong.valueOf(product.id)))
                .execute() == 1
    }

    fun getProduct(id: Long, create: DSLContext = dslContext): Product? {
        return create.select()
            .from(PRODUCTS)
            .where(PRODUCTS.ID.eq(ULong.valueOf(id)))
            .fetchOne()?.map {
                Product(
                    id = it[PRODUCTS.ID].toLong(),
                    name = it[PRODUCTS.NAME],
                    barcode = it[PRODUCTS.BARCODE]
                )
            }
    }


    // Aux

    fun connectProduct(product_id: Long, user_id: Long, create: DSLContext = dslContext): Boolean {
        return create.insertInto(
            ProductsUsers.PRODUCTS_USERS,
            ProductsUsers.PRODUCTS_USERS.PRODUCT_ID, ProductsUsers.PRODUCTS_USERS.USER_ID
        )
            .values(ULong.valueOf(product_id), ULong.valueOf(user_id))
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