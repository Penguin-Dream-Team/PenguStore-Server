/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Stats implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer numItems;
    private final Integer time;
    private final Double  latitude;
    private final Double  longitude;

    public Stats(Stats value) {
        this.numItems = value.numItems;
        this.time = value.time;
        this.latitude = value.latitude;
        this.longitude = value.longitude;
    }

    public Stats(
        Integer numItems,
        Integer time,
        Double  latitude,
        Double  longitude
    ) {
        this.numItems = numItems;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter for <code>stats.num_items</code>.
     */
    public Integer getNumItems() {
        return this.numItems;
    }

    /**
     * Getter for <code>stats.time</code>.
     */
    public Integer getTime() {
        return this.time;
    }

    /**
     * Getter for <code>stats.latitude</code>.
     */
    public Double getLatitude() {
        return this.latitude;
    }

    /**
     * Getter for <code>stats.longitude</code>.
     */
    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stats (");

        sb.append(numItems);
        sb.append(", ").append(time);
        sb.append(", ").append(latitude);
        sb.append(", ").append(longitude);

        sb.append(")");
        return sb.toString();
    }
}
