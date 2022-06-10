package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.bitmaps.BitmapComputer;
import com.klinksoftware.rag.bitmaps.BitmapControlPanel;
import com.klinksoftware.rag.bitmaps.BitmapGlass;
import com.klinksoftware.rag.bitmaps.BitmapLiquid;
import com.klinksoftware.rag.bitmaps.BitmapMetal;
import com.klinksoftware.rag.bitmaps.BitmapMonitor;
import com.klinksoftware.rag.bitmaps.BitmapPipe;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapEquipment {

    private float computerWidth, computerHeight;
    private float terminalWidth, terminalHeight;
    private float junctionWidth, pipeHeight, pipeRadius, junctionHalfDepth;
    private float tubeRadius, tubeHeight, tubeCapRadius, tubeTopCapHeight, tubeBotCapHeight;
    private MeshList meshList;
    private HashMap<String, BitmapBase> bitmaps;

    public MapEquipment(MeshList meshList, HashMap<String, BitmapBase> bitmaps) {
        this.meshList = meshList;
        this.bitmaps = bitmaps;

        computerWidth = MapBuilder.SEGMENT_SIZE * (0.6f + AppWindow.random.nextFloat(0.2f));
        computerHeight = (MapBuilder.SEGMENT_SIZE - MapBuilder.FLOOR_HEIGHT) * (0.7f + AppWindow.random.nextFloat(0.3f));

        terminalWidth = MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f));
        terminalHeight = (MapBuilder.SEGMENT_SIZE - MapBuilder.FLOOR_HEIGHT) * (0.3f + AppWindow.random.nextFloat(0.2f));

        junctionWidth = MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f));
        pipeHeight = (MapBuilder.SEGMENT_SIZE * 0.2f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.2f));
        pipeRadius = ((MapBuilder.SEGMENT_SIZE * 0.05f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.1f))) * 0.5f;
        junctionHalfDepth = pipeRadius * 1.1f;

        tubeCapRadius = (MapBuilder.SEGMENT_SIZE * (0.4f + AppWindow.random.nextFloat(0.2f))) * 0.5f;
        tubeRadius = tubeCapRadius * (0.7f + AppWindow.random.nextFloat(0.2f));
        tubeHeight = (MapBuilder.SEGMENT_SIZE - MapBuilder.FLOOR_HEIGHT) * (0.7f + AppWindow.random.nextFloat(0.3f));
        tubeTopCapHeight = tubeHeight * (0.15f + AppWindow.random.nextFloat(0.2f));
        tubeBotCapHeight = tubeHeight * (0.15f + AppWindow.random.nextFloat(0.2f));
    }

        //
        // pedestals
        //

    private void addPedestal(MapRoom room, int roomNumber, int x, float by, int z, float width) {
        float dx, dz, widOffset;
        String name;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("accessory")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("accessory", bitmap);
        }

        // pedestal a little bigger than equipment
        width += (MapBuilder.SEGMENT_SIZE * 0.05f);
        if (width > MapBuilder.SEGMENT_SIZE) {
            width = MapBuilder.SEGMENT_SIZE;
        }

        widOffset = (MapBuilder.SEGMENT_SIZE - width) * 0.5f;

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + widOffset;
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + widOffset;

        name = "pedestal_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        meshList.add(MeshMapUtility.createCube(room, name, "accessory", dx, (dx + width), by, (by + MapBuilder.FLOOR_HEIGHT), dz, (dz + width), true, true, true, true, true, false, false, MeshMapUtility.UV_MAP));
    }

        //
        // computer banks
        //

    private void addBank(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dy, dz, widOffset;
        String name;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("computer")) {
            bitmap = new BitmapComputer();
            bitmap.generate();
            bitmaps.put("computer", bitmap);
        }

        widOffset = (MapBuilder.SEGMENT_SIZE - computerWidth) * 0.5f;

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + widOffset;
        dy = by + MapBuilder.FLOOR_HEIGHT;
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + widOffset;

        addPedestal(room, roomNumber, x, by, z, computerWidth);

        name = "computer_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        meshList.add(MeshMapUtility.createCube(room, name, "computer", dx, (dx + computerWidth), dy, (dy + computerHeight), dz, (dz + computerWidth), true, true, true, true, true, false, false, MeshMapUtility.UV_BOX));
    }

        //
        // terminals
        //

    public void addTerminal(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz, widOffset;
        float deskHalfWid, deskShortHalfWid, standWid, standHalfWid, standHigh;
        String name;
        RagPoint rotAngle;
        Mesh mesh;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("accessory")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("accessory", bitmap);
        }
        if (!bitmaps.containsKey("monitor")) {
            bitmap = new BitmapMonitor();
            bitmap.generate();
            bitmaps.put("monitor", bitmap);
        }

            // the desk and stand

        widOffset = (MapBuilder.SEGMENT_SIZE - terminalWidth) * 0.5f;

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        deskHalfWid = terminalWidth * 0.5f;
        deskShortHalfWid=deskHalfWid*0.9f;

        standWid = terminalWidth * 0.05f;
        standHalfWid=standWid*0.5f;
        standHigh = terminalHeight * 0.1f;

        rotAngle = new RagPoint(0.0f, (AppWindow.random.nextBoolean() ? 0.0f : 90.0f), 0.0f);
        name = "monitor_stand_bottom_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        mesh = MeshMapUtility.createCube(room, name, "accessory", (dx - deskHalfWid), (dx + deskHalfWid), by, (by + terminalHeight), (dz - deskShortHalfWid), (dz + deskShortHalfWid), true, true, true, true, true, false, false, MeshMapUtility.UV_MAP);

        by += terminalHeight;

        rotAngle.setFromValues(0.0f, (AppWindow.random.nextFloat() * 360.0f), 0.0f);
        name = "monitor_stand_top_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        mesh.combine(MeshMapUtility.createCubeRotated(room, name, "accessory", (dx - standHalfWid), (dx + standHalfWid), by, (by + standHigh), (dz - standHalfWid), (dz + standHalfWid), rotAngle, true, true, true, true, false, false, false, MeshMapUtility.UV_MAP));

        meshList.add(mesh);

            // the monitor

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + widOffset;
        by += standHigh;

        name = "monitor_stand_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        meshList.add(MeshMapUtility.createCubeRotated(room, name, "monitor", dx, (dx + terminalWidth), by, (by + ((terminalWidth * 6) / 9)), (dz - standHalfWid), (dz + standHalfWid), rotAngle, true, true, true, true, true, true, false, MeshMapUtility.UV_BOX));
    }

        //
        // junctions
        //

    public void addJunction(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz, juncHalfWid;
        String name;
        RagPoint rotAngle, centerPnt, pipePnt;
        Mesh mesh, mesh2;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("pipe")) {
            bitmap = new BitmapPipe();
            bitmap.generate();
            bitmaps.put("pipe", bitmap);
        }
        if (!bitmaps.containsKey("panel")) {
            bitmap = new BitmapControlPanel();
            bitmap.generate();
            bitmaps.put("panel", bitmap);
        }

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

            // the junction

        juncHalfWid = junctionWidth * 0.5f;

        rotAngle = new RagPoint(0.0f, AppWindow.random.nextFloat(359.0f), 0.0f);
        name = "junction_panel_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        meshList.add(MeshMapUtility.createCubeRotated(room, name, "panel", (dx - juncHalfWid), (dx + juncHalfWid), (by + pipeHeight), ((by + pipeHeight) + junctionWidth), (dz - junctionHalfDepth), (dz + junctionHalfDepth), rotAngle, true, true, true, true, true, true, false, MeshMapUtility.UV_BOX));

            // the pipes

        mesh = null;
        centerPnt = new RagPoint(dx, by, dz);

        if (AppWindow.random.nextBoolean()) {
            pipePnt = new RagPoint(((dx - juncHalfWid) + pipeRadius), by, dz);
            pipePnt.rotateAroundPoint(centerPnt, rotAngle);
            name = "junction_upper_neg_pipe_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
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
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
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
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, ((by + pipeHeight) + junctionWidth), (by + (MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT)), pipeRadius, false, false);
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
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
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
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
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
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, name, "pipe", 16, pipePnt, by, (by + pipeHeight), pipeRadius, false, false);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }

        if (mesh != null) {
            meshList.add(mesh);
        }
    }

        //
        // lab tubes
        //

    public void addTube(MapRoom room, int roomNumber, int x, float by, int z) {
        float dx, dz;
        float yBotCapBy, yBotCapTy, yTopCapBy, yTopCapTy, y;
        String name;
        RagPoint centerPnt;
        Mesh mesh, mesh2;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("glass")) {
            bitmap = new BitmapGlass();
            bitmap.generate();
            bitmaps.put("glass", bitmap);
        }
        if (!bitmaps.containsKey("liquid")) {
            bitmap = new BitmapLiquid();
            bitmap.generate();
            bitmaps.put("liquid", bitmap);
        }
        if (!bitmaps.containsKey("accessory")) {
            bitmap = new BitmapMetal();
            bitmap.generate();
            bitmaps.put("accessory", bitmap);
        }

        name = "tube_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        // tube center
        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        centerPnt = new RagPoint(dx, (by + (tubeHeight * 0.5f)), dz);

        // the top and bottom caps
        yBotCapBy = by;
        yBotCapTy = yBotCapBy + tubeBotCapHeight;

        yTopCapBy = yBotCapTy + (tubeHeight - (tubeBotCapHeight + tubeTopCapHeight));
        yTopCapTy = yTopCapBy + tubeTopCapHeight;

        mesh = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", 16, centerPnt, yBotCapTy, yBotCapBy, tubeCapRadius, true, false);
        mesh2 = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", 16, centerPnt, yTopCapTy, yTopCapBy, tubeCapRadius, true, true);
        mesh.combine(mesh2);
        meshList.add(mesh);

        // the tube
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_glass"), "glass", 16, centerPnt, yTopCapBy, yBotCapTy, tubeRadius, false, false));

        // the liquid in the tube
        y = yBotCapTy + (AppWindow.random.nextFloat() * (yTopCapBy - yBotCapTy));
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_liquid"), "liquid", 16, centerPnt, y, yBotCapTy, (tubeRadius * 0.98f), true, false));
    }

        //
        // equipment build
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {

        switch (AppWindow.random.nextInt(4)) {
            case 0:
                addBank(room, roomNumber, x, by, z);
                break;
            case 1:
                addTerminal(room, roomNumber, x, by, z);
                break;
            case 2:
                addTube(room, roomNumber, x, by, z);
                break;
            case 3:
                addJunction(room, roomNumber, x, by, z);
                break;
        }
    }

}
