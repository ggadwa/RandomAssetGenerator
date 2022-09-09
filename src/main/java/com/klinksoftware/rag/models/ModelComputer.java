package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.utility.MeshUtility;

@ModelInterface
public class ModelComputer extends ModelBase {

    @Override
    public float getCameraDistance() {
        return (12.0f);
    }

    private void buildSingleBank(String computerBitmapName, float xMin, float xMax, float zMin, float zMax, boolean flipFront, float pedestalHeight, float computerWidth) {
        float ty, by, high;
        boolean hasTop;

        by = pedestalHeight;
        ty = by + (computerWidth * 2);

        // possible bottom
        if (AppWindow.random.nextBoolean()) {
            high = MapBuilder.SEGMENT_SIZE * (0.05f + AppWindow.random.nextFloat(0.05f));
            scene.rootNode.meshes.add(MeshUtility.createCube("spacer", xMin, xMax, (by + high), by, zMin, zMax, true, true, true, true, false, false, false, MeshUtility.UV_MAP));
            by += high;
            ty += high;
        }

        // possible top
        hasTop = AppWindow.random.nextBoolean();
        if (hasTop) {
            high = MapBuilder.SEGMENT_SIZE * (0.05f + AppWindow.random.nextFloat(0.05f));
            scene.rootNode.meshes.add(MeshUtility.createCube("spacer", xMin, xMax, (ty + high), ty, zMin, zMax, true, true, true, true, false, true, false, MeshUtility.UV_MAP));
        }

        // the computer
        scene.rootNode.meshes.add(MeshUtility.createCube(computerBitmapName, xMin, xMax, by, ty, zMin, zMax, false, false, (!flipFront), flipFront, false, false, false, MeshUtility.UV_WHOLE));
        scene.rootNode.meshes.add(MeshUtility.createCube("panel", xMin, xMax, ty, by, zMin, zMax, true, true, flipFront, (!flipFront), false, (!hasTop), false, MeshUtility.UV_WHOLE));
    }

    @Override
    public void buildMeshes() {
        float computerWidth, wid, midX, midZ;
        float pedestalWidth, pedestalHeight;

        addBitmap("computer", new String[]{"Computer"});
        addBitmap("panel", new String[]{"Metal", "MetalPlank", "MetalPlate"});
        addBitmap("spacer", new String[]{"Metal", "MetalPlank", "MetalPlate"});
        addBitmap("pedestal", new String[]{"Hexagon", "MetalPlank", "MetalPlate", "Mosaic", "WoodPanel"});

        // computer size
        computerWidth = MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.3f));

        // pedestal a little bigger than equipment
        pedestalWidth = computerWidth + (MapBuilder.SEGMENT_SIZE * 0.05f);
        pedestalHeight = (MapBuilder.FLOOR_HEIGHT * 0.25f) + AppWindow.random.nextFloat(MapBuilder.FLOOR_HEIGHT * 0.75f);

        scene.rootNode.meshes.add(MeshUtility.createCube("pedestal", -pedestalWidth, pedestalWidth, 0, pedestalHeight, -pedestalWidth, pedestalWidth, true, true, true, true, true, true, false, MeshUtility.UV_MAP));

        // computer banks
        switch (AppWindow.random.nextInt(4)) {
            case 0: // one bank
                buildSingleBank("computer", -computerWidth, computerWidth, -computerWidth, computerWidth, true, pedestalHeight, computerWidth);
                break;
            case 1:
                addBitmap("computer2", new String[]{"Computer"});
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                buildSingleBank("computer", (midX - wid), (midX + wid), (midZ - wid), (midZ + wid), true, pedestalHeight, wid);
                buildSingleBank("computer2", ((-midX) - wid), ((-midX) + wid), ((-midZ) - wid), ((-midZ) + wid), false, pedestalHeight, wid);
                break;
            case 2:
                addBitmap("computer2", new String[]{"Computer"});
                addBitmap("computer3", new String[]{"Computer"});
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                buildSingleBank("computer", (midX - wid), (midX + wid), (midZ - wid), (midZ + wid), true, pedestalHeight, wid);
                buildSingleBank("computer2", ((-midX) - wid), ((-midX) + wid), ((-midZ) - wid), ((-midZ) + wid), false, pedestalHeight, wid);
                buildSingleBank("computer3", (midX - wid), (midX + wid), ((-midZ) - wid), ((-midZ) + wid), false, pedestalHeight, wid);
                break;
            case 3:
                addBitmap("computer2", new String[]{"Computer"});
                addBitmap("computer3", new String[]{"Computer"});
                addBitmap("computer4", new String[]{"Computer"});
                wid = computerWidth / 2.05f;
                midX = computerWidth / 2.0f;
                midZ = computerWidth / 2.0f;
                buildSingleBank("computer", (midX - wid), (midX + wid), (midZ - wid), (midZ + wid), true, pedestalHeight, wid);
                buildSingleBank("computer2", ((-midX) - wid), ((-midX) + wid), ((-midZ) - wid), ((-midZ) + wid), false, pedestalHeight, wid);
                buildSingleBank("computer3", (midX - wid), (midX + wid), ((-midZ) - wid), ((-midZ) + wid), false, pedestalHeight, wid);
                buildSingleBank("computer4", ((-midX) - wid), ((-midX) + wid), (midZ - wid), (midZ + wid), true, pedestalHeight, wid);
                break;
        }
    }
}
