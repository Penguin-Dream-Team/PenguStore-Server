/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.tables.LocalProductImages;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LocalProductImagesRecord extends UpdatableRecordImpl<LocalProductImagesRecord> implements Record3<ULong, ULong, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>local_product_images.id</code>.
     */
    public LocalProductImagesRecord setId(ULong value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>local_product_images.id</code>.
     */
    public ULong getId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>local_product_images.product_id</code>.
     */
    public LocalProductImagesRecord setProductId(ULong value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>local_product_images.product_id</code>.
     */
    public ULong getProductId() {
        return (ULong) get(1);
    }

    /**
     * Setter for <code>local_product_images.image_url</code>.
     */
    public LocalProductImagesRecord setImageUrl(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>local_product_images.image_url</code>.
     */
    public String getImageUrl() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<ULong, ULong, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return LocalProductImages.LOCAL_PRODUCT_IMAGES.ID;
    }

    @Override
    public Field<ULong> field2() {
        return LocalProductImages.LOCAL_PRODUCT_IMAGES.PRODUCT_ID;
    }

    @Override
    public Field<String> field3() {
        return LocalProductImages.LOCAL_PRODUCT_IMAGES.IMAGE_URL;
    }

    @Override
    public ULong component1() {
        return getId();
    }

    @Override
    public ULong component2() {
        return getProductId();
    }

    @Override
    public String component3() {
        return getImageUrl();
    }

    @Override
    public ULong value1() {
        return getId();
    }

    @Override
    public ULong value2() {
        return getProductId();
    }

    @Override
    public String value3() {
        return getImageUrl();
    }

    @Override
    public LocalProductImagesRecord value1(ULong value) {
        setId(value);
        return this;
    }

    @Override
    public LocalProductImagesRecord value2(ULong value) {
        setProductId(value);
        return this;
    }

    @Override
    public LocalProductImagesRecord value3(String value) {
        setImageUrl(value);
        return this;
    }

    @Override
    public LocalProductImagesRecord values(ULong value1, ULong value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LocalProductImagesRecord
     */
    public LocalProductImagesRecord() {
        super(LocalProductImages.LOCAL_PRODUCT_IMAGES);
    }

    /**
     * Create a detached, initialised LocalProductImagesRecord
     */
    public LocalProductImagesRecord(ULong id, ULong productId, String imageUrl) {
        super(LocalProductImages.LOCAL_PRODUCT_IMAGES);

        setId(id);
        setProductId(productId);
        setImageUrl(imageUrl);
    }
}
