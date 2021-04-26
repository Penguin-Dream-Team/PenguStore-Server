package store.pengu.server.daos

import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.types.ULong
import store.pengu.server.data.Local_Product_Price
import store.pengu.server.data.Product
import store.pengu.server.db.pengustore.Tables
import store.pengu.server.db.pengustore.Tables.CROWD_PRODUCT_IMAGES
import store.pengu.server.db.pengustore.Tables.LOCAL_PRODUCT_IMAGES
import store.pengu.server.db.pengustore.tables.CrowdProductPrices.CROWD_PRODUCT_PRICES
import store.pengu.server.db.pengustore.tables.Products.PRODUCTS
import store.pengu.server.db.pengustore.tables.ProductsUsers
import store.pengu.server.db.pengustore.tables.LocalProductPrices.LOCAL_PRODUCT_PRICES
import store.pengu.server.routes.requests.GetImageRequest
import store.pengu.server.routes.requests.ImageRequest

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

    fun addBarcode(product: Product, create: DSLContext = dslContext): Boolean {
        val res = create.update(PRODUCTS)
            .set(PRODUCTS.BARCODE, product.barcode)
            .where(PRODUCTS.ID.eq(ULong.valueOf(product.id)))
            .execute() == 1

        val localPrices = create.select()
            .from(LOCAL_PRODUCT_PRICES)
            .where(LOCAL_PRODUCT_PRICES.PRODUCT_ID.eq(ULong.valueOf(product.id)))
            .fetch().map{
                Local_Product_Price(
                    product_id = it[LOCAL_PRODUCT_PRICES.PRODUCT_ID].toLong(),
                    price = it[LOCAL_PRODUCT_PRICES.PRICE],
                    latitude = it[LOCAL_PRODUCT_PRICES.LATITUDE],
                    longitude = it[LOCAL_PRODUCT_PRICES.LONGITUDE]
                )
            }

        var condition = DSL.noCondition() // Alternatively, use trueCondition()

        val itr = localPrices.iterator()
        itr.forEach {
            condition = DSL.noCondition()
            condition = condition.and(CROWD_PRODUCT_PRICES.BARCODE.eq(product.barcode))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.le(it.latitude+0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LATITUDE.ge(it.latitude-0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.le(it.longitude+0.0001))
            condition = condition.and(CROWD_PRODUCT_PRICES.LONGITUDE.ge(it.longitude-0.0001))

            val crowd_price = create.select()
                .from(CROWD_PRODUCT_PRICES)
                .where(condition)
                .fetchOne()?.map{
                    it[CROWD_PRODUCT_PRICES.PRICE]
                }

            if (crowd_price == null){
                create.insertInto(
                    Tables.CROWD_PRODUCT_PRICES,
                    Tables.CROWD_PRODUCT_PRICES.BARCODE, Tables.CROWD_PRODUCT_PRICES.PRICE, Tables.CROWD_PRODUCT_PRICES.LATITUDE, Tables.CROWD_PRODUCT_PRICES.LONGITUDE)
                    .values(product.barcode, it.price, it.latitude, it.longitude)
                    .execute() == 1
            }
        }

        return res
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

    fun addImage(imageRequest: ImageRequest, create: DSLContext = dslContext): Boolean {
        if (imageRequest.barcode != null) {
            return create.insertInto(
                CROWD_PRODUCT_IMAGES,
                CROWD_PRODUCT_IMAGES.BARCODE, CROWD_PRODUCT_IMAGES.IMAGE_URL
            )
                .values(imageRequest.barcode, imageRequest.image_url)
                .execute() == 1

        } else {
            return create.insertInto(
                LOCAL_PRODUCT_IMAGES,
                LOCAL_PRODUCT_IMAGES.PRODUCT_ID, LOCAL_PRODUCT_IMAGES.IMAGE_URL
            )
                .values(imageRequest.product_id?.let { ULong.valueOf(it) }, imageRequest.image_url)
                .execute() == 1
        }
    }

    fun deleteImage(imageRequest: ImageRequest, create: DSLContext = dslContext): Boolean {
        return if (imageRequest.barcode != null) {
            create.delete(CROWD_PRODUCT_IMAGES)
                .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(imageRequest.barcode))
                .execute() == 1

        } else {
            create.delete(LOCAL_PRODUCT_IMAGES)
                .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(imageRequest.product_id?.let { ULong.valueOf(it) }))
                .execute() == 1
        }
    }

    fun getImage(imageRequest: GetImageRequest, create: DSLContext = dslContext): List<String> {
        return if (imageRequest.barcode != null) {
            create.select()
                .from(CROWD_PRODUCT_IMAGES)
                .where(CROWD_PRODUCT_IMAGES.BARCODE.eq(imageRequest.barcode))
                .fetch().map {
                    it[CROWD_PRODUCT_IMAGES.IMAGE_URL]
                }

        } else {
            create.select()
                .from(LOCAL_PRODUCT_IMAGES)
                .where(LOCAL_PRODUCT_IMAGES.PRODUCT_ID.eq(imageRequest.product_id?.let { ULong.valueOf(it) }))
                .fetch().map {
                    it[LOCAL_PRODUCT_IMAGES.IMAGE_URL]
                }
        }
    }

}