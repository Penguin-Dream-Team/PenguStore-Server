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
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import store.pengu.server.db.pengustore.DefaultSchema;
import store.pengu.server.db.pengustore.Keys;
import store.pengu.server.db.pengustore.tables.records.ProductXImageRecord;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ProductXImage extends TableImpl<ProductXImageRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>product_x_image</code>
     */
    public static final ProductXImage PRODUCT_X_IMAGE = new ProductXImage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ProductXImageRecord> getRecordType() {
        return ProductXImageRecord.class;
    }

    /**
     * The column <code>product_x_image.product_id</code>.
     */
    public final TableField<ProductXImageRecord, Long> PRODUCT_ID = createField(DSL.name("product_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>product_x_image.image</code>.
     */
    public final TableField<ProductXImageRecord, byte[]> IMAGE = createField(DSL.name("image"), SQLDataType.BLOB.nullable(false), this, "");

    private ProductXImage(Name alias, Table<ProductXImageRecord> aliased) {
        this(alias, aliased, null);
    }

    private ProductXImage(Name alias, Table<ProductXImageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>product_x_image</code> table reference
     */
    public ProductXImage(String alias) {
        this(DSL.name(alias), PRODUCT_X_IMAGE);
    }

    /**
     * Create an aliased <code>product_x_image</code> table reference
     */
    public ProductXImage(Name alias) {
        this(alias, PRODUCT_X_IMAGE);
    }

    /**
     * Create a <code>product_x_image</code> table reference
     */
    public ProductXImage() {
        this(DSL.name("product_x_image"), null);
    }

    public <O extends Record> ProductXImage(Table<O> child, ForeignKey<O, ProductXImageRecord> key) {
        super(child, key, PRODUCT_X_IMAGE);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<ProductXImageRecord> getPrimaryKey() {
        return Keys.KEY_PRODUCT_X_IMAGE_PRIMARY;
    }

    @Override
    public List<UniqueKey<ProductXImageRecord>> getKeys() {
        return Arrays.<UniqueKey<ProductXImageRecord>>asList(Keys.KEY_PRODUCT_X_IMAGE_PRIMARY);
    }

    @Override
    public ProductXImage as(String alias) {
        return new ProductXImage(DSL.name(alias), this);
    }

    @Override
    public ProductXImage as(Name alias) {
        return new ProductXImage(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ProductXImage rename(String name) {
        return new ProductXImage(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ProductXImage rename(Name name) {
        return new ProductXImage(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Long, byte[]> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
