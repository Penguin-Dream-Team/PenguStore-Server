/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

import store.pengu.server.db.pengustore.tables.ShoppingList;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShoppingListRecord extends UpdatableRecordImpl<ShoppingListRecord> implements Record3<Long, Long, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>shopping_list.shop_id</code>.
     */
    public ShoppingListRecord setShopId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>shopping_list.shop_id</code>.
     */
    public Long getShopId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>shopping_list.user_id</code>.
     */
    public ShoppingListRecord setUserId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>shopping_list.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>shopping_list.name</code>.
     */
    public ShoppingListRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>shopping_list.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Long, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return ShoppingList.SHOPPING_LIST.SHOP_ID;
    }

    @Override
    public Field<Long> field2() {
        return ShoppingList.SHOPPING_LIST.USER_ID;
    }

    @Override
    public Field<String> field3() {
        return ShoppingList.SHOPPING_LIST.NAME;
    }

    @Override
    public Long component1() {
        return getShopId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Long value1() {
        return getShopId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public ShoppingListRecord value1(Long value) {
        setShopId(value);
        return this;
    }

    @Override
    public ShoppingListRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public ShoppingListRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public ShoppingListRecord values(Long value1, Long value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ShoppingListRecord
     */
    public ShoppingListRecord() {
        super(ShoppingList.SHOPPING_LIST);
    }

    /**
     * Create a detached, initialised ShoppingListRecord
     */
    public ShoppingListRecord(Long shopId, Long userId, String name) {
        super(ShoppingList.SHOPPING_LIST);

        setShopId(shopId);
        setUserId(userId);
        setName(name);
    }
}