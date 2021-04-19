/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.tables.ProductsUsers;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProductsUsersRecord extends UpdatableRecordImpl<ProductsUsersRecord> implements Record2<ULong, ULong> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>products_users.product_id</code>.
     */
    public ProductsUsersRecord setProductId(ULong value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>products_users.product_id</code>.
     */
    public ULong getProductId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>products_users.user_id</code>.
     */
    public ProductsUsersRecord setUserId(ULong value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>products_users.user_id</code>.
     */
    public ULong getUserId() {
        return (ULong) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<ULong, ULong> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<ULong, ULong> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<ULong, ULong> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return ProductsUsers.PRODUCTS_USERS.PRODUCT_ID;
    }

    @Override
    public Field<ULong> field2() {
        return ProductsUsers.PRODUCTS_USERS.USER_ID;
    }

    @Override
    public ULong component1() {
        return getProductId();
    }

    @Override
    public ULong component2() {
        return getUserId();
    }

    @Override
    public ULong value1() {
        return getProductId();
    }

    @Override
    public ULong value2() {
        return getUserId();
    }

    @Override
    public ProductsUsersRecord value1(ULong value) {
        setProductId(value);
        return this;
    }

    @Override
    public ProductsUsersRecord value2(ULong value) {
        setUserId(value);
        return this;
    }

    @Override
    public ProductsUsersRecord values(ULong value1, ULong value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProductsUsersRecord
     */
    public ProductsUsersRecord() {
        super(ProductsUsers.PRODUCTS_USERS);
    }

    /**
     * Create a detached, initialised ProductsUsersRecord
     */
    public ProductsUsersRecord(ULong productId, ULong userId) {
        super(ProductsUsers.PRODUCTS_USERS);

        setProductId(productId);
        setUserId(userId);
    }
}