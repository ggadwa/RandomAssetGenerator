package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapPillar {

    private boolean squareBase;
    private float[] cylinderSegments;
    private MeshList meshList;

    public MapPillar(MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.meshList = meshList;

        buildBitmap(bitmaps);

        squareBase = AppWindow.random.nextBoolean();
        cylinderSegments = MeshMapUtility.createCylinderSegmentList(1, 5);
    }

    public void buildBitmap(HashMap<String, BitmapBase> bitmaps) {
        String[] pillarBitmaps = {"Brick", "Concrete", "Metal", "Mosaic", "Plaster", "Stone", "Tile"};

        BitmapBase bitmap;

        try {
            bitmap = (BitmapBase) (Class.forName("com.klinksoftware.rag.bitmaps.Bitmap" + pillarBitmaps[AppWindow.random.nextInt(pillarBitmaps.length)].replace(" ", ""))).getConstructor().newInstance();
            bitmap.generate();
            bitmaps.put("pillar", bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        //
        // pillars
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {
        float pillarTy, ty, pillarBy, radius, baseRadius;
        String name;
        RagPoint centerPnt;
        Mesh mesh;

        name = "pillar_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        ty = by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT);
        pillarTy = ty - MapBuilder.FLOOR_HEIGHT;
        pillarBy = by + MapBuilder.FLOOR_HEIGHT;

        // xz position
        centerPnt = new RagPoint((((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE / 2)), ((ty + by) / 2), ((room.z + z) * MapBuilder.SEGMENT_SIZE + (MapBuilder.SEGMENT_SIZE / 2)));

        radius = MapBuilder.SEGMENT_SIZE / 4.0f;
        baseRadius = radius * 1.5f;

        // create the pillar
        mesh = MeshMapUtility.createCylinder(room, name, "pillar", centerPnt, pillarTy, pillarBy, cylinderSegments, radius, false, false);

        if (squareBase) {
            mesh.combine(MeshMapUtility.createCube(room, name, "pillar", (centerPnt.x - baseRadius), (centerPnt.x + baseRadius), pillarBy, by, (centerPnt.z - baseRadius), (centerPnt.z + baseRadius), true, true, true, true, false, true, false, MeshMapUtility.UV_MAP));
            mesh.combine(MeshMapUtility.createCube(room, name, "pillar", (centerPnt.x - baseRadius), (centerPnt.x + baseRadius), ty, pillarTy, (centerPnt.z - baseRadius), (centerPnt.z + baseRadius), true, true, true, true, true, false, false, MeshMapUtility.UV_MAP));
        } else {
            mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", centerPnt, pillarBy, by, baseRadius, true, false));
            mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", centerPnt, ty, pillarTy, baseRadius, false, true));
        }

        meshList.add(mesh);
    }
}
