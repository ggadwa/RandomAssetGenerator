package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.bitmaps.BitmapPillar;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapPillar {

    private boolean squareBase, squareColumn;
    private float radius, baseRadius, capSize;
    private float[] cylinderSegments;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapPillar(MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.meshList = meshList;
        this.bitmaps = bitmaps;

        squareBase = AppWindow.random.nextBoolean();
        squareColumn = AppWindow.random.nextBoolean();
        capSize = MapBuilder.FLOOR_HEIGHT + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT);
        cylinderSegments = MeshMapUtility.createCylinderSegmentList(1, 4);

        baseRadius = (MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.6f))) * 0.5f;
        radius = baseRadius * (0.6f + AppWindow.random.nextFloat(0.1f));
    }

        //
        // pillars
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {
        float pillarTy, ty, pillarBy;
        String name;
        RagPoint centerPnt;
        Mesh mesh;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("pillar")) {
            bitmap = new BitmapPillar();
            bitmap.generate();
            bitmaps.put("pillar", bitmap);
        }

        name = "pillar_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        ty = by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT);
        pillarTy = ty - capSize;
        pillarBy = by + capSize;

        // xz position
        centerPnt = new RagPoint((((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE / 2)), ((ty + by) / 2), ((room.z + z) * MapBuilder.SEGMENT_SIZE + (MapBuilder.SEGMENT_SIZE / 2)));

        // create the pillar
        mesh = MeshMapUtility.createCylinder(room, name, "pillar", (squareColumn ? 4 : 16), centerPnt, pillarTy, pillarBy, cylinderSegments, radius, false, false);

        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", (squareBase ? 4 : 16), centerPnt, pillarBy, by, baseRadius, true, false));
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", (squareBase ? 4 : 16), centerPnt, ty, pillarTy, baseRadius, false, true));

        meshList.add(mesh);
    }
}
