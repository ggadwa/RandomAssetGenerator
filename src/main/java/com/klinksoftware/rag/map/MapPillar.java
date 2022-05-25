package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapPillar
{

    private float[] cylinderSegments;
    private MeshList meshList;

    public MapPillar(MeshList meshList) {
        this.meshList = meshList;

        cylinderSegments = MeshMapUtility.createCylinderSegmentList(1, 5);
    }

        //
        // pillars
        //

    public void build(MapRoom room, int roomNumber) {
        int x, z, gx, gz, lx, rx, tz, bz;
        float baseHigh, by, pillarTy, ty, pillarBy, offset, radius, baseRadius;
        boolean squareBase;
        String name;
        RagPoint centerPnt;
        Mesh mesh;

        name = "pillar_" + Integer.toString(roomNumber);
        squareBase = AppWindow.random.nextBoolean();

        centerPnt = new RagPoint(room.x * MapBuilder.SEGMENT_SIZE, 0, room.z * MapBuilder.SEGMENT_SIZE);

        ty = MapBuilder.SEGMENT_SIZE + MapBuilder.FLOOR_HEIGHT;
        pillarTy = ty - MapBuilder.FLOOR_HEIGHT;
        by = 0;
        pillarBy = by + MapBuilder.FLOOR_HEIGHT;

        radius = MapBuilder.SEGMENT_SIZE / 4.0f;
        baseRadius = radius * 1.5f;

        mesh = MeshMapUtility.createCylinder(room, name, "pillar", centerPnt, pillarTy, pillarBy, cylinderSegments, radius, false, false);

        if (squareBase) {
            mesh.combine(MeshMapUtility.createCube(room, name, "pillar", (centerPnt.x - baseRadius), (centerPnt.x + baseRadius), pillarBy, by, (centerPnt.z - baseRadius), (centerPnt.z + baseRadius), true, true, true, true, false, true, false, MeshMapUtility.UV_MAP));
            mesh.combine(MeshMapUtility.createCube(room, name, "pillar", (centerPnt.x - baseRadius), (centerPnt.x + baseRadius), ty, pillarTy, (centerPnt.z - baseRadius), (centerPnt.z + baseRadius), true, true, true, true, true, false, false, MeshMapUtility.UV_MAP));
        } else {
            mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", centerPnt, pillarBy, by, baseRadius, true, false));
            mesh.combine(MeshMapUtility.createMeshCylinderSimple(room, name, "pillar", centerPnt, ty, pillarTy, baseRadius, false, true));
        }

        //room.setBlockedGrid(x,z)

        meshList.add(mesh);

        /*

            // build the pillars

        offset=MapBuilder.SEGMENT_SIZE*0.5f;
        radius=MapBuilder.SEGMENT_SIZE*(0.1f+(AppWindow.random.nextFloat()*0.2f));
        baseRadius=radius*(1.3f+(AppWindow.random.nextFloat()*0.2f));

        cylinderSegments=MeshMapUtility.createCylinderSegmentList(5,3,0.2f);
        centerPnt=new RagPoint(0.0f,0.0f,0.0f);

        squareBase=AppWindow.random.nextBoolean();

        for (gz=tz;gz<bz;gz++) {
            for (gx=lx;gx<rx;gx++) {
                if (pattern[gx-lx][gz-tz]==0x0) continue;

                centerPnt.x=room.offset.x+(((float)gx*MapBuilder.SEGMENT_SIZE)+offset);
                centerPnt.z=room.offset.z+(((float)gz*MapBuilder.SEGMENT_SIZE)+offset);

                nameSuffix=Integer.toString(gx)+"_"+Integer.toString(gz);

                mesh=MeshMapUtility.createCylinder(room,(this.name+"_"+nameSuffix),"pillar",centerPnt,pillarTy,pillarBy,cylinderSegments,radius,false,false);

                if (squareBase) {
                    mesh.combine(MeshMapUtility.createCube(room,(name+"_base_bot_"+nameSuffix),"pillar",(centerPnt.x-baseRadius),(centerPnt.x+baseRadius),pillarBy,by,(centerPnt.z-baseRadius),(centerPnt.z+baseRadius),true,true,true,true,false,true,false,MeshMapUtility.UV_MAP));
                    mesh.combine(MeshMapUtility.createCube(room,(name+"_base_bot_"+nameSuffix),"pillar",(centerPnt.x-baseRadius),(centerPnt.x+baseRadius),ty,pillarTy,(centerPnt.z-baseRadius),(centerPnt.z+baseRadius),true,true,true,true,true,false,false,MeshMapUtility.UV_MAP));
                }
                else {
                    mesh.combine(MeshMapUtility.createMeshCylinderSimple(room,(name+"_base_bot_"+nameSuffix),"pillar",centerPnt,pillarBy,by,baseRadius,true,false));
                    mesh.combine(MeshMapUtility.createMeshCylinderSimple(room,(this.name+"_base_top_"+nameSuffix),"pillar",centerPnt,ty,pillarTy,baseRadius,false,true));
                }

                meshList.add(mesh);

                room.setGrid(0,gx,gz,1);
            }
        }
         */
    }
}
