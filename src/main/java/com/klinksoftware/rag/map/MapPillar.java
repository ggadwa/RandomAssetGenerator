package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapPillar {

    private static final int[] COLUMN_SIDE_COUNTS = {4, 8, 16};

    private int columnSideCount, baseTopSideCount, baseBotSideCount;
    private float radius, baseRadius, baseTopHeight, baseBotHeight;
    private float[] cylinderSegments;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapPillar(MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.meshList = meshList;
        this.bitmaps = bitmaps;

        columnSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseTopSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseBotSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseTopHeight = MapBuilder.FLOOR_HEIGHT + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT * 2);
        baseBotHeight = MapBuilder.FLOOR_HEIGHT + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT * 2);
        cylinderSegments = MeshMapUtility.createCylinderSegmentList(1, 4);

        baseRadius = (MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.6f))) * 0.5f;
        radius = baseRadius * (0.7f + AppWindow.random.nextFloat(0.1f));
    }

        //
        // pillars
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {
        float pillarTy, ty, pillarBy;
        String name;
        RagPoint centerPnt;
        Mesh mesh;

        BitmapBase.mapBitmapLoader(bitmaps, "pillar", new String[]{"Brick", "Metal", "Mosaic", "Temple", "Tile"});

        name = "pillar_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        ty = by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT);
        pillarTy = ty - baseTopHeight;
        pillarBy = by + baseBotHeight;

        // xz position
        centerPnt = new RagPoint((((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE / 2)), ((ty + by) / 2), ((room.z + z) * MapBuilder.SEGMENT_SIZE + (MapBuilder.SEGMENT_SIZE / 2)));

        // create the pillar
        mesh = MeshMapUtility.createCylinder(room, name, "pillar", columnSideCount, centerPnt, pillarTy, pillarBy, cylinderSegments, radius, false, false);

        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", baseTopSideCount, centerPnt, pillarBy, by, baseRadius, true, false));
        mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", baseBotSideCount, centerPnt, ty, pillarTy, baseRadius, false, true));

        meshList.add(mesh);
    }
}
