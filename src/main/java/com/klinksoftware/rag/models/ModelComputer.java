package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.MeshUtility;

@ModelInterface
public class ModelComputer extends ModelBase {

    @Override
    public void buildInternal() {
        float computerWidth, computerHeight;

        addBitmap("computer", new String[]{"Computer"});

        computerWidth = MapBuilder.SEGMENT_SIZE * (0.5f + AppWindow.random.nextFloat(0.2f));
        computerHeight = MapBuilder.SEGMENT_SIZE * (0.7f + AppWindow.random.nextFloat(0.3f));

        meshList.add(MeshUtility.createCube("computer", -computerWidth, computerWidth, 0, computerHeight, -computerWidth, computerWidth, true, true, true, true, true, false, false, MeshUtility.UV_BOX));

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
