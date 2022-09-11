package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelStorage extends ModelBase {

    @Override
    public void buildMeshes() {
        int n, x, z, groupCount, stackCount;
        float dx, y, dz, boxSize, drawBoxSize, boxSizeReduction, boxHalfSize, bigHeight;
        RagPoint rotAngle;
        Mesh mesh, mesh2;

        addBitmap("box", new String[]{"Storage"});

        // box size
        boxSize = (MapBuilder.SEGMENT_SIZE * 0.2f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.2f));
        boxSizeReduction = 0.9f + AppWindow.random.nextFloat(0.1f);

        rotAngle = new RagPoint(0.0f, 0.0f, 0.0f);

        // groups of stacks
        groupCount = 1 + AppWindow.random.nextInt(3);

        mesh = null;

        for (z = 0; z != groupCount; z++) {
            for (x = 0; x != groupCount; x++) {

                // the stacks
                stackCount = 1 + AppWindow.random.nextInt(3);

                dx = x * boxSize - (((groupCount * boxSize) / 2) - (boxSize / 2));
                dz = (z * boxSize) - (((groupCount * boxSize) / 2) - (boxSize / 2));
                y = 0.0f;

                // sometime one large box
                if (AppWindow.random.nextFloat() < 0.3f) {
                    boxHalfSize = boxSize * 0.5f;
                    bigHeight = boxSize * (1 + AppWindow.random.nextInt(3));

                    mesh2 = MeshUtility.createCubeRotated("box", (dx - boxHalfSize), (dx + boxHalfSize), y, (y + bigHeight), (dz - boxHalfSize), (dz + boxHalfSize), rotAngle, true, true, true, true, true, true, false, MeshUtility.UV_WHOLE);

                    if (mesh == null) {
                        mesh = mesh2;
                    } else {
                        mesh.combine(mesh2);
                    }

                    continue;
                }

                // a stack of boxes
                drawBoxSize = boxSize;

                for (n = 0; n != stackCount; n++) {
                    boxHalfSize = drawBoxSize * 0.5f;
                    rotAngle.setFromValues(0.0f, (-10.0f + (AppWindow.random.nextFloat() * 20.0f)), 0.0f);
                    mesh2 = MeshUtility.createCubeRotated("box", (dx - boxHalfSize), (dx + boxHalfSize), y, (y + drawBoxSize), (dz - boxHalfSize), (dz + boxHalfSize), rotAngle, true, true, true, true, true, true, false, MeshUtility.UV_WHOLE);

                    if (mesh == null) {
                        mesh = mesh2;
                    } else {
                        mesh.combine(mesh2);
                    }

                    // go up one level
                    y += drawBoxSize;
                    drawBoxSize *= boxSizeReduction;
                }
            }
        }

        scene.rootNode.addMesh(mesh);
    }
}
