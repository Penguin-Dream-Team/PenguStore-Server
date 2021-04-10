/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

import store.pengu.server.db.pengustore.tables.CrowdProductPrices;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CrowdProductPricesRecord extends UpdatableRecordImpl<CrowdProductPricesRecord> implements Record4<String, Double, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>crowd_product_prices.barcode</code>.
     */
    public CrowdProductPricesRecord setBarcode(String value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>crowd_product_prices.barcode</code>.
     */
    public String getBarcode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>crowd_product_prices.price</code>.
     */
    public CrowdProductPricesRecord setPrice(Double value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>crowd_product_prices.price</code>.
     */
    public Double getPrice() {
        return (Double) get(1);
    }

    /**
     * Setter for <code>crowd_product_prices.latitude</code>.
     */
    public CrowdProductPricesRecord setLatitude(Double value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>crowd_product_prices.latitude</code>.
     */
    public Double getLatitude() {
        return (Double) get(2);
    }

    /**
     * Setter for <code>crowd_product_prices.longitude</code>.
     */
    public CrowdProductPricesRecord setLongitude(Double value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>crowd_product_prices.longitude</code>.
     */
    public Double getLongitude() {
        return (Double) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record3<String, Double, Double> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, Double, Double, Double> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<String, Double, Double, Double> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return CrowdProductPrices.CROWD_PRODUCT_PRICES.BARCODE;
    }

    @Override
    public Field<Double> field2() {
        return CrowdProductPrices.CROWD_PRODUCT_PRICES.PRICE;
    }

    @Override
    public Field<Double> field3() {
        return CrowdProductPrices.CROWD_PRODUCT_PRICES.LATITUDE;
    }

    @Override
    public Field<Double> field4() {
        return CrowdProductPrices.CROWD_PRODUCT_PRICES.LONGITUDE;
    }

    @Override
    public String component1() {
        return getBarcode();
    }

    @Override
    public Double component2() {
        return getPrice();
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
    public String value1() {
        return getBarcode();
    }

    @Override
    public Double value2() {
        return getPrice();
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
    public CrowdProductPricesRecord value1(String value) {
        setBarcode(value);
        return this;
    }

    @Override
    public CrowdProductPricesRecord value2(Double value) {
        setPrice(value);
        return this;
    }

    @Override
    public CrowdProductPricesRecord value3(Double value) {
        setLatitude(value);
        return this;
    }

    @Override
    public CrowdProductPricesRecord value4(Double value) {
        setLongitude(value);
        return this;
    }

    @Override
    public CrowdProductPricesRecord values(String value1, Double value2, Double value3, Double value4) {
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
     * Create a detached CrowdProductPricesRecord
     */
    public CrowdProductPricesRecord() {
        super(CrowdProductPrices.CROWD_PRODUCT_PRICES);
    }

    /**
     * Create a detached, initialised CrowdProductPricesRecord
     */
    public CrowdProductPricesRecord(String barcode, Double price, Double latitude, Double longitude) {
        super(CrowdProductPrices.CROWD_PRODUCT_PRICES);

        setBarcode(barcode);
        setPrice(price);
        setLatitude(latitude);
        setLongitude(longitude);
    }
}
