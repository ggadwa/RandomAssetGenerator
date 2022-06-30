package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.MeshUtility;

@ModelInterface
public class ModelComputer extends ModelBase {

    @Override
    public float getCameraDistance() {
        return (11.0f);
    }

    @Override
    public void buildInternal() {
        float computerWidth, computerHeight, computerHeight2, wid, midX, midZ;
        float pedestalWidth, pedestalHeight;

        addBitmap("computer", new String[]{"Computer"});
        addBitmap("pedestal", new String[]{"Metal", "Tile", "Wood"});

        // computer size
        computerWidth = MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.3f));
        computerHeight = MapBuilder.SEGMENT_SIZE * (0.7f + AppWindow.random.nextFloat(0.3f));
        computerHeight2 = computerHeight * 0.9f;

        // pedestal a little bigger than equipment
        pedestalWidth = computerWidth + (MapBuilder.SEGMENT_SIZE * 0.05f);
        pedestalHeight = (MapBuilder.FLOOR_HEIGHT * 0.25f) + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT * 0.75f);

        meshList.add(MeshUtility.createCube("pedestal", -pedestalWidth, pedestalWidth, 0, pedestalHeight, -pedestalWidth, pedestalWidth, true, true, true, true, true, true, false, MeshUtility.UV_MAP));

        // computer banks
        switch (AppWindow.random.nextInt(4)) {
            case 0: // one bank
                meshList.add(MeshUtility.createCube("computer", -computerWidth, computerWidth, pedestalHeight, (pedestalHeight + computerHeight), -computerWidth, computerWidth, true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                break;
            case 1:
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                meshList.add(MeshUtility.createCube("computer", (midX - wid), (midX + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), (midZ - wid), (midZ + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", ((-midX) - wid), ((-midX) + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), ((-midZ) - wid), ((-midZ) + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                break;
            case 2:
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                meshList.add(MeshUtility.createCube("computer", (midX - wid), (midX + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), (midZ - wid), (midZ + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", ((-midX) - wid), ((-midX) + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), ((-midZ) - wid), ((-midZ) + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", (midX - wid), (midX + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), ((-midZ) - wid), ((-midZ) + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                break;
            case 3:
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                meshList.add(MeshUtility.createCube("computer", (midX - wid), (midX + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), (midZ - wid), (midZ + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", ((-midX) - wid), ((-midX) + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), ((-midZ) - wid), ((-midZ) + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", (midX - wid), (midX + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), ((-midZ) - wid), ((-midZ) + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                meshList.add(MeshUtility.createCube("computer", ((-midX) - wid), ((-midX) + wid), pedestalHeight, (pedestalHeight + (AppWindow.random.nextBoolean() ? computerHeight : computerHeight2)), (midZ - wid), (midZ + wid), true, true, true, true, true, false, false, MeshUtility.UV_BOX));
                break;
        }

        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
