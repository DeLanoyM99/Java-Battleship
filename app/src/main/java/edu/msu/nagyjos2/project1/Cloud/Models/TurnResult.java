package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "board") // root xml node
public class TurnResult {
    @Attribute
    private String status;

    @Attribute
    private String surrender;

    @ElementList(name = "tile", inline = true, required = false, type = Tile.class)
    private List<Tile> tiles;

    public String getStatus() { return status; }

    public String getSurrender() { return surrender; }

public List<Tile> getTiles() { return tiles; }

    public TurnResult(String status, String surrender, ArrayList<Tile> tiles) {
        this.status = status;
        this.surrender = surrender;
        this.tiles = tiles;
    }

    public TurnResult() {}
}
