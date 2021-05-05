/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.pojos;


import java.io.Serializable;

import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Ratings implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ULong   id;
    private final String  barcode;
    private final Integer rating;

    public Ratings(Ratings value) {
        this.id = value.id;
        this.barcode = value.barcode;
        this.rating = value.rating;
    }

    public Ratings(
        ULong   id,
        String  barcode,
        Integer rating
    ) {
        this.id = id;
        this.barcode = barcode;
        this.rating = rating;
    }

    /**
     * Getter for <code>ratings.id</code>.
     */
    public ULong getId() {
        return this.id;
    }

    /**
     * Getter for <code>ratings.barcode</code>.
     */
    public String getBarcode() {
        return this.barcode;
    }

    /**
     * Getter for <code>ratings.rating</code>.
     */
    public Integer getRating() {
        return this.rating;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ratings (");

        sb.append(id);
        sb.append(", ").append(barcode);
        sb.append(", ").append(rating);

        sb.append(")");
        return sb.toString();
    }
}