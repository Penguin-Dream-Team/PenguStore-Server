/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
import store.pengu.server.db.pengustore.Keys;
import store.pengu.server.db.pengustore.tables.records.RatingsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Ratings extends TableImpl<RatingsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>ratings</code>
     */
    public static final Ratings RATINGS = new Ratings();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RatingsRecord> getRecordType() {
        return RatingsRecord.class;
    }

    /**
     * The column <code>ratings.user_id</code>.
     */
    public final TableField<RatingsRecord, ULong> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>ratings.barcode</code>.
     */
    public final TableField<RatingsRecord, String> BARCODE = createField(DSL.name("barcode"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>ratings.rating</code>.
     */
    public final TableField<RatingsRecord, Integer> RATING = createField(DSL.name("rating"), SQLDataType.INTEGER.nullable(false), this, "");

    private Ratings(Name alias, Table<RatingsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Ratings(Name alias, Table<RatingsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>ratings</code> table reference
     */
    public Ratings(String alias) {
        this(DSL.name(alias), RATINGS);
    }

    /**
     * Create an aliased <code>ratings</code> table reference
     */
    public Ratings(Name alias) {
        this(alias, RATINGS);
    }

    /**
     * Create a <code>ratings</code> table reference
     */
    public Ratings() {
        this(DSL.name("ratings"), null);
    }

    public <O extends Record> Ratings(Table<O> child, ForeignKey<O, RatingsRecord> key) {
        super(child, key, RATINGS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<RatingsRecord> getPrimaryKey() {
        return Keys.KEY_RATINGS_PRIMARY;
    }

    @Override
    public List<UniqueKey<RatingsRecord>> getKeys() {
        return Arrays.<UniqueKey<RatingsRecord>>asList(Keys.KEY_RATINGS_PRIMARY);
    }

    @Override
    public List<ForeignKey<RatingsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<RatingsRecord, ?>>asList(Keys.RATINGS_IBFK_1);
    }

    private transient Users _users;

    public Users users() {
        if (_users == null)
            _users = new Users(this, Keys.RATINGS_IBFK_1);

        return _users;
    }

    @Override
    public Ratings as(String alias) {
        return new Ratings(DSL.name(alias), this);
    }

    @Override
    public Ratings as(Name alias) {
        return new Ratings(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Ratings rename(String name) {
        return new Ratings(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Ratings rename(Name name) {
        return new Ratings(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
