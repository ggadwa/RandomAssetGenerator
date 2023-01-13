package com.klinksoftware.rag.map.utility;

public class MapWindow {

    public static final int WINDOW_DIR_Z = 0;
    public static final int WINDOW_DIR_X = 1;

    private float x, z;
    private int direction;

    public MapWindow(float x, float z, int direction) {
        this.x = x;
        this.z = z;
        this.direction = direction;
    }

    public void build(MapRoom room, int roomNumber) {

    }

}
