/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;

import store.pengu.server.db.pengustore.tables.ProductXImage;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProductXImageRecord extends UpdatableRecordImpl<ProductXImageRecord> implements Record2<Long, byte[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>product_x_image.product_id</code>.
     */
    public ProductXImageRecord setProductId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>product_x_image.product_id</code>.
     */
    public Long getProductId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>product_x_image.image</code>.
     */
    public ProductXImageRecord setImage(byte[] value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>product_x_image.image</code>.
     */
    public byte[] getImage() {
        return (byte[]) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, byte[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Long, byte[]> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ProductXImage.PRODUCT_X_IMAGE.PRODUCT_ID;
    }

    @Override
    public Field<byte[]> field2() {
        return ProductXImage.PRODUCT_X_IMAGE.IMAGE;
    }

    @Override
    public Long component1() {
        return getProductId();
    }

    @Override
    public byte[] component2() {
        return getImage();
    }

    @Override
    public Long value1() {
        return getProductId();
    }

    @Override
    public byte[] value2() {
        return getImage();
    }

    @Override
    public ProductXImageRecord value1(Long value) {
        setProductId(value);
        return this;
    }

    @Override
    public ProductXImageRecord value2(byte[] value) {
        setImage(value);
        return this;
    }

    @Override
    public ProductXImageRecord values(Long value1, byte[] value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProductXImageRecord
     */
    public ProductXImageRecord() {
        super(ProductXImage.PRODUCT_X_IMAGE);
    }

    /**
     * Create a detached, initialised ProductXImageRecord
     */
    public ProductXImageRecord(Long productId, byte[] image) {
        super(ProductXImage.PRODUCT_X_IMAGE);

        setProductId(productId);
        setImage(image);
    }
}
