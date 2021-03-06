/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Beacons implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer numItems;
    private final Double  latitude;
    private final Double  longitude;

    public Beacons(Beacons value) {
        this.numItems = value.numItems;
        this.latitude = value.latitude;
        this.longitude = value.longitude;
    }

    public Beacons(
        Integer numItems,
        Double  latitude,
        Double  longitude
    ) {
        this.numItems = numItems;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter for <code>beacons.num_items</code>.
     */
    public Integer getNumItems() {
        return this.numItems;
    }

    /**
     * Getter for <code>beacons.latitude</code>.
     */
    public Double getLatitude() {
        return this.latitude;
    }

    /**
     * Getter for <code>beacons.longitude</code>.
     */
    public Double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Beacons (");

        sb.append(numItems);
        sb.append(", ").append(latitude);
        sb.append(", ").append(longitude);

        sb.append(")");
        return sb.toString();
    }
}
