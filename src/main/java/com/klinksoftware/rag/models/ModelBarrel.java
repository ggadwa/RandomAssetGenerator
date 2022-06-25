package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.mesh.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelBarrel extends ModelBase {

    @Override
    public float getCameraRotateY() {
        return (10.0f);
    }

    @Override
    public void buildInternal() {
        float barrelSectionRadius, barrelRingRadius, barrelRingHeight, barrelSectionHeight;
        float by;
        RagPoint centerPnt;
        Mesh mesh;

        addBitmap("barrel", new String[]{"Metal"});

        barrelSectionRadius = (MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        barrelRingRadius = barrelSectionRadius + (MapBuilder.SEGMENT_SIZE * 0.02f);
        barrelRingHeight = MapBuilder.SEGMENT_SIZE * (0.02f + AppWindow.random.nextFloat(0.02f));
        barrelSectionHeight = MapBuilder.SEGMENT_SIZE * (0.15f + AppWindow.random.nextFloat(0.1f));

        // barrel parts
        by = 0;
        centerPnt = new RagPoint(0, (barrelRingHeight * 0.5f), 0);
        mesh = MeshUtility.createMeshCylinderSimple("barrel", 16, centerPnt, by, (by + barrelRingHeight), barrelRingRadius, false, true);

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

        meshList.add(mesh);

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
