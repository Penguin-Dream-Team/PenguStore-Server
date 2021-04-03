/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.tables.Pantries;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PantriesRecord extends UpdatableRecordImpl<PantriesRecord> implements Record5<ULong, String, String, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>pantries.pantry_id</code>.
     */
    public PantriesRecord setPantryId(ULong value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>pantries.pantry_id</code>.
     */
    public ULong getPantryId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>pantries.code</code>.
     */
    public PantriesRecord setCode(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>pantries.code</code>.
     */
    public String getCode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>pantries.name</code>.
     */
    public PantriesRecord setName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>pantries.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>pantries.latitude</code>.
     */
    public PantriesRecord setLatitude(Double value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>pantries.latitude</code>.
     */
    public Double getLatitude() {
        return (Double) get(3);
    }

    /**
     * Setter for <code>pantries.longitude</code>.
     */
    public PantriesRecord setLongitude(Double value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>pantries.longitude</code>.
     */
    public Double getLongitude() {
        return (Double) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<ULong> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<ULong, String, String, Double, Double> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<ULong, String, String, Double, Double> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Pantries.PANTRIES.PANTRY_ID;
    }

    @Override
    public Field<String> field2() {
        return Pantries.PANTRIES.CODE;
    }

    @Override
    public Field<String> field3() {
        return Pantries.PANTRIES.NAME;
    }

    @Override
    public Field<Double> field4() {
        return Pantries.PANTRIES.LATITUDE;
    }

    @Override
    public Field<Double> field5() {
        return Pantries.PANTRIES.LONGITUDE;
    }

    @Override
    public ULong component1() {
        return getPantryId();
    }

    @Override
    public String component2() {
        return getCode();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Double component4() {
        return getLatitude();
    }

    @Override
    public Double component5() {
        return getLongitude();
    }

    @Override
    public ULong value1() {
        return getPantryId();
    }

    @Override
    public String value2() {
        return getCode();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Double value4() {
        return getLatitude();
    }

    @Override
    public Double value5() {
        return getLongitude();
    }

    @Override
    public PantriesRecord value1(ULong value) {
        setPantryId(value);
        return this;
    }

    @Override
    public PantriesRecord value2(String value) {
        setCode(value);
        return this;
    }

    @Override
    public PantriesRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public PantriesRecord value4(Double value) {
        setLatitude(value);
        return this;
    }

    @Override
    public PantriesRecord value5(Double value) {
        setLongitude(value);
        return this;
    }

    @Override
    public PantriesRecord values(ULong value1, String value2, String value3, Double value4, Double value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PantriesRecord
     */
    public PantriesRecord() {
        super(Pantries.PANTRIES);
    }

    /**
     * Create a detached, initialised PantriesRecord
     */
    public PantriesRecord(ULong pantryId, String code, String name, Double latitude, Double longitude) {
        super(Pantries.PANTRIES);

        setPantryId(pantryId);
        setCode(code);
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
    }
}
