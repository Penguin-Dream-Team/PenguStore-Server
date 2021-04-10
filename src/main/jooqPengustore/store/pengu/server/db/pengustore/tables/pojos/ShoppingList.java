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
public class ShoppingList implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ULong  id;
    private final String name;
    private final Double latitude;
    private final Double longitude;

    public ShoppingList(ShoppingList value) {
        this.id = value.id;
        this.name = value.name;
        this.latitude = value.latitude;
        this.longitude = value.longitude;
    }

    public ShoppingList(
        ULong  id,
        String name,
        Double latitude,
        Double longitude
    ) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter for <code>shopping_list.id</code>.
     */
    public ULong getId() {
        return this.id;
    }

    /**
     * Getter for <code>shopping_list.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for <code>shopping_list.latitude</code>.
     */
    public Double getLatitude() {
        return this.latitude;
    }

    /**
     * Getter for <code>shopping_list.longitude</code>.
     */
    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShoppingList (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(latitude);
        sb.append(", ").append(longitude);

        sb.append(")");
        return sb.toString();
    }
}
