package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapPillar
{
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapPillar(MeshList meshList,MapRoom room,String name)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
    }
   
        //
        // pillars
        //
    
    public void build()
    {
        /*
        int         x,z,gx,gz,lx,rx,tz,bz;
        float       baseHigh,by,pillarTy,ty,pillarBy,
                    offset,radius,baseRadius;
        float[]     cylinderSegments;
        boolean     squareBase;
        byte        b1,b2,b3;
        byte[][]    pattern;
        String      nameSuffix;
        RagPoint    centerPnt;
        Mesh        mesh;
        
            // bounds with margins
            
        lx=room.piece.margins[0];
        rx=room.piece.size.x-(room.piece.margins[2]);
        if (!room.requiredStairs.isEmpty()) {
            if (lx<2) lx=2;
            if (rx>(room.piece.size.x-2)) rx=room.piece.size.x-2;
        }
        if (rx<=lx) return;
        
        tz=room.piece.margins[1];
        bz=room.piece.size.z-(room.piece.margins[3]);
        if (!room.requiredStairs.isEmpty()) {
            if (tz<2) tz=2;
            if (bz>(room.piece.size.z-2)) bz=room.piece.size.z-2;
        }
        if (bz<=tz) return;
        
            // add in the random patterns
            
        pattern=new byte[rx-lx][bz-tz];
        
        b1=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        b2=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        b3=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        
        gz=(bz-tz)/2;
        
        for (gx=lx;gx<rx;gx+=2) {
            pattern[gx-lx][0]=b1;
            pattern[gx-lx][(bz-tz)-1]=b2;
            pattern[gx-lx][gz]=b3;
        }
        
        b1=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        b2=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        b3=(byte)(AppWindow.random.nextBoolean()?0x0:0x1);
        
        gx=(rx-lx)/2;
        
        for (gz=tz;gz<bz;gz+=2) {
            pattern[0][gz-tz]=b1;
            pattern[(rx-lx)-1][gz-tz]=b2;
            pattern[gx][gz-tz]=b3;
        }
        
            // the y bounds
         
        baseHigh=MapBuilder.SEGMENT_SIZE*(0.1f+(AppWindow.random.nextFloat()*0.2f));
        by=this.room.offset.y;
        ty=(this.room.offset.y+(this.room.storyCount*MapBuilder.SEGMENT_SIZE));
        pillarBy=by+baseHigh;
        pillarTy=ty-baseHigh;
        
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
