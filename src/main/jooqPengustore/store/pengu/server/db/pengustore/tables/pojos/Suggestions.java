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
public class Suggestions implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ULong   shoppingListId;
    private final String  rowNumber;
    private final String  colNumber;
    private final Integer cellVal;

    public Suggestions(Suggestions value) {
        this.shoppingListId = value.shoppingListId;
        this.rowNumber = value.rowNumber;
        this.colNumber = value.colNumber;
        this.cellVal = value.cellVal;
    }

    public Suggestions(
        ULong   shoppingListId,
        String  rowNumber,
        String  colNumber,
        Integer cellVal
    ) {
        this.shoppingListId = shoppingListId;
        this.rowNumber = rowNumber;
        this.colNumber = colNumber;
        this.cellVal = cellVal;
    }

    /**
     * Getter for <code>suggestions.shopping_list_id</code>.
     */
    public ULong getShoppingListId() {
        return this.shoppingListId;
    }

    /**
     * Getter for <code>suggestions.row_number</code>.
     */
    public String getRowNumber() {
        return this.rowNumber;
    }

    /**
     * Getter for <code>suggestions.col_number</code>.
     */
    public String getColNumber() {
        return this.colNumber;
    }

    /**
     * Getter for <code>suggestions.cell_val</code>.
     */
    public Integer getCellVal() {
        return this.cellVal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Suggestions (");

        sb.append(shoppingListId);
        sb.append(", ").append(rowNumber);
        sb.append(", ").append(colNumber);
        sb.append(", ").append(cellVal);

        sb.append(")");
        return sb.toString();
    }
}
