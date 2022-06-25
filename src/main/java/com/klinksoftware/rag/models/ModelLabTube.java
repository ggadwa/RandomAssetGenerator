package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelLabTube extends ModelBase {

    @Override
    public float getCameraRotateY() {
        return (10.0f);
    }

    @Override
    public void buildInternal() {
        float yBotCapBy, yBotCapTy, yTopCapBy, yTopCapTy, y;
        float tubeCapRadius, tubeRadius, tubeHeight, tubeTopCapHeight, tubeBotCapHeight;
        RagPoint centerPnt;
        Mesh mesh, mesh2;

        addBitmap("accessory", new String[]{"Metal", "Tile", "Wood"});
        addBitmap("glass", new String[]{"Glass"});
        addBitmap("liquid", new String[]{"Liquid"});

        tubeCapRadius = (MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        tubeRadius = tubeCapRadius * (0.7f + AppWindow.random.nextFloat(0.2f));
        tubeHeight = (MapBuilder.SEGMENT_SIZE - MapBuilder.FLOOR_HEIGHT) * (0.7f + AppWindow.random.nextFloat(0.5f));
        tubeTopCapHeight = tubeHeight * (0.05f + AppWindow.random.nextFloat(0.2f));
        tubeBotCapHeight = tubeHeight * (0.05f + AppWindow.random.nextFloat(0.2f));

        // tube center
        centerPnt = new RagPoint(0, (tubeHeight * 0.5f), 0);

        // the top and bottom caps
        yBotCapBy = 0;
        yBotCapTy = yBotCapBy + tubeBotCapHeight;

        yTopCapBy = yBotCapTy + (tubeHeight - (tubeBotCapHeight + tubeTopCapHeight));
        yTopCapTy = yTopCapBy + tubeTopCapHeight;

        mesh = MeshUtility.createMeshCylinderSimple("accessory", 16, centerPnt, yBotCapTy, yBotCapBy, tubeCapRadius, true, true);
        mesh2 = MeshUtility.createMeshCylinderSimple("accessory", 16, centerPnt, yTopCapTy, yTopCapBy, tubeCapRadius, true, true);
        mesh.combine(mesh2);
        meshList.add(mesh);

        // the tube
        meshList.add(MeshUtility.createMeshCylinderSimple("glass", 16, centerPnt, yTopCapBy, yBotCapTy, tubeRadius, false, false));

        // the liquid in the tube
        y = yBotCapTy + (AppWindow.random.nextFloat() * (yTopCapBy - yBotCapTy));
        meshList.add(MeshUtility.createMeshCylinderSimple("liquid", 16, centerPnt, y, yBotCapTy, (tubeRadius * 0.98f), true, false));

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
