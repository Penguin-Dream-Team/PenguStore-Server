/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
import store.pengu.server.db.pengustore.tables.records.ProductsRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Products extends TableImpl<ProductsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>products</code>
     */
    public static final Products PRODUCTS = new Products();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProductsRecord> getRecordType() {
        return ProductsRecord.class;
    }

    /**
     * The column <code>products.id</code>.
     */
    public final TableField<ProductsRecord, ULong> ID = createField(DSL.name("id"), SQLDataType.BIGINTUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>products.barcode</code>.
     */
    public final TableField<ProductsRecord, String> BARCODE = createField(DSL.name("barcode"), SQLDataType.VARCHAR(255).defaultValue(DSL.inline("NULL", SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>products.name</code>.
     */
    public final TableField<ProductsRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).defaultValue(DSL.inline("NULL", SQLDataType.VARCHAR)), this, "");

    private Products(Name alias, Table<ProductsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Products(Name alias, Table<ProductsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>products</code> table reference
     */
    public Products(String alias) {
        this(DSL.name(alias), PRODUCTS);
    }

    /**
     * Create an aliased <code>products</code> table reference
     */
    public Products(Name alias) {
        this(alias, PRODUCTS);
    }

    /**
     * Create a <code>products</code> table reference
     */
    public Products() {
        this(DSL.name("products"), null);
    }

    public <O extends Record> Products(Table<O> child, ForeignKey<O, ProductsRecord> key) {
        super(child, key, PRODUCTS);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<ProductsRecord, ULong> getIdentity() {
        return (Identity<ProductsRecord, ULong>) super.getIdentity();
    }

    @Override
    public UniqueKey<ProductsRecord> getPrimaryKey() {
        return Keys.KEY_PRODUCTS_PRIMARY;
    }

    @Override
    public List<UniqueKey<ProductsRecord>> getKeys() {
        return Arrays.<UniqueKey<ProductsRecord>>asList(Keys.KEY_PRODUCTS_PRIMARY, Keys.KEY_PRODUCTS_BARCODE);
    }

    @Override
    public Products as(String alias) {
        return new Products(DSL.name(alias), this);
    }

    @Override
    public Products as(Name alias) {
        return new Products(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Products rename(String name) {
        return new Products(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Products rename(Name name) {
        return new Products(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<ULong, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
