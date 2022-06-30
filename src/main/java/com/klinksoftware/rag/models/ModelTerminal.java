package com.klinksoftware.rag.models;

import com.klinksoftware.rag.AppWindow;
import com.klinksoftware.rag.map.MapBuilder;
import com.klinksoftware.rag.mesh.Mesh;
import com.klinksoftware.rag.utility.RagPoint;

@ModelInterface
public class ModelTerminal extends ModelBase {

    @Override
    public void buildInternal() {
        float terminalWidth, terminalHeight, widOffset;
        float deskHalfWid, deskShortHalfWid, standWid, standHalfWid, standHigh;
        String name;
        RagPoint rotAngle;
        Mesh mesh;

        addBitmap("minitor", new String[]{"Monitor"});
        addBitmap("desk", new String[]{"Metal", "Tile", "Wood"});

        // sizes
        terminalWidth = MapBuilder.SEGMENT_SIZE * (0.3f + AppWindow.random.nextFloat(0.2f));
        terminalHeight = MapBuilder.SEGMENT_SIZE * (0.2f + AppWindow.random.nextFloat(0.1f));

        /*


        // the desk and stand
        widOffset = (MapBuilder.SEGMENT_SIZE - terminalWidth) * 0.5f;

        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        deskHalfWid = terminalWidth * 0.5f;
        deskShortHalfWid = deskHalfWid * 0.9f;

        standWid = terminalWidth * 0.05f;
        standHalfWid = standWid * 0.5f;
        standHigh = terminalHeight * 0.1f;

        rotAngle = new RagPoint(0.0f, (AppWindow.random.nextBoolean() ? 0.0f : 90.0f), 0.0f);
        name = "monitor_stand_bottom_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        mesh = MeshUtility.createCube("accessory", (dx - deskHalfWid), (dx + deskHalfWid), by, (by + terminalHeight), (dz - deskShortHalfWid), (dz + deskShortHalfWid), true, true, true, true, true, false, false, MeshUtility.UV_MAP);

        by += terminalHeight;

        rotAngle.setFromValues(0.0f, (AppWindow.random.nextFloat() * 360.0f), 0.0f);
        name = "monitor_stand_top_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        mesh.combine(MeshUtility.createCubeRotated("accessory", (dx - standHalfWid), (dx + standHalfWid), by, (by + standHigh), (dz - standHalfWid), (dz + standHalfWid), rotAngle, true, true, true, true, false, false, false, MeshUtility.UV_MAP));

        meshList.add(mesh);

        // the monitor
        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + widOffset;
        by += standHigh;

        name = "monitor_stand_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);
        meshList.add(MeshUtility.createCubeRotated("monitor", dx, (dx + terminalWidth), by, (by + ((terminalWidth * 6) / 9)), (dz - standHalfWid), (dz + standHalfWid), rotAngle, true, true, true, true, true, true, false, MeshUtility.UV_BOX));
*/
        // now build a fake skeleton for the glTF
        skeleton = meshList.rebuildMapMeshesWithSkeleton();
    }
}
