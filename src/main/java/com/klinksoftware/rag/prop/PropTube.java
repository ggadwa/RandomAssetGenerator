package com.klinksoftware.rag.prop;

import com.klinksoftware.rag.prop.utility.PropBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;
import com.klinksoftware.rag.prop.utility.PropInterface;

@PropInterface
public class PropTube extends PropBase {

    @Override
    public float getCameraRotateY() {
        return (10.0f);
    }

    @Override
    public void buildMeshes() {
        float yBotCapBy, yBotCapTy, yTopCapBy, yTopCapTy, y;
        float tubeCapRadius, tubeRadius, tubeHeight, tubeTopCapHeight, tubeBotCapHeight;
        RagPoint centerPnt;
        Mesh mesh, mesh2;

        scene.bitmapGroup.add("accessory", new String[]{"Metal", "WoodPanel"});
        scene.bitmapGroup.add("glass", new String[]{"Glass"});
        scene.bitmapGroup.add("liquid", new String[]{"Liquid"});

        tubeCapRadius = (MapBase.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        tubeRadius = tubeCapRadius * (0.7f + AppWindow.random.nextFloat(0.2f));
        tubeHeight = (MapBase.SEGMENT_SIZE - MapBase.FLOOR_HEIGHT) * (0.7f + AppWindow.random.nextFloat(0.5f));
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
        scene.rootNode.addMesh(mesh);

        // the tube
        scene.rootNode.addMesh(MeshUtility.createMeshCylinderSimple("glass", 16, centerPnt, yTopCapBy, yBotCapTy, tubeRadius, false, false));

        // the liquid in the tube
        y = yBotCapTy + (AppWindow.random.nextFloat() * (yTopCapBy - yBotCapTy));
        scene.rootNode.addMesh(MeshUtility.createMeshCylinderSimple("liquid", 16, centerPnt, y, yBotCapTy, (tubeRadius * 0.98f), true, false));
    }
}
