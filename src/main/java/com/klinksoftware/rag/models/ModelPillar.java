package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelPillar extends ModelBase {

    private static final int[] COLUMN_SIDE_COUNTS = {4, 8, 16};

    @Override
    public float getCameraRotateY() {
        return (10.0f);
    }

    @Override
    public float getCameraDistance() {
        return (10.0f);
    }

    @Override
    public void buildMeshes() {
        int n, columnSideCount, baseTopSideCount, baseBotSideCount, topBaseCount, botBaseCount;
        float radius, baseRadius, topRadius, botRadius, baseTopHeight, baseBotHeight, topBaseGrow, botBaseGrow;
        float pillarTy, pillarBy;
        float[] cylinderSegments;
        RagPoint centerPnt;
        Mesh mesh, mesh2;

        addBitmap("pillar", new String[]{"BrickPattern", "BrickRow", "Metal", "Mosaic", "Temple", "Tile"});
        addBitmap("cap", new String[]{"BrickPattern", "BrickRow", "Metal", "Mosaic", "Temple", "Tile"});

        columnSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseTopSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseBotSideCount = COLUMN_SIDE_COUNTS[AppWindow.random.nextInt(3)];
        baseTopHeight = (MapBuilder.FLOOR_HEIGHT * 0.2f) + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT);
        baseBotHeight = (MapBuilder.FLOOR_HEIGHT * 0.2f) + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT);
        cylinderSegments = MeshUtility.createCylinderSegmentList(1, 5);

        topBaseCount = 1 + AppWindow.random.nextInt(3);
        topBaseGrow = 0.8f + AppWindow.random.nextFloat(0.1f);
        botBaseCount = 1 + AppWindow.random.nextInt(3);
        botBaseGrow = 0.8f + AppWindow.random.nextFloat(0.1f);

        baseRadius = (MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.6f))) * 0.5f;

        // pillar pieces
        pillarTy = MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT;
        pillarBy = baseBotHeight;

        // xz position
        centerPnt = new RagPoint(0.0f, (pillarTy / 2), 0.0f);

        // top
        mesh = null;
        topRadius = baseRadius;

        for (n = 0; n != topBaseCount; n++) {
            mesh2 = MeshUtility.createMeshCylinderSimple("cap", baseTopSideCount, centerPnt, (pillarTy - baseTopHeight), pillarTy, topRadius, true, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }

            pillarTy -= baseTopHeight;
            topRadius *= topBaseGrow;
        }

        // bottom
        botRadius = baseRadius;

        for (n = 0; n != botBaseCount; n++) {
            mesh.combine(MeshUtility.createMeshCylinderSimple("cap", baseBotSideCount, centerPnt, pillarBy, (pillarBy + baseBotHeight), botRadius, false, true));

            pillarBy += baseBotHeight;
            botRadius *= botBaseGrow;
        }

        scene.rootNode.addMesh(mesh);

        // middle
        radius = Math.min(topRadius, botRadius) * (0.7f + AppWindow.random.nextFloat(0.1f));

        mesh = MeshUtility.createCylinder("pillar", columnSideCount, centerPnt, pillarTy, pillarBy, cylinderSegments, radius, false, false);
        scene.rootNode.addMesh(mesh);
    }
}
