package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapComputer
{
    private float           segmentSize;
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapComputer(MeshList meshList,MapRoom room,String name,float segmentSize)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
        this.segmentSize=segmentSize;
    }
    
        //
        // platform
        //
        
    private void addPlatform(MapRoom room,int lx,int tz,int rx,int bz,float floorDepth)
    {
        float       xMin,xMax,yMin,yMax,zMin,zMax;
        
        xMin=room.offset.x+((float)lx*segmentSize);
        xMax=room.offset.x+((float)rx*segmentSize);
        yMin=room.offset.y;
        yMax=room.offset.y+floorDepth;
        zMin=room.offset.z+((float)tz*segmentSize);
        zMax=room.offset.z+((float)bz*segmentSize);
            
        meshList.add(MeshUtility.createCube(room,(name+"_pedestal"),"platform",xMin,xMax,yMin,yMax,zMin,zMax,true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize));
    }
        
        //
        // computer banks
        //
        
    private void addBank(MapRoom room,int gx,int gz,float wid,float high,float floorDepth,int pieceCount)
    {
        float       x,y,z,widOffset;
        
        widOffset=(segmentSize-wid)*0.5f;
        
        x=(room.offset.x+((float)gx*segmentSize))+widOffset;
        y=room.offset.y+floorDepth;
        z=(room.offset.z+((float)gz*segmentSize))+widOffset;

        meshList.add(MeshUtility.createCube(room,(name+"_computer_"+pieceCount),"computer",x,(x+wid),y,(y+high),z,(z+wid),true,true,true,true,true,false,false,MeshUtility.UV_BOX,segmentSize));
    }
   
        //
        // terminals
        //
        
    public void addTerminal(MapRoom room,int gx,int gz,float wid,float high,float floorDepth,int pieceCount)
    {
        /*
        let panelMargin,mesh,mesh2;
        let xBound,yBound,zBound;
        
            // the panel bottom
            
        panelMargin=genRandom.randomInt(Math.trunc(segmentSize/5),Math.trunc(segmentSize/8));

        xBound=new BoundClass((pnt.x+panelMargin),((pnt.x+segmentSize)-panelMargin));
        zBound=new BoundClass((pnt.z+panelMargin),((pnt.z+segmentSize)-panelMargin));
        yBound=new BoundClass(pnt.y,(pnt.y-Math.trunc(constants.ROOM_FLOOR_HEIGHT*0.3)));
        
        mesh=MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize);
        MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
        
            // the panel wedge

        yBound.max=yBound.min;
        yBound.min=yBound.max-floorDepth;
        
        mesh2=MeshPrimitivesClass.createMeshDirectionWedge(this.view,this.metalBitmap,xBound,yBound,zBound,dir,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
        mesh.combine(mesh2);
        this.map.meshList.add(mesh);
        
            // the panel top
        
        mesh=MeshPrimitivesClass.createMeshDirectionWedge(this.view,this.panelBitmap,xBound,yBound,zBound,dir,false,false,false,true,true,false,constants.MESH_FLAG_DECORATION);
        MeshPrimitivesClass.meshWedgeSetWholeUV(mesh,0,false,false,false);
        MeshPrimitivesClass.meshWedgeScaleUV(mesh,0,false,false,false,0.1,0.9,0.1,0.9);
        this.map.meshList.add(mesh);
        */
    }
    
        //
        // junctions
        //
       
    public void addJunction(MapRoom room,int gx,int gz,float wid,float high,float floorDepth,int pieceCount)
    {
        /*
        let juncMargin,juncWid,pipeRadius,pipeHigh,mesh;
        let xBound,yBound,zBound,pipeYBound,centerPnt;
        let upperPipe,lowerPipe;
        
            // junction sizes
            
        juncMargin=genRandom.randomInt(Math.trunc(segmentSize/5),Math.trunc(segmentSize/8));
        juncWid=Math.trunc(segmentSize*0.2);
        
        pipeRadius=Math.trunc(segmentSize*0.05);
        pipeHigh=Math.trunc(constants.ROOM_FLOOR_HEIGHT*0.3);

        yBound=new BoundClass((pnt.y-pipeHigh),(pnt.y-constants.ROOM_FLOOR_HEIGHT));
        
            // the junction
            
        switch (dir) {
            
            case constants.ROOM_SIDE_LEFT:
                xBound=new BoundClass(pnt.x,(pnt.x+juncWid));
                zBound=new BoundClass((pnt.z+juncMargin),((pnt.z+segmentSize)-juncMargin));
                mesh=MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_TOP:
                xBound=new BoundClass((pnt.x+juncMargin),((pnt.x+segmentSize)-juncMargin));
                zBound=new BoundClass(pnt.z,(pnt.z+juncWid));
                mesh=MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_RIGHT:
                xBound=new BoundClass(((pnt.x+segmentSize)-juncWid),(pnt.x+segmentSize));
                zBound=new BoundClass((pnt.z+juncMargin),((pnt.z+segmentSize)-juncMargin));
                mesh=MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_BOTTOM:
                xBound=new BoundClass((pnt.x+juncMargin),((pnt.x+segmentSize)-juncMargin));
                zBound=new BoundClass(((pnt.z+segmentSize)-juncWid),(pnt.z+segmentSize));
                mesh=MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
        }
        
            // the pipes
            
        upperPipe=genRandom.randomPercentage(0.5);
        lowerPipe=((genRandom.randomPercentage(0.5))||(!upperPipe));
        
        centerPnt=new RagPoint(xBound.getMidPoint(),pnt.y,zBound.getMidPoint());
        
        if (upperPipe) {
            pipeYBound=new BoundClass(room.yBound.min,yBound.min);
            this.map.meshList.add(MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.pipeBitmap,centerPnt,pipeYBound,pipeRadius,false,false,constants.MESH_FLAG_DECORATION));
        }
        if (lowerPipe) {
            pipeYBound=new BoundClass(yBound.max,pnt.y);
            this.map.meshList.add(MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.pipeBitmap,centerPnt,pipeYBound,pipeRadius,false,false,constants.MESH_FLAG_DECORATION));
        }
*/
    }
   
    public void build()
    {
        int     x,z,lx,rx,tz,bz,skipX,skipZ,
                pieceCount;
        float   bankWid,bankHigh,floorDepth;
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
            
        bankWid=(segmentSize*0.5f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.3f));
        bankHigh=bankWid*(1.0f+(GeneratorMain.random.nextFloat()*(segmentSize*0.15f)));
        floorDepth=segmentSize*0.1f;
        
            // the equipment platform
            
        addPlatform(room,lx,tz,rx,bz,floorDepth);
        
            // if enough room, make a path
            // through the equipment
        
        skipX=-1;
        if ((rx-lx)>2) skipX=(lx+1)+GeneratorMain.random.nextInt((rx-lx)-2);
        skipZ=-1;
        if ((bz-tz)>2) skipZ=(tz+1)+GeneratorMain.random.nextInt((bz-tz)-2);
        
            // the pieces
          
        pieceCount=0;
        
        for (z=tz;z<bz;z++) {
            if (z==skipZ) continue;
            
            for (x=lx;x<rx;x++) {
                if (x==skipX) continue;
                
                switch (GeneratorMain.random.nextInt(4)) {
                    case 0:
                    case 1:
                        addBank(room,x,z,bankWid,bankHigh,floorDepth,pieceCount);         // appears twice as much as the others
                        break;
                    case 2:
                    //    this.addTerminal(room,pnt,dir,floorDepth);
                        break;
                    case 3:
                    //    this.addJunction(room,pnt,dir);
                        break;
                }
                
                pieceCount++;
                
                this.room.setGrid(0,x,z,1);
            }
        }
    }

}
