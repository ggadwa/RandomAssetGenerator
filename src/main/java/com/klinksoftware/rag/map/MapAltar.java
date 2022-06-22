package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.mesh.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MapAltar {
    private MapBuilder mapBuilder;
    private ArrayList<MapRoom> rooms;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapAltar(MapBuilder mapBuilder, ArrayList<MapRoom> rooms, MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.mapBuilder = mapBuilder;
        this.rooms = rooms;
        this.meshList = meshList;
        this.bitmaps = bitmaps;
    }

        //
        // single altar
        //

    public Mesh addAltar(MapRoom room, int roomNumber, int lx, int rx, float by, int tz, int bz) {
        int n, x, z, dx, dz, levelCount;
        float xMin, xMax, zMin, zMax;
        String name;
        Mesh mesh, mesh2;

        levelCount = 1 + AppWindow.random.nextInt(5);

        mesh = null;

        for (n=0;n!=levelCount;n++) {
            xMin = (room.x + lx) * MapBuilder.SEGMENT_SIZE;
            xMax = (room.x + rx) * MapBuilder.SEGMENT_SIZE;
            zMin = (room.z + tz) * MapBuilder.SEGMENT_SIZE;
            zMax = (room.z + bz) * MapBuilder.SEGMENT_SIZE;

            name = "altar_" + Integer.toString(roomNumber) + "_" + Integer.toString(lx) + "x" + Integer.toString(tz);
            mesh2 = MeshUtility.createCube("platform", xMin, xMax, by, (by + MapBuilder.FLOOR_HEIGHT), zMin, zMax, true, true, true, true, true, false, false, MeshUtility.UV_MAP);

            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }

            if (n==0) {
                for (z=tz;z<bz;z++) {
                    for (x=lx;x<rx;x++) {
                        room.setBlockedGrid(x, z);
                    }
                }
            }

            by += MapBuilder.FLOOR_HEIGHT;

            dx=Math.abs(lx-rx);
            dz=Math.abs(tz-bz);
            if ((dx<=1) || (dz<=1)) break;

            if (dx>dz) {
                if (AppWindow.random.nextBoolean()) {
                    lx++;
                }
                else {
                    rx--;
                }
            }
            else {
                if (AppWindow.random.nextBoolean()) {
                    tz++;
                }
                else {
                    bz--;
                }
            }
        }

        return(mesh);
    }

        //
        // altar
        // note "platform" bitmap should always exist
        //

    public void build(MapRoom room, int roomNumber, float by) {
        int mx, mz;
        Mesh mesh;

        // rooms with 10x10 can get half or quarter versions

        if ((room.piece.sizeX >= 10) && (room.piece.sizeZ >= 10)) {

            mx = room.piece.sizeX / 2;
            mz = room.piece.sizeZ / 2;

            switch (AppWindow.random.nextInt(3)) {
                case 0:
                    mesh = addAltar(room, roomNumber, 1, (room.piece.sizeX - 1), by, 1, (room.piece.sizeZ - 1));
                    break;
                case 1:
                    mesh = addAltar(room, roomNumber, 1, mx, by, 1, (room.piece.sizeZ - 1));
                    mesh.combine(addAltar(room, roomNumber, mx, (room.piece.sizeX - 1), by, 1, (room.piece.sizeZ - 1)));
                    break;
                default:
                    mesh = addAltar(room, roomNumber, 1, mx, by, 1, mz);
                    mesh.combine(addAltar(room, roomNumber, 1, mx, by, mz, (room.piece.sizeZ - 1)));
                    mesh.combine(addAltar(room, roomNumber, mx, (room.piece.sizeX - 1), by, 1, mz));
                    mesh.combine(addAltar(room, roomNumber, mx, (room.piece.sizeX - 1), by, mz, (room.piece.sizeZ - 1)));
                    break;
            }
        }
        else {
            mesh = addAltar(room, roomNumber, 1, (room.piece.sizeX - 1), by, 1, (room.piece.sizeZ - 1));
        }

        meshList.add(mesh);
    }

}
