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
    
    /*
        constructor(view,map,platformBitmap)
    {
        let genBitmap;
        
        super(view,map,platformBitmap);
        
            // bitmaps
            
        genBitmap=new GenBitmapMetalClass(this.view);
        this.metalBitmap=genBitmap.generate(false);

        genBitmap=new GenBitmapPanelClass(this.view);
        this.panelBitmap=genBitmap.generate(false);

        genBitmap=new GenBitmapComputerClass(this.view);
        this.computerBitmap=genBitmap.generate(false);

        genBitmap=new GenBitmapPipeClass(this.view);
        this.pipeBitmap=genBitmap.generate(false);

        Object.seal(this);
    }
    
        //
        // platform
        //
        
    addPlatform(room,rect)
    {
        let xBound,yBound,zBound;
        
        xBound=new BoundClass((room.xBound.min+(rect.lft*segmentSize)),(room.xBound.min+(rect.rgt*segmentSize)));
        zBound=new BoundClass((room.zBound.min+(rect.top*segmentSize)),(room.zBound.min+(rect.bot*segmentSize)));
        yBound=new BoundClass((room.yBound.max-constants.ROOM_FLOOR_DEPTH),room.yBound.max);

        this.map.meshList.add(MeshPrimitivesClass.createMeshCube(this.view,this.platformBitmap,xBound,yBound,zBound,true,true,true,true,true,false,false,constants.MESH_FLAG_DECORATION));
    }
        
        //
        // computer banks
        //
        
    addBank(room,x,z,margin,dir)
    {
        let wid,mesh;
        let xBound,yBound,zBound,xBound2,zBound2;
       
            // computer

        wid=segmentSize-(margin*2);
        
        x=room.xBound.min+(x*segmentSize);
        z=room.zBound.min+(z*segmentSize);
        
        xBound=new BoundClass((x+margin),(x+wid));
        zBound=new BoundClass((z+margin),(z+wid));
        yBound=new BoundClass((room.yBound.max-constants.ROOM_FLOOR_HEIGHT),(room.yBound.max-constants.ROOM_FLOOR_DEPTH));
        
            // create meshes that point right way
            
        switch (dir) {
            
            case constants.ROOM_SIDE_LEFT:
                xBound2=new BoundClass((xBound.min+constants.ROOM_FLOOR_DEPTH),xBound.max);
                xBound.max=xBound2.min;
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound2,yBound,zBound,false,true,true,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.computerBitmap,xBound,yBound,zBound,true,false,true,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,0,0.1,0.9,0.1,0.9);        // front facing poly
                MeshPrimitivesClass.meshCubeScaleUV(mesh,1,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,2,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,3,0.1,0.9,0.0,0.1);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_TOP:
                zBound2=new BoundClass((zBound.min+constants.ROOM_FLOOR_DEPTH),zBound.max);
                zBound.max=zBound2.min;
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound2,true,true,false,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.computerBitmap,xBound,yBound,zBound,true,true,true,false,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,0,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,1,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,2,0.1,0.9,0.1,0.9);        // front facing poly
                MeshPrimitivesClass.meshCubeScaleUV(mesh,3,0.1,0.9,0.0,0.1);
                this.map.meshList.add(mesh);
                
                break;
                
            case constants.ROOM_SIDE_RIGHT:
                xBound2=new BoundClass(xBound.min,(xBound.max-constants.ROOM_FLOOR_DEPTH));
                xBound.min=xBound2.max;
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound2,yBound,zBound,true,false,true,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.computerBitmap,xBound,yBound,zBound,false,true,true,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,0,0.1,0.9,0.1,0.9);        // front facing poly
                MeshPrimitivesClass.meshCubeScaleUV(mesh,1,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,2,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,3,0.1,0.9,0.0,0.1);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_BOTTOM:
                zBound2=new BoundClass(zBound.min,(zBound.max-constants.ROOM_FLOOR_DEPTH));
                zBound.min=zBound2.max;
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound2,true,true,true,false,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.computerBitmap,xBound,yBound,zBound,true,true,false,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,0,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,1,0.0,0.1,0.1,0.9);
                MeshPrimitivesClass.meshCubeScaleUV(mesh,2,0.1,0.9,0.1,0.9);        // front facing poly
                MeshPrimitivesClass.meshCubeScaleUV(mesh,3,0.1,0.9,0.0,0.1);
                this.map.meshList.add(mesh);
                break;

        }
    }
    
        //
        // terminals
        //
        
    addTerminal(room,pnt,dir)
    {
        let panelMargin,mesh,mesh2;
        let xBound,yBound,zBound;
        
            // the panel bottom
            
        panelMargin=genRandom.randomInt(Math.trunc(segmentSize/5),Math.trunc(segmentSize/8));

        xBound=new BoundClass((pnt.x+panelMargin),((pnt.x+segmentSize)-panelMargin));
        zBound=new BoundClass((pnt.z+panelMargin),((pnt.z+segmentSize)-panelMargin));
        yBound=new BoundClass(pnt.y,(pnt.y-Math.trunc(constants.ROOM_FLOOR_HEIGHT*0.3)));
        
        mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound,true,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
        MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
        
            // the panel wedge

        yBound.max=yBound.min;
        yBound.min=yBound.max-constants.ROOM_FLOOR_DEPTH;
        
        mesh2=MeshPrimitivesClass.createMeshDirectionWedge(this.view,this.metalBitmap,xBound,yBound,zBound,dir,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
        mesh.combineMesh(mesh2);
        this.map.meshList.add(mesh);
        
            // the panel top
        
        mesh=MeshPrimitivesClass.createMeshDirectionWedge(this.view,this.panelBitmap,xBound,yBound,zBound,dir,false,false,false,true,true,false,constants.MESH_FLAG_DECORATION);
        MeshPrimitivesClass.meshWedgeSetWholeUV(mesh,0,false,false,false);
        MeshPrimitivesClass.meshWedgeScaleUV(mesh,0,false,false,false,0.1,0.9,0.1,0.9);
        this.map.meshList.add(mesh);
    }
    
        //
        // junctions
        //
        
    addJunction(room,pnt,dir)
    {
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
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound,true,true,true,true,true,true,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_TOP:
                xBound=new BoundClass((pnt.x+juncMargin),((pnt.x+segmentSize)-juncMargin));
                zBound=new BoundClass(pnt.z,(pnt.z+juncWid));
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound,true,true,true,true,true,true,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_RIGHT:
                xBound=new BoundClass(((pnt.x+segmentSize)-juncWid),(pnt.x+segmentSize));
                zBound=new BoundClass((pnt.z+juncMargin),((pnt.z+segmentSize)-juncMargin));
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound,true,true,true,true,true,true,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
                this.map.meshList.add(mesh);
                break;
                
            case constants.ROOM_SIDE_BOTTOM:
                xBound=new BoundClass((pnt.x+juncMargin),((pnt.x+segmentSize)-juncMargin));
                zBound=new BoundClass(((pnt.z+segmentSize)-juncWid),(pnt.z+segmentSize));
                mesh=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,xBound,yBound,zBound,true,true,true,true,true,true,false,constants.MESH_FLAG_DECORATION);
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
    }
    
        //
        // single spot piece
        //
        
    addPiece(room,x,z,margin,dir)
    {
        let pnt;
        
            // computer item
            
        pnt=new RagPoint((room.xBound.min+(x*segmentSize)),room.yBound.max,(room.zBound.min+(z*segmentSize)));
        
        switch (genRandom.randomIndex(4)) {
            case 0:
            case 1:
                this.addBank(room,x,z,margin,dir);         // appears twice as much as the others
                break;
            case 2:
                this.addTerminal(room,pnt,dir);
                break;
            case 3:
                this.addJunction(room,pnt,dir);
                break;
        }
    }
    
        //
        // computer decorations mainline
        //

    create(room,rect)
    {
        let x,z,margin;
        
            // the platform
            
        this.addPlatform(room,rect);
        
            // a margin for the items that use
            // the same margins
            
        margin=genRandom.randomInt(0,Math.trunc(segmentSize/8));
        
            // computer pieces
            
        for (x=rect.lft;x!==rect.rgt;x++) {
            for (z=rect.top;z!==rect.bot;z++) {
                
                if (x===rect.lft) {
                    this.addPiece(room,x,z,margin,constants.ROOM_SIDE_LEFT);
                }
                else {
                    if (x===(rect.rgt-1)) {
                        this.addPiece(room,x,z,margin,constants.ROOM_SIDE_RIGHT);
                    }
                    else {
                        if (z===(rect.top)) {
                            this.addPiece(room,x,z,margin,constants.ROOM_SIDE_TOP);
                        }
                        else {
                            if (z===(rect.bot-1)) {
                                this.addPiece(room,x,z,margin,constants.ROOM_SIDE_BOTTOM);
                            }
                        }
                    }
                }
            }
        }
    }
    */
    
    public void build()
    {
        int     x,z,lx,rx,tz,bz,skipX,skipZ,
                computerCount;
        float   ty,by,wid,widOffset,
                xMin,xMax,yMin,yMax,zMin,zMax;
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
        
            // size
            
        wid=segmentSize*0.7f;
        widOffset=segmentSize*0.15f;
        ty=segmentSize;
        by=segmentSize*0.1f;
        
            // the equipment floor
            
        xMin=room.offset.x+((float)lx*segmentSize);
        xMax=room.offset.x+((float)rx*segmentSize);
        yMin=room.offset.y;
        yMax=room.offset.y+by;
        zMin=room.offset.z+((float)tz*segmentSize);
        zMax=room.offset.z+((float)bz*segmentSize);
            
        mesh=MeshUtility.createCube(room,(name+"_pedestal"),"platform",xMin,xMax,yMin,yMax,zMin,zMax,true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize);
        meshList.add(mesh);
        
            // if enough room, make a path
            // through the computers
        
        skipX=-1;
        if ((rx-lx)>2) skipX=(lx+1)+GeneratorMain.random.nextInt((rx-lx)-2);
        skipZ=-1;
        if ((bz-tz)>2) skipZ=(tz+1)+GeneratorMain.random.nextInt((bz-tz)-2);
        
            // the computers
          
        computerCount=0;
        
        yMin=room.offset.y+by;
        yMax=room.offset.y+ty;
        
        for (z=tz;z<bz;z++) {
            if (z==skipZ) continue;
            
            zMin=(room.offset.z+((float)z*segmentSize))+widOffset;
            zMax=zMin+wid;
            
            for (x=lx;x<rx;x++) {
                if (x==skipX) continue;
                
                xMin=(room.offset.x+((float)x*segmentSize))+widOffset;
                xMax=xMin+wid;
                
                mesh=MeshUtility.createCube(room,(name+"_computer_"+computerCount),"computer",xMin,xMax,yMin,yMax,zMin,zMax,true,true,true,true,true,false,false,MeshUtility.UV_BOX,segmentSize);
                computerCount++;
                
                meshList.add(mesh);
                
                this.room.setGrid(0,x,z,1);
            }
        }
    }

}
