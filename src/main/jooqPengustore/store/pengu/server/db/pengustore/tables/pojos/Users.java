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
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ULong  userid;
    private final String username;

    public Users(Users value) {
        this.userid = value.userid;
        this.username = value.username;
    }

    public Users(
        ULong  userid,
        String username
    ) {
        this.userid = userid;
        this.username = username;
    }

    /**
     * Getter for <code>users.userid</code>.
     */
    public ULong getUserid() {
        return this.userid;
    }

    /**
     * Getter for <code>users.username</code>.
     */
    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Users (");

        sb.append(userid);
        sb.append(", ").append(username);

        sb.append(")");
        return sb.toString();
    }
}
