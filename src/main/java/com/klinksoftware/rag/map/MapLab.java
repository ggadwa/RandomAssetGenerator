package com.klinksoftware.rag.map;

import com.klinksoftware.rag.GeneratorMain;
import com.klinksoftware.rag.mesh.*;

public class MapLab
{
    private float           segmentSize;
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapLab(MeshList meshList,MapRoom room,String name,float segmentSize)
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
        
        this.tubeHigh=constants.ROOM_FLOOR_HEIGHT;
        this.tubeCapHigh=genRandom.randomInt(floorDepth,Math.trunc(constants.ROOM_FLOOR_HEIGHT*0.25));
        
        this.smallTubeHigh=genRandom.randomInt(floorDepth,floorDepth);
        this.smallTubeCapHigh=Math.trunc(this.smallTubeHigh*genRandom.randomFloat(0.1,0.2));

            // bitmaps
            
        genBitmap=new GenBitmapMetalClass(this.view);
        this.metalBitmap=genBitmap.generate(false);
        
        genBitmap=new GenBitmapPanelClass(this.view);
        this.panelBitmap=genBitmap.generate(false);
        
        genBitmap=new GenBitmapGlassClass(this.view);
        this.glassBitmap=genBitmap.generate(false);
        
        genBitmap=new GenBitmapGooClass(this.view);
        this.gooBitmap=genBitmap.generate(false);
        
        genBitmap=new GenBitmapPipeClass(this.view);
        this.pipeBitmap=genBitmap.generate(false);
        
        Object.seal(this);
    }
    */
        //
        // lab tubes
        //
        /*
    addTubeInternal(centerPnt,radius,topCapHigh,botCapHigh,tubeHigh,float floorDepth)
    {
        let yBound,mesh,meshIdx;
        let yCapTop,yCapBottom,yBaseTop,yBaseBottom,yLiqHigh;
        let moveY,movement,msec;
        
            // the top and bottom base
        
        yCapTop=(centerPnt.y-tubeHigh);
        yCapBottom=yCapTop+topCapHigh;
        
        yBaseTop=centerPnt.y-botCapHigh;
        yBaseBottom=centerPnt.y;
        
        if (yBaseTop!==yBaseBottom) {
            yBound=new BoundClass(yBaseTop,yBaseBottom);
            mesh=MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.pipeBitmap,centerPnt,yBound,radius,true,false,constants.MESH_FLAG_DECORATION);
            MeshPrimitivesClass.meshCylinderScaleU(mesh,5.0);
            this.map.meshList.add(mesh);
        }
        
        if (yCapTop!==yCapBottom) {
            yBound=new BoundClass(yCapTop,yCapBottom);
            mesh=MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.pipeBitmap,centerPnt,yBound,radius,true,true,constants.MESH_FLAG_DECORATION);
            MeshPrimitivesClass.meshCylinderScaleU(mesh,5.0);
            this.map.meshList.add(mesh);
        }
        
            // the tube
        
        yBound=new BoundClass(yCapBottom,yBaseTop); 
        radius=Math.trunc(radius*0.9);

        mesh=MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.glassBitmap,centerPnt,yBound,radius,false,false,constants.MESH_FLAG_DECORATION);
        MeshPrimitivesClass.meshCylinderScaleU(mesh,5.0);
        this.map.meshList.add(mesh);
        
            // the liquid in the tube
        
        yLiqHigh=genRandom.randomInt(floorDepth,(yBound.getSize()-floorDepth));    
        yBound=new BoundClass((yBaseTop-yLiqHigh),yBaseTop);
        
        radius=Math.trunc(radius*0.98);

        mesh=MeshPrimitivesClass.createMeshCylinderSimple(this.view,this.gooBitmap,centerPnt,yBound,radius,true,false,constants.MESH_FLAG_DECORATION);
        MeshPrimitivesClass.meshCylinderScaleU(mesh,5.0);
        meshIdx=this.map.meshList.add(mesh);
        
            // liquid movement
        
        moveY=Math.trunc(yLiqHigh*genRandom.randomFloat(0.1,0.7));
        msec=genRandom.randomInt(500,2000);
        
        movement=new MovementClass(meshIdx,true,0);
        movement.addMove(new MoveClass(msec,new RagPoint(0,0,0)));
        movement.addMove(new MoveClass(msec,new RagPoint(0,moveY,0)));
        
        this.map.movementList.add(movement);
    }
    */
    
    public void addTube(MapRoom room,int gx,int gz,int pieceCount)
    {
        /*
        let centerPnt,radius;
        
        x=(room.xBound.min+(x*constants.ROOM_BLOCK_WIDTH))+Math.trunc(constants.ROOM_BLOCK_WIDTH*0.5);
        z=(room.zBound.min+(z*constants.ROOM_BLOCK_WIDTH))+Math.trunc(constants.ROOM_BLOCK_WIDTH*0.5);
        centerPnt=new RagPoint(x,room.yBound.max,z);
        
        radius=Math.trunc(constants.ROOM_BLOCK_WIDTH*0.35);
        
        this.addTubeInternal(centerPnt,radius,this.tubeCapHigh,this.tubeCapHigh,this.tubeHigh);
*/
    }
    
        //
        // lab machinery
        //
/*
    addMachineryItemPanel(xBound,zBound,y,floorDepth)
    {
        let mesh,yBound;
        
        yBound=new BoundClass((y-Math.trunc(floorDepth*0.5)),y);
        
        mesh=MeshUtility.createCube(room,"","panel",xBound,yBound,zBound,true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize);
        MeshPrimitivesClass.meshCubeSetWholeUV(mesh);
        MeshPrimitivesClass.meshCubeScaleUV(mesh,0,0.1,0.9,0.0,0.1);
        MeshPrimitivesClass.meshCubeScaleUV(mesh,1,0.1,0.9,0.0,0.1);
        MeshPrimitivesClass.meshCubeScaleUV(mesh,2,0.1,0.9,0.0,0.1);
        MeshPrimitivesClass.meshCubeScaleUV(mesh,3,0.1,0.9,0.0,0.1);
        MeshPrimitivesClass.meshCubeScaleUV(mesh,4,0.1,0.9,0.1,0.9);
        this.map.meshList.add(mesh);
    }
    
    addMachineryItem(xBound,zBound,y)
    {
        let centerPnt,radius,high,capHigh;
        
        switch (genRandom.randomIndex(3)) {
            
                // small tube
                
            case 0:
                centerPnt=new RagPoint(xBound.getMidPoint(),y,zBound.getMidPoint());
                radius=Math.trunc(xBound.getSize()*0.3);

                high=genRandom.randomInt(floorDepth,floorDepth);
                capHigh=Math.trunc(high*genRandom.randomFloat(0.1,0.2));

                this.addTubeInternal(centerPnt,radius,this.smallTubeCapHigh,0,this.smallTubeHigh);
                break;
                
                // panel
                
            case 1:
                this.addMachineryItemPanel(xBound,zBound,y);
                break;
        }
    }
    */
    
    public void addMachinery(MapRoom room,int gx,int gz,int pieceCount)
    {
        /*
        let xBound,yBound,zBound;
        let sz,xBoundItem,zBoundItem;
        let reduceSize;
        
            // the platform
            
        x=room.xBound.min+(x*constants.ROOM_BLOCK_WIDTH);
        z=room.zBound.min+(z*constants.ROOM_BLOCK_WIDTH);

        xBound=new BoundClass(x,(x+constants.ROOM_BLOCK_WIDTH));
        zBound=new BoundClass(z,(z+constants.ROOM_BLOCK_WIDTH));

        yBound=new BoundClass((room.yBound.max-floorDepth),room.yBound.max);
        this.map.meshList.add(MeshUtility.createCube(room,"","platform",xBound,yBound,zBound,true,true,true,true,true,false,false,MeshUtility.UV_MAP,segmentSize));
        
            // the box
        
        reduceSize=Math.trunc(constants.ROOM_BLOCK_WIDTH*0.1);
        xBound.min+=reduceSize;
        xBound.max-=reduceSize;
        zBound.min+=reduceSize;
        zBound.max-=reduceSize;

        yBound=new BoundClass((room.yBound.max-Math.trunc(constants.ROOM_FLOOR_HEIGHT*0.3)),(room.yBound.max-floorDepth));
        this.map.meshList.add(MeshUtility.createCube(room,"","accessory",xBound,yBound,zBound,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize));
        
            // box items
        
        sz=Math.trunc((xBound.getSize()-(constants.ROOM_BLOCK_WIDTH*0.05))*0.5);
        
        xBoundItem=new BoundClass(xBound.min,(xBound.min+sz));
        zBoundItem=new BoundClass(zBound.min,(zBound.min+sz));
        this.addMachineryItem(xBoundItem,zBoundItem,yBound.min);
        
        xBoundItem=new BoundClass((xBound.max-sz),xBound.max);
        this.addMachineryItem(xBoundItem,zBoundItem,yBound.min);
        
        xBoundItem=new BoundClass(xBound.min,(xBound.min+sz));
        zBoundItem=new BoundClass((zBound.max-sz),zBound.max);
        this.addMachineryItem(xBoundItem,zBoundItem,yBound.min);
        
        xBoundItem=new BoundClass((xBound.max-sz),xBound.max);
        this.addMachineryItem(xBoundItem,zBoundItem,yBound.min);
*/
    }
        
        //
        // lab
        //
    
    public void build()
    {
        int         x,z,lx,rx,tz,bz,skipX,skipZ,
                    pieceCount;
        
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
        
            // if enough room, make a path
            // through the lab
        
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
        
                switch (GeneratorMain.random.nextInt(3)) {
                    case 0:
                        addTube(room,x,z,pieceCount);
                        pieceCount++;
                        room.setGrid(0,x,z,1);
                        break;
                    case 1:
                        addMachinery(room,x,z,pieceCount);
                        pieceCount++;
                        room.setGrid(0,x,z,1);
                        break;
                }
            }
        }

    }
    
}
