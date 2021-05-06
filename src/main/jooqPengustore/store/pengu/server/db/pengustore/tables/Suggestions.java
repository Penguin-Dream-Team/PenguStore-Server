/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.types.ULong;

import store.pengu.server.db.pengustore.DefaultSchema;
import store.pengu.server.db.pengustore.Indexes;
import store.pengu.server.db.pengustore.Keys;
import store.pengu.server.db.pengustore.tables.records.SuggestionsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Suggestions extends TableImpl<SuggestionsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>suggestions</code>
     */
    public static final Suggestions SUGGESTIONS = new Suggestions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SuggestionsRecord> getRecordType() {
        return SuggestionsRecord.class;
    }

    /**
     * The column <code>suggestions.row_number</code>.
     */
    public final TableField<SuggestionsRecord, ULong> ROW_NUMBER = createField(DSL.name("row_number"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>suggestions.col_number</code>.
     */
    public final TableField<SuggestionsRecord, ULong> COL_NUMBER = createField(DSL.name("col_number"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>suggestions.cell_val</code>.
     */
    public final TableField<SuggestionsRecord, Integer> CELL_VAL = createField(DSL.name("cell_val"), SQLDataType.INTEGER.nullable(false), this, "");

    private Suggestions(Name alias, Table<SuggestionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Suggestions(Name alias, Table<SuggestionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>suggestions</code> table reference
     */
    public Suggestions(String alias) {
        this(DSL.name(alias), SUGGESTIONS);
    }

    /**
     * Create an aliased <code>suggestions</code> table reference
     */
    public Suggestions(Name alias) {
        this(alias, SUGGESTIONS);
    }

    /**
     * Create a <code>suggestions</code> table reference
     */
    public Suggestions() {
        this(DSL.name("suggestions"), null);
    }

    public <O extends Record> Suggestions(Table<O> child, ForeignKey<O, SuggestionsRecord> key) {
        super(child, key, SUGGESTIONS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SUGGESTIONS_COL_NUMBER);
    }

    @Override
    public UniqueKey<SuggestionsRecord> getPrimaryKey() {
        return Keys.KEY_SUGGESTIONS_PRIMARY;
    }

    @Override
    public List<UniqueKey<SuggestionsRecord>> getKeys() {
        return Arrays.<UniqueKey<SuggestionsRecord>>asList(Keys.KEY_SUGGESTIONS_PRIMARY);
    }

    @Override
    public List<ForeignKey<SuggestionsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SuggestionsRecord, ?>>asList(Keys.SUGGESTIONS_IBFK_1, Keys.SUGGESTIONS_IBFK_2);
    }

    private transient Products _suggestionsIbfk_1;
    private transient Products _suggestionsIbfk_2;

    public Products suggestionsIbfk_1() {
        if (_suggestionsIbfk_1 == null)
            _suggestionsIbfk_1 = new Products(this, Keys.SUGGESTIONS_IBFK_1);

        return _suggestionsIbfk_1;
    }

    public Products suggestionsIbfk_2() {
        if (_suggestionsIbfk_2 == null)
            _suggestionsIbfk_2 = new Products(this, Keys.SUGGESTIONS_IBFK_2);

        return _suggestionsIbfk_2;
    }

    @Override
    public Suggestions as(String alias) {
        return new Suggestions(DSL.name(alias), this);
    }

    @Override
    public Suggestions as(Name alias) {
        return new Suggestions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Suggestions rename(String name) {
        return new Suggestions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Suggestions rename(Name name) {
        return new Suggestions(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, ULong, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
