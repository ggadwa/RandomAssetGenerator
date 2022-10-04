package com.klinksoftware.rag.prop;

import com.klinksoftware.rag.prop.utility.PropBase;
import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.utility.MapBase;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.prop.utility.PropInterface;

@PropInterface
public class PropComputerBank extends PropBase {

    @Override
    public float getCameraDistance() {
        return (12.0f);
    }

    private void buildSingleBank(String computerBitmapName, float xMin, float xMax, float zMin, float zMax, boolean flipFront, float pedestalHeight, float computerWidth) {
        float ty, by, x, z, xWid, zWid, xSz, zSz, high, margin;
        boolean hasTop;

        by = pedestalHeight;
        ty = by + (computerWidth * 2);

        // possible bottom
        if (AppWindow.random.nextBoolean()) {
            margin = MapBase.SEGMENT_SIZE * (0.01f + AppWindow.random.nextFloat(0.01f));
            high = MapBase.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.1f));
            scene.rootNode.addMesh(MeshUtility.createCube("spacer", (xMin - margin), (xMax + margin), (by + high), by, (zMin - margin), (zMax + margin), true, true, true, true, false, true, false, MeshUtility.UV_MAP));
            by += high;
            ty += high;
        }

        // possible top
        hasTop = AppWindow.random.nextBoolean();
        if (hasTop) {
            xSz = Math.abs(xMax - xMin);
            xWid = (xSz * 0.1f) + AppWindow.random.nextFloat(xSz * 0.5f);
            x = xMin + AppWindow.random.nextFloat(xSz - xWid);

            zSz = Math.abs(zMax - zMin);
            zWid = (zSz * 0.1f) + AppWindow.random.nextFloat(zSz * 0.5f);
            z = zMin + AppWindow.random.nextFloat(zSz - zWid);

            high = MapBase.SEGMENT_SIZE * (0.025f + AppWindow.random.nextFloat(0.075f));
            scene.rootNode.addMesh(MeshUtility.createCube("spacer", x, (x + xWid), (ty + high), ty, z, (z + zWid), true, true, true, true, false, true, false, MeshUtility.UV_MAP));
        }

        // the computer
        scene.rootNode.addMesh(MeshUtility.createCube(computerBitmapName, xMin, xMax, by, ty, zMin, zMax, false, false, (!flipFront), flipFront, false, false, false, MeshUtility.UV_WHOLE));
        scene.rootNode.addMesh(MeshUtility.createCube("panel", xMin, xMax, ty, by, zMin, zMax, true, true, flipFront, (!flipFront), false, true, false, MeshUtility.UV_WHOLE));
    }

    @Override
    public void buildMeshes() {
        float computerWidth, wid, midX, midZ, margin;
        float pedestalWidth, pedestalHeight;

        scene.bitmapGroup.add("computer", new String[]{"Computer"});
        scene.bitmapGroup.add("panel", new String[]{"Metal", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("spacer", new String[]{"Metal", "MetalPlank", "MetalPlate"});
        scene.bitmapGroup.add("pedestal", new String[]{"Hexagon", "MetalPlank", "MetalPlate", "Mosaic", "WoodPanel"});

        // computer size
        computerWidth = MapBase.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.3f));

        // pedestal a little bigger than equipment
        pedestalWidth = computerWidth + (MapBase.SEGMENT_SIZE * 0.05f);
        pedestalHeight = (MapBase.FLOOR_HEIGHT * 0.1f) + AppWindow.random.nextFloat(MapBase.FLOOR_HEIGHT * 0.5f);

        scene.rootNode.addMesh(MeshUtility.createCube("pedestal", -pedestalWidth, pedestalWidth, 0, pedestalHeight, -pedestalWidth, pedestalWidth, true, true, true, true, true, true, false, MeshUtility.UV_MAP));

        margin = computerWidth * 0.05f;
        wid = computerWidth - (margin * 2.0f);
        midX = computerWidth * 0.5f;
        midZ = computerWidth * 0.5f;

        // computer banks
        switch (AppWindow.random.nextInt(4)) {
            case 0: // one bank
                buildSingleBank("computer", -computerWidth, computerWidth, -computerWidth, computerWidth, true, pedestalHeight, computerWidth);
                break;
            case 1:
                scene.bitmapGroup.add("computer4", new String[]{"Computer"});
                buildSingleBank("computer", -(wid + margin), -margin, -(wid + margin), -margin, false, pedestalHeight, wid);
                //buildSingleBank("computer2", margin, (wid + margin), -(wid + margin), -margin, false, pedestalHeight, wid);
                buildSingleBank("computer4", margin, (wid + margin), margin, (wid + margin), true, pedestalHeight, wid);
                break;
            case 2:
                scene.bitmapGroup.add("computer2", new String[]{"Computer"});
                scene.bitmapGroup.add("computer3", new String[]{"Computer"});
                buildSingleBank("computer", -(wid + margin), -margin, -(wid + margin), -margin, false, pedestalHeight, wid);
                buildSingleBank("computer2", margin, (wid + margin), -(wid + margin), -margin, false, pedestalHeight, wid);
                buildSingleBank("computer3", -(wid + margin), -margin, margin, (wid + margin), true, pedestalHeight, wid);
                break;
            case 3:
                scene.bitmapGroup.add("computer2", new String[]{"Computer"});
                scene.bitmapGroup.add("computer3", new String[]{"Computer"});
                scene.bitmapGroup.add("computer4", new String[]{"Computer"});
                buildSingleBank("computer", -(wid + margin), -margin, -(wid + margin), -margin, false, pedestalHeight, wid);
                buildSingleBank("computer2", margin, (wid + margin), -(wid + margin), -margin, false, pedestalHeight, wid);
                buildSingleBank("computer3", -(wid + margin), -margin, margin, (wid + margin), true, pedestalHeight, wid);
                buildSingleBank("computer4", margin, (wid + margin), margin, (wid + margin), true, pedestalHeight, wid);
                break;
        }
    }
}
