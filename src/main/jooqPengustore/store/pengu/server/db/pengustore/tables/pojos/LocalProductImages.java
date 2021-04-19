/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.pojos;


import java.io.Serializable;

import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LocalProductImages implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ULong  id;
    private final ULong  productId;
    private final String imageUrl;

    public LocalProductImages(LocalProductImages value) {
        this.id = value.id;
        this.productId = value.productId;
        this.imageUrl = value.imageUrl;
    }

    public LocalProductImages(
        ULong  id,
        ULong  productId,
        String imageUrl
    ) {
        this.id = id;
        this.productId = productId;
        this.imageUrl = imageUrl;
    }

    /**
     * Getter for <code>local_product_images.id</code>.
     */
    public ULong getId() {
        return this.id;
    }

    /**
     * Getter for <code>local_product_images.product_id</code>.
     */
    public ULong getProductId() {
        return this.productId;
    }

    /**
     * Getter for <code>local_product_images.image_url</code>.
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LocalProductImages (");

        sb.append(id);
        sb.append(", ").append(productId);
        sb.append(", ").append(imageUrl);

        sb.append(")");
        return sb.toString();
    }
}