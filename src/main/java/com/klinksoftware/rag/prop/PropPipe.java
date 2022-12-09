package com.klinksoftware.rag.prop;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.prop.utility.PropBase;
import com.klinksoftware.rag.prop.utility.PropInterface;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@PropInterface
public class PropPipe extends PropBase {

    private static final int PIPE_SIDE_COUNT = 24;
    private static final int PIPE_CURVE_COUNT = 8;

    private Mesh addSinglePipeSegment(float pipeRadius, float pipeLength, float endRadius, float endLength, boolean skipEnd) {
        Mesh mesh, mesh2;
        RagPoint pnt;

        pnt = new RagPoint(0.0f, 0.0f, 0.0f);

        mesh = MeshUtility.createMeshCylinderSimple("pipe", PIPE_SIDE_COUNT, pnt, 0.0f, pipeLength, pipeRadius, false, false);
        mesh.transformUVs(0.0f, 0.0f, 1.0f, (pipeLength / (pipeRadius * 2.0f)));
        mesh2 = MeshUtility.createMeshCylinderSimple("pipe", PIPE_SIDE_COUNT, pnt, -endLength, 0.0f, endRadius, true, true);
        mesh.combine(mesh2);
        if (!skipEnd) {
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", PIPE_SIDE_COUNT, pnt, pipeLength, (pipeLength + endLength), endRadius, true, true);
            mesh.combine(mesh2);
        }

        return (mesh);
    }

    @Override
    public void buildMeshes() {
        float pipeRadius, endRadius;
        float pipeLength, endLength;
        Mesh mesh, mesh2;

        scene.bitmapGroup.add("pipe", new String[]{"BitmapPipe"});

        pipeRadius = 0.5f + AppWindow.random.nextFloat(0.5f);
        pipeLength = 2.0f + AppWindow.random.nextFloat(3.0f);
        endRadius = pipeRadius + (0.05f + AppWindow.random.nextFloat(0.15f));
        endLength = 0.1f + AppWindow.random.nextFloat(0.5f);
        RagPoint pnt = new RagPoint(0.0f, 0.0f, 0.0f);

        // straight pipe
        mesh = addSinglePipeSegment(pipeRadius, pipeLength, endRadius, endLength, false);
        scene.rootNode.addMesh(mesh);

        // curve
        pnt = new RagPoint(0.0f, (pipeLength + endLength), 0.0f);
        mesh = MeshUtility.createMeshCylinderCorner("pipe", PIPE_SIDE_COUNT, PIPE_CURVE_COUNT, pnt, pipeRadius);
        scene.rootNode.addMesh(mesh);

        // t-section
        mesh = addSinglePipeSegment(pipeRadius, (pipeRadius * 2.0f), endRadius, endLength, true);

        mesh2 = addSinglePipeSegment(pipeRadius, (pipeRadius * 2.0f), endRadius, endLength, false);
        mesh.rotate(new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 90.0f));
        mesh.move(new RagPoint((pipeRadius * 2.0f), pipeRadius, 0.0f));
        mesh.combine(mesh2);

        mesh.rotate(new RagPoint(0.0f, 0.0f, 0.0f), new RagPoint(0.0f, 0.0f, 270.0f));
        pnt = new RagPoint(((pipeRadius * 1.0f) + endLength), ((pipeLength + endLength) + (pipeRadius * 1.3f)), 0.0f);
        mesh.move(pnt);
        scene.rootNode.addMesh(mesh);



        pnt = new RagPoint(0.0f, 0.0f, 0.0f);
        mesh = MeshUtility.createMeshCylinderSimple("pipe", PIPE_SIDE_COUNT, pnt, 0.0f, pipeLength, pipeRadius, true, true);
        mesh.rotate(pnt, new RagPoint(0.0f, 0.0f, 90.0f));
        pnt = new RagPoint(0.0f, (pipeLength + pipeRadius), 0.0f);
        mesh.move(pnt);
        //scene.rootNode.addMesh(mesh);


        pnt = new RagPoint(0.0f, 0.0f, 0.0f);
        mesh = MeshUtility.createMeshCylinderSimple("pipe", PIPE_SIDE_COUNT, pnt, 0.0f, pipeLength, pipeRadius, true, true);
        mesh.rotate(pnt, new RagPoint(0.0f, 0.0f, 90.0f));
        pnt = new RagPoint(pipeLength, (pipeLength * 0.5f), 0.0f);
        mesh.move(pnt);
        //scene.rootNode.addMesh(mesh);

        /*

        addPipeStraightChunk(0, pnt, 5.0f, radius, new RagPoint(0.0f, 0.0f, 0.0f));
        pnt.y += 5.0f;
        addPipeStraightChunk(0, pnt, 1.0f, radius + 0.1f, new RagPoint(0.0f, 0.0f, 0.0f));
*/
    }
}
