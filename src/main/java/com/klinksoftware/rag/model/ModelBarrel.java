package com.klinksoftware.rag.model;

import com.klinksoftware.rag.model.utility.ModelInterface;
import com.klinksoftware.rag.model.utility.ModelBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelBarrel extends ModelBase {

    @Override
    public float getCameraRotateY() {
        return (10.0f);
    }

    @Override
    public void buildMeshes() {
        float barrelSectionRadius, barrelRingRadius, barrelRingHeight, barrelSectionHeight;
        float by;
        RagPoint centerPnt;
        Mesh mesh;

        scene.bitmapGroup.add("barrel", new String[]{"Metal"});

        barrelSectionRadius = (MapBase.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        barrelRingRadius = barrelSectionRadius + (MapBase.SEGMENT_SIZE * 0.02f);
        barrelRingHeight = MapBase.SEGMENT_SIZE * (0.02f + AppWindow.random.nextFloat(0.02f));
        barrelSectionHeight = MapBase.SEGMENT_SIZE * (0.15f + AppWindow.random.nextFloat(0.1f));

        // barrel parts
        by = 0;
        centerPnt = new RagPoint(0, (barrelRingHeight * 0.5f), 0);
        mesh = MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, true, true);

        by += barrelRingHeight;
        centerPnt = new RagPoint(0, (by + (barrelSectionHeight * 0.5f)), 0);
        mesh.combine(MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelSectionHeight), barrelSectionRadius, false, false));

        by += barrelSectionHeight;
        centerPnt = new RagPoint(0, (by + (barrelRingHeight * 0.5f)), 0);
        mesh.combine(MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, true, true));

        by += barrelRingHeight;
        centerPnt = new RagPoint(0, (by + (barrelSectionHeight * 0.5f)), 0);
        mesh.combine(MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelSectionHeight), barrelSectionRadius, false, false));

        by += barrelSectionHeight;
        centerPnt = new RagPoint(0, (by + (barrelRingHeight * 0.5f)), 0);
        mesh.combine(MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, true, true));

        scene.rootNode.addMesh(mesh);
    }
}
