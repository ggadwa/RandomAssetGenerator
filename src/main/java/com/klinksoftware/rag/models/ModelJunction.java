package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.scene.Mesh;
import com.klinksoftware.rag.utility.MeshUtility;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelJunction extends ModelBase {

    @Override
    public void buildMeshes() {
        float junctionWidth, pipeHeight, pipeRadius, junctionHalfDepth;
        float dx, dz, juncHalfWid;
        String name;
        RagPoint rotAngle, centerPnt, pipePnt;
        Mesh mesh, mesh2;

        addBitmap("computer", new String[]{"Computer"});
        addBitmap("panel", new String[]{"Metal", "MetalPlank", "MetalPlate"});
        addBitmap("pipe", new String[]{"Pipe"});

        // sizes
        junctionWidth = MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f));
        pipeHeight = (MapBuilder.SEGMENT_SIZE * 0.2f) + AppWindow.random.nextFloat(MapBuilder.SEGMENT_SIZE * 0.2f);
        pipeRadius = ((MapBuilder.SEGMENT_SIZE * 0.05f) + AppWindow.random.nextFloat((MapBuilder.SEGMENT_SIZE * 0.1f))) * 0.5f;
        junctionHalfDepth = pipeRadius * 1.1f;

        // the junction

        juncHalfWid = junctionWidth * 0.5f;

        scene.rootNode.addMesh(MeshUtility.createCube("computer", -juncHalfWid, juncHalfWid, pipeHeight, (pipeHeight + junctionWidth), -junctionHalfDepth, junctionHalfDepth, false, false, false, true, false, false, false, MeshUtility.UV_WHOLE));
        scene.rootNode.addMesh(MeshUtility.createCube("panel", -juncHalfWid, juncHalfWid, pipeHeight, (pipeHeight + junctionWidth), -junctionHalfDepth, junctionHalfDepth, true, true, true, false, true, true, false, MeshUtility.UV_MAP));
        /*
            // the pipes

        mesh = null;
        centerPnt = new RagPoint(dx, by, dz);

        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(((dx - juncHalfWid) + pipeRadius), by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_upper_neg_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }
        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(dx, by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_upper_center_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }
        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(((dx + juncHalfWid) - pipeRadius), by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_upper_pos_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }

        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(((dx - juncHalfWid) + pipeRadius), by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_lower_neg_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }
        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(dx, by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_lower_center_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }
        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(((dx + juncHalfWid) - pipeRadius), by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_lower_pos_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshUtility.createMeshCylinderSimple("pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }

        if (mesh != null) {
            scene.rootNode.addMesh(mesh);
        }

         */
    }
}
