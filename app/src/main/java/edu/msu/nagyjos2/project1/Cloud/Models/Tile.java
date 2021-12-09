package edu.msu.nagyjos2.project1.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "tile")
public class Tile {
    @Attribute
    private String pos;

    @Attribute
    private String boat;

    @Attribute
    private String hit;

    public int getPosToInt() { return Integer.parseInt(pos); }

    public boolean getBoatAsBool() { return Boolean.parseBoolean(boat); }

    public boolean getHitAsBool() { return Boolean.parseBoolean(hit); }

    public Tile() {}
}
