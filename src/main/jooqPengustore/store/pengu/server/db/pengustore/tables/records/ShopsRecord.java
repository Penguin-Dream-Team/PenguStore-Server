/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.tables.Shops;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShopsRecord extends UpdatableRecordImpl<ShopsRecord> implements Record4<ULong, String, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>shops.shop_id</code>.
     */
    public ShopsRecord setShopId(ULong value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>shops.shop_id</code>.
     */
    public ULong getShopId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>shops.name</code>.
     */
    public ShopsRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>shops.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>shops.latitude</code>.
     */
    public ShopsRecord setLatitude(Double value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>shops.latitude</code>.
     */
    public Double getLatitude() {
        return (Double) get(2);
    }

    /**
     * Setter for <code>shops.longitude</code>.
     */
    public ShopsRecord setLongitude(Double value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>shops.longitude</code>.
     */
    public Double getLongitude() {
        return (Double) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, String, Double, Double> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, String, Double, Double> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Shops.SHOPS.SHOP_ID;
    }

    @Override
    public Field<String> field2() {
        return Shops.SHOPS.NAME;
    }

    @Override
    public Field<Double> field3() {
        return Shops.SHOPS.LATITUDE;
    }

    @Override
    public Field<Double> field4() {
        return Shops.SHOPS.LONGITUDE;
    }

    @Override
    public ULong component1() {
        return getShopId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public Double component3() {
        return getLatitude();
    }

    @Override
    public Double component4() {
        return getLongitude();
    }

    @Override
    public ULong value1() {
        return getShopId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public Double value3() {
        return getLatitude();
    }

    @Override
    public Double value4() {
        return getLongitude();
    }

    @Override
    public ShopsRecord value1(ULong value) {
        setShopId(value);
        return this;
    }

    @Override
    public ShopsRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public ShopsRecord value3(Double value) {
        setLatitude(value);
        return this;
    }

    @Override
    public ShopsRecord value4(Double value) {
        setLongitude(value);
        return this;
    }

    @Override
    public ShopsRecord values(ULong value1, String value2, Double value3, Double value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ShopsRecord
     */
    public ShopsRecord() {
        super(Shops.SHOPS);
    }

    /**
     * Create a detached, initialised ShopsRecord
     */
    public ShopsRecord(ULong shopId, String name, Double latitude, Double longitude) {
        super(Shops.SHOPS);

        setShopId(shopId);
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
    }
}