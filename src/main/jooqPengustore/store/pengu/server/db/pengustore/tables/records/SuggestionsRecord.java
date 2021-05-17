/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.records;


import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.tables.Suggestions;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SuggestionsRecord extends UpdatableRecordImpl<SuggestionsRecord> implements Record4<ULong, String, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>suggestions.user_id</code>.
     */
    public SuggestionsRecord setUserId(ULong value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>suggestions.user_id</code>.
     */
    public ULong getUserId() {
        return (ULong) get(0);
    }

    /**
     * Setter for <code>suggestions.row_number</code>.
     */
    public SuggestionsRecord setRowNumber(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>suggestions.row_number</code>.
     */
    public String getRowNumber() {
        return (String) get(1);
    }

    /**
     * Setter for <code>suggestions.col_number</code>.
     */
    public SuggestionsRecord setColNumber(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>suggestions.col_number</code>.
     */
    public String getColNumber() {
        return (String) get(2);
    }

    /**
     * Setter for <code>suggestions.cell_val</code>.
     */
    public SuggestionsRecord setCellVal(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>suggestions.cell_val</code>.
     */
    public Integer getCellVal() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record3<ULong, String, String> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<ULong, String, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<ULong, String, String, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<ULong> field1() {
        return Suggestions.SUGGESTIONS.USER_ID;
    }

    @Override
    public Field<String> field2() {
        return Suggestions.SUGGESTIONS.ROW_NUMBER;
    }

    @Override
    public Field<String> field3() {
        return Suggestions.SUGGESTIONS.COL_NUMBER;
    }

    @Override
    public Field<Integer> field4() {
        return Suggestions.SUGGESTIONS.CELL_VAL;
    }

    @Override
    public ULong component1() {
        return getUserId();
    }

    @Override
    public String component2() {
        return getRowNumber();
    }

    @Override
    public String component3() {
        return getColNumber();
    }

    @Override
    public Integer component4() {
        return getCellVal();
    }

    @Override
    public ULong value1() {
        return getUserId();
    }

    @Override
    public String value2() {
        return getRowNumber();
    }

    @Override
    public String value3() {
        return getColNumber();
    }

    @Override
    public Integer value4() {
        return getCellVal();
    }

    @Override
    public SuggestionsRecord value1(ULong value) {
        setUserId(value);
        return this;
    }

    @Override
    public SuggestionsRecord value2(String value) {
        setRowNumber(value);
        return this;
    }

    @Override
    public SuggestionsRecord value3(String value) {
        setColNumber(value);
        return this;
    }

    @Override
    public SuggestionsRecord value4(Integer value) {
        setCellVal(value);
        return this;
    }

    @Override
    public SuggestionsRecord values(ULong value1, String value2, String value3, Integer value4) {
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
     * Create a detached SuggestionsRecord
     */
    public SuggestionsRecord() {
        super(Suggestions.SUGGESTIONS);
    }

    /**
     * Create a detached, initialised SuggestionsRecord
     */
    public SuggestionsRecord(ULong userId, String rowNumber, String colNumber, Integer cellVal) {
        super(Suggestions.SUGGESTIONS);

        setUserId(userId);
        setRowNumber(rowNumber);
        setColNumber(colNumber);
        setCellVal(cellVal);
    }
}
