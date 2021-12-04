package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Nested class to store one catalog row
 */
@Root(name = "lobby")
public final class LobbyItem {
    @Attribute
    private String hostid;

    @Attribute
    private String name;

    public String getId() {
        return hostid;
    }

    public void setId(String id) {
        this.hostid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LobbyItem(String id, String name) {
        this.name = name;
        this.hostid = id;
    }

    public LobbyItem() {}

}
