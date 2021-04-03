/*
 * This file is generated by jOOQ.
 */
package store.pengu.server.db.pengustore.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ShoppingList implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long   shopId;
    private final Long   userId;
    private final String name;

    public ShoppingList(ShoppingList value) {
        this.shopId = value.shopId;
        this.userId = value.userId;
        this.name = value.name;
    }

    public ShoppingList(
        Long   shopId,
        Long   userId,
        String name
    ) {
        this.shopId = shopId;
        this.userId = userId;
        this.name = name;
    }

    /**
     * Getter for <code>shopping_list.shop_id</code>.
     */
    public Long getShopId() {
        return this.shopId;
    }

    /**
     * Getter for <code>shopping_list.user_id</code>.
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Getter for <code>shopping_list.name</code>.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShoppingList (");

        sb.append(shopId);
        sb.append(", ").append(userId);
        sb.append(", ").append(name);

        sb.append(")");
        return sb.toString();
    }
}
