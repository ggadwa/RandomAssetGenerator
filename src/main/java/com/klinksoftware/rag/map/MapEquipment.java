package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.bitmaps.BitmapBase;
import com.klinksoftware.rag.bitmaps.BitmapComputer;
import com.klinksoftware.rag.bitmaps.BitmapMetal;
import com.klinksoftware.rag.bitmaps.BitmapMonitor;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;
import java.util.HashMap;

public class MapEquipment {

    private float computerWidth, computerHeight;
    private float terminalWidth, terminalHeight;
    private float junctionWidth, pipeHeight, pipeRadius;
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
        pipeRadius = (MapBuilder.SEGMENT_SIZE * 0.05f) + (AppWindow.random.nextFloat() * (MapBuilder.SEGMENT_SIZE * 0.1f));

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
        /*
        boolean         upperPipe,lowerPipe;
        float           x,y,z,juncHalfWid;
        RagPoint        rotAngle,centerPnt;
        Mesh            mesh,mesh2;
        BitmapBase bitmap;

        // bitmap
        if (!bitmaps.containsKey("pipe")) {
            bitmap = new BitmapPipe();
            bitmap.generate();
            bitmaps.put("pipe", bitmap);
        }
        if (!bitmaps.containsKey("panel")) {
            bitmap = new BitmapPanel();
            bitmap.generate();
            bitmaps.put("panel", bitmap);
        }

        x=(room.offset.x+((float)gx*MapBuilder.SEGMENT_SIZE))+(MapBuilder.SEGMENT_SIZE*0.5f);
        y=room.offset.y;
        z=(room.offset.z+((float)gz*MapBuilder.SEGMENT_SIZE))+(MapBuilder.SEGMENT_SIZE*0.5f);

            // the junction

        juncHalfWid=juncWid*0.5f;

        rotAngle=new RagPoint(0.0f,(AppWindow.random.nextBoolean()?0.0f:90.0f),0.0f);
        meshList.add(MeshMapUtility.createCubeRotated(room,(name+"_panel_"+pieceCount),"panel",(x-juncHalfWid),(x+juncHalfWid),(y+pipeHigh),((y+pipeHigh)+juncWid),(z-pipeRadius),(z+pipeRadius),rotAngle,true,true,true,true,true,true,false,MeshMapUtility.UV_BOX));

            // the pipes

        upperPipe=AppWindow.random.nextBoolean();
        lowerPipe=((AppWindow.random.nextBoolean())||(!upperPipe));

        mesh=null;
        centerPnt=new RagPoint(x,y,z);

        if (upperPipe) {
            mesh=MeshMapUtility.createMeshCylinderSimple(room,(name+"_panel_pipe_"+pieceCount),"pipe",centerPnt,((y+pipeHigh)+juncWid),(room.offset.y+((float)room.storyCount*MapBuilder.SEGMENT_SIZE)),pipeRadius,false,false);
        }
        if (lowerPipe) {
            mesh2=MeshMapUtility.createMeshCylinderSimple(room,(name+"_panel_pipe_"+pieceCount),"pipe",centerPnt,y,(y+pipeHigh),pipeRadius,false,false);
            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }
        }

        meshList.add(mesh);
*/
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

        name = "tube_" + Integer.toString(roomNumber) + "_" + Integer.toString(x) + "x" + Integer.toString(z);

        // tube center
        dx = ((room.x + x) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);
        dz = ((room.z + z) * MapBuilder.SEGMENT_SIZE) + (MapBuilder.SEGMENT_SIZE * 0.5f);

        centerPnt = new RagPoint(dx, (by + (tubeHeight * 0.5f)), dz);

        // the top and bottom caps
        yBotCapBy = by;
        yBotCapTy = yBotCapBy + tubeBotCapHeight;

        yTopCapBy = yBotCapTy + tubeHeight;
        yTopCapTy = yTopCapBy + tubeTopCapHeight;

        mesh = null;

        if (tubeBotCapHeight != 0.0f) {
            mesh = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", centerPnt, yBotCapTy, yBotCapBy, tubeCapRadius, true, false);
        }

        if (tubeTopCapHeight != 0.0f) {
            mesh2 = MeshMapUtility.createMeshCylinderSimple(room, (name + "_top"), "accessory", centerPnt, yTopCapTy, yTopCapBy, tubeCapRadius, true, true);
            if (mesh == null) {
                mesh = mesh2;
            } else {
                mesh.combine(mesh2);
            }
        }

        meshList.add(mesh);

        // the tube
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_glass"), "glass", centerPnt, yTopCapBy, yBotCapTy, tubeRadius, false, false));

        // the liquid in the tube
        y = yBotCapTy + (AppWindow.random.nextFloat() * (yTopCapBy - yBotCapTy));
        meshList.add(MeshMapUtility.createMeshCylinderSimple(room, (name + "_liquid"), "liquid", centerPnt, y, yBotCapTy, (tubeRadius * 0.98f), true, false));
    }

        //
        // equipment build
        //

    public void build(MapRoom room, int roomNumber, int x, float by, int z) {
        // addBank(room, roomNumber, x, by, z);

        //addTerminal(room, roomNumber, x, by, z);
        //addTube(room, roomNumber, x, by, z);
        addJunction(room, roomNumber, x, by, z);
        /*
        int     x,z,lx,rx,tz,bz,skipX,skipZ,
                pieceType,pieceCount;
        float   bankWid,bankHigh,
                terminalWid,terminalHigh,
                juncWid,pipeHigh,pipeRadius,
                roomHigh,tubeRadius,tubeHigh,
                tubeCapRadius,tubeTopCapHigh,tubeBotCapHigh,
                floorDepth;
        Mesh    mesh;

            // bounds with margins

        lx=room.piece.margins[0];
        rx=room.piece.size.x-(room.piece.margins[2]);
        if (!room.requiredStairs.isEmpty()) {
            if (lx<3) lx=3;
            if (rx>(room.piece.size.x-3)) rx=room.piece.size.x-3;
        }
        if (rx<=lx) return;

        tz=this.room.piece.margins[1];
        bz=this.room.piece.size.z-(room.piece.margins[3]);
        if (!room.requiredStairs.isEmpty()) {
            if (tz<3) tz=3;
            if (bz>(room.piece.size.z-3)) bz=room.piece.size.z-3;
        }
        if (bz<=tz) return;

            // sizes

        roomHigh=(float)room.storyCount*MapBuilder.SEGMENT_SIZE;

        bankWid=(MapBuilder.SEGMENT_SIZE*0.5f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.3f));
        bankHigh=bankWid*(1.0f+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.15f)));

        terminalWid=(MapBuilder.SEGMENT_SIZE*0.5f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.3f));
        terminalHigh=(MapBuilder.SEGMENT_SIZE*0.2f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.3f));

        juncWid=(MapBuilder.SEGMENT_SIZE*0.4f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.2f));
        pipeHigh=(MapBuilder.SEGMENT_SIZE*0.2f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.2f));
        pipeRadius=(MapBuilder.SEGMENT_SIZE*0.05f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.1f));

        tubeRadius=(MapBuilder.SEGMENT_SIZE*0.2f)+(AppWindow.random.nextFloat()*(MapBuilder.SEGMENT_SIZE*0.15f));
        tubeHigh=(roomHigh*0.2f)+(AppWindow.random.nextFloat()*(roomHigh*0.3f));
        tubeCapRadius=tubeRadius*(1.0f+(AppWindow.random.nextFloat()*0.15f));
        tubeTopCapHigh=(roomHigh*0.05f)+(AppWindow.random.nextFloat()*(roomHigh*0.1f));
        tubeBotCapHigh=(roomHigh*0.05f)+(AppWindow.random.nextFloat()*(roomHigh*0.1f));

        floorDepth=MapBuilder.SEGMENT_SIZE*0.1f;

            // if enough room, make a path
            // through the equipment

        skipX=-1;
        if ((rx-lx)>2) skipX=(lx+1)+AppWindow.random.nextInt((rx-lx)-2);
        skipZ=-1;
        if ((bz-tz)>2) skipZ=(tz+1)+AppWindow.random.nextInt((bz-tz)-2);

            // the pieces

        pieceCount=0;

        for (z=tz;z<bz;z++) {
            if (z==skipZ) continue;

            for (x=lx;x<rx;x++) {
                if (x==skipX) continue;

                    // only terminals on edges

                pieceType=AppWindow.random.nextInt(4);
                if ((pieceType==1) && ((z!=tz) && (z!=(bz-1)) && (x!=lx) && (x!=(rx-1)))) pieceType=0;

                switch (pieceType) {
                    case 0:
                        addBank(room,x,z,bankWid,bankHigh,floorDepth,pieceCount);
                        break;
                    case 1:
                        this.addTerminal(room,x,z,terminalWid,terminalHigh,pieceCount);
                        break;
                    case 2:
                        this.addJunction(room,x,z,juncWid,pipeHigh,pipeRadius,pieceCount);
                        break;
                    case 3:
                        addTube(room,x,z,tubeRadius,tubeHigh,tubeCapRadius,tubeTopCapHigh,tubeBotCapHigh,pieceCount);
                        break;
                }

                pieceCount++;

                this.room.setGrid(0,x,z,1);
            }
        }
*/
    }

}
