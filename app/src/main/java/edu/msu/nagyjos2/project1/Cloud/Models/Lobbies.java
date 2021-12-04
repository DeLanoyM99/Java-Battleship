package edu.msu.nagyjos2.project1.Cloud.Models;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "lobbies")
public final class Lobbies {

    @ElementList(name = "lobby", inline = true, required = false, type = LobbyItem.class)
    private List<LobbyItem> items;

    public List<LobbyItem> getItems() {
        return items;
    }

    public void setItems(List<LobbyItem> items) {
        this.items = items;
    }

    public Lobbies(ArrayList<LobbyItem> items) {
        this.items = items;
    }

    public Lobbies() {
    }
}
