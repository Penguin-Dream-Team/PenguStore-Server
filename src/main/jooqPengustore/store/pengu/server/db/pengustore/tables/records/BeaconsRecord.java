/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;

import store.pengu.server.db.pengustore.tables.Beacons;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BeaconsRecord extends UpdatableRecordImpl<BeaconsRecord> implements Record3<Integer, Double, Double> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>beacons.num_items</code>.
     */
    public BeaconsRecord setNumItems(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>beacons.num_items</code>.
     */
    public Integer getNumItems() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>beacons.latitude</code>.
     */
    public BeaconsRecord setLatitude(Double value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>beacons.latitude</code>.
     */
    public Double getLatitude() {
        return (Double) get(1);
    }

    /**
     * Setter for <code>beacons.longitude</code>.
     */
    public BeaconsRecord setLongitude(Double value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>beacons.longitude</code>.
     */
    public Double getLongitude() {
        return (Double) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Double, Double> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Double, Double> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, Double, Double> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Beacons.BEACONS.NUM_ITEMS;
    }

    @Override
    public Field<Double> field2() {
        return Beacons.BEACONS.LATITUDE;
    }

    @Override
    public Field<Double> field3() {
        return Beacons.BEACONS.LONGITUDE;
    }

    @Override
    public Integer component1() {
        return getNumItems();
    }

    @Override
    public Double component2() {
        return getLatitude();
    }

    @Override
    public Double component3() {
        return getLongitude();
    }

    @Override
    public Integer value1() {
        return getNumItems();
    }

    @Override
    public Double value2() {
        return getLatitude();
    }

    @Override
    public Double value3() {
        return getLongitude();
    }

    @Override
    public BeaconsRecord value1(Integer value) {
        setNumItems(value);
        return this;
    }

    @Override
    public BeaconsRecord value2(Double value) {
        setLatitude(value);
        return this;
    }

    @Override
    public BeaconsRecord value3(Double value) {
        setLongitude(value);
        return this;
    }

    @Override
    public BeaconsRecord values(Integer value1, Double value2, Double value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BeaconsRecord
     */
    public BeaconsRecord() {
        super(Beacons.BEACONS);
    }

    /**
     * Create a detached, initialised BeaconsRecord
     */
    public BeaconsRecord(Integer numItems, Double latitude, Double longitude) {
        super(Beacons.BEACONS);

        setNumItems(numItems);
        setLatitude(latitude);
        setLongitude(longitude);
    }
}
