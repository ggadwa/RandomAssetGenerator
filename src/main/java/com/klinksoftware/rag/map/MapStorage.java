package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

public class MapStorage
{
    private float           segmentSize;
    private String          name;
    private MeshList        meshList;
    private MapRoom         room;
    
    public MapStorage(MeshList meshList,MapRoom room,String name,float segmentSize)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
        this.segmentSize=segmentSize;
    }
    
        //
        // boxes
        //
    
    private void addBoxes(int gx,int gz,float boxSize,int storageCount)
    {
        int         stackLevel,stackCount;
        float       x,y,z,boxHalfSize;
        RagPoint    rotAngle;
        Mesh        mesh,mesh2;
        
            // box size
            
        x=(room.offset.x+(gx*segmentSize))+(segmentSize*0.5f);
        z=(room.offset.z+(gz*segmentSize))+(segmentSize*0.5f);
        
        boxHalfSize=boxSize*0.5f;
        
        rotAngle=new RagPoint(0.0f,0.0f,0.0f);
        
            // stacks of boxes
            
        stackCount=1+GeneratorMain.random.nextInt(3);
            
            // the stacks
            
        mesh=null;
        y=room.offset.y;
            
        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {
            rotAngle.setFromValues(0.0f,(-10.0f+(GeneratorMain.random.nextFloat()*20.0f)),0.0f);
            mesh2=MeshUtility.createCubeRotated(room,(name+"_"+Integer.toString(storageCount)),"box",(x-boxHalfSize),(x+boxHalfSize),y,(y+boxSize),(z-boxHalfSize),(z+boxHalfSize),rotAngle,true,true,true,true,true,(stackLevel!=0),false,MeshUtility.UV_WHOLE,segmentSize);
            
            if (mesh==null) {
                mesh=mesh2;
            }
            else {
                mesh.combine(mesh2);
            }
            
                // go up one level

            y+=boxSize;
            if ((y+boxSize)>(room.offset.y+(segmentSize*room.storyCount))) break;
        }
        
        meshList.add(mesh);
    }
    
        //
        // shelves
        //
        
    private void addShelf(int gx,int gz,float shelfHigh,float shelfBoxSize,float xShelfMargin,float zShelfMargin,int storageCount)
    {
        /*
        let legWid,mesh,mesh2;
        let stackLevel,stackCount;
        let n,nItem,bx,bz,boxSize,rotAngle,boxMesh,minBoxSize,extraBoxSize,minBoxHigh,extraBoxHigh;
        let tableXBound,tableYBound,tableZBound;
        let legXMinBound,legXMaxBound,legZMinBound,legZMaxBound,legYBound;
        let boxXBound,boxYBound,boxZBound;
        */
        
        /*
        int             stackLevel,stackCount;
        float           x,z,legWid;
        RagPoint        rotAngle;
        
        x=room.offset.x+((float)x*segmentSize);
        z=room.offset.z+((float)z*segmentSize);
        
        legWid=(segmentSize*0.1f);

            // height and width

        stackCount=1+GeneratorMain.random.nextInt(3);
        
            // some preset bounds
            
        rotAngle=new RagPoint(0.0f,0.0f,0.0f);
        
        tableXBound=new BoundClass((x+this.xShelfMargin),((x+segmentSize)-this.xShelfMargin));
        tableYBound=new BoundClass((room.yBound.max-this.shelfHigh),((room.yBound.max-this.shelfHigh)+constants.ROOM_FLOOR_DEPTH));
        tableZBound=new BoundClass((z+this.zShelfMargin),((z+segmentSize)-this.zShelfMargin));

        legXMinBound=new BoundClass((x+this.xShelfMargin),((x+this.xShelfMargin)+legWid));
        legXMaxBound=new BoundClass((((x+segmentSize)-this.xShelfMargin)-legWid),((x+segmentSize)-this.xShelfMargin));
        legZMinBound=new BoundClass((z+this.zShelfMargin),((z+this.zShelfMargin)+legWid));
        legZMaxBound=new BoundClass((((z+segmentSize)-this.zShelfMargin)-legWid),((z+segmentSize)-this.zShelfMargin));
        
        legYBound=new BoundClass(((room.yBound.max-this.shelfHigh)+constants.ROOM_FLOOR_DEPTH),room.yBound.max);
        
        boxXBound=new BoundClass(0,0,0);
        boxYBound=new BoundClass(0,0,0);
        boxZBound=new BoundClass(0,0,0);
        
        minBoxHigh=Math.trunc(this.shelfHigh*0.5);
        extraBoxHigh=Math.trunc(this.shelfHigh*0.25);
        
        minBoxSize=Math.trunc(segmentSize*0.05);
        extraBoxSize=Math.trunc(segmentSize*0.15);

            // the stacked shelves
            
        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {

                // the table

            mesh2=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,tableXBound,tableYBound,tableZBound,true,true,true,true,true,true,false,constants.MESH_FLAG_DECORATION);
            if (mesh===null) {
                mesh=mesh2;
            }
            else {
                mesh.combineMesh(mesh2);
            }
            
                // legs

            mesh2=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,legXMinBound,legYBound,legZMinBound,true,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
            mesh.combineMesh(mesh2);

            mesh2=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,legXMinBound,legYBound,legZMaxBound,true,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
            mesh.combineMesh(mesh2);

            mesh2=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,legXMaxBound,legYBound,legZMinBound,true,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
            mesh.combineMesh(mesh2);

            mesh2=MeshPrimitivesClass.createMeshCube(this.view,this.metalBitmap,legXMaxBound,legYBound,legZMaxBound,true,true,true,true,false,false,false,constants.MESH_FLAG_DECORATION);
            mesh.combineMesh(mesh2);
            
                // items on self
                
            nItem=genRandom.randomIndex(3);
            
            for (n=0;n!==nItem;n++) {
                boxSize=genRandom.randomInt(minBoxSize,extraBoxSize);
                bx=genRandom.randomInt((tableXBound.min+boxSize),(tableXBound.getSize()-(boxSize*2)));
                bz=genRandom.randomInt((tableZBound.min+boxSize),(tableZBound.getSize()-(boxSize*2)));
                
                boxXBound.setFromValues((bx-boxSize),(bx+boxSize));
                boxYBound.setFromValues((tableYBound.min-genRandom.randomInt(minBoxHigh,extraBoxHigh)),tableYBound.min);
                boxZBound.setFromValues((bz-boxSize),(bz+boxSize));

                rotAngle.setFromValues(0.0,(genRandom.randomFloat(-10.0,20.0)),0.0);
                boxMesh=MeshPrimitivesClass.createMeshRotatedCube(this.view,this.woodBitmap,boxXBound,boxYBound,boxZBound,rotAngle,true,true,true,true,true,false,false,constants.MESH_FLAG_DECORATION);
                MeshPrimitivesClass.meshCubeSetWholeUV(boxMesh);
                this.map.meshList.add(boxMesh);
            }

                // go up one level

            tableYBound.add(-this.shelfHigh);
            if (tableYBound.min<room.yBound.min) break;
            
            legYBound.add(-this.shelfHigh);
        }
        
        this.map.meshList.add(mesh);
        */
    }
            
        //
        // storage
        //

    public void build()
    {
        int     x,z,lx,rx,tz,bz,
                storageCount;
        float   boxSize,shelfBoxSize,
                shelfHigh,xShelfMargin,zShelfMargin;
        
            // bounds with margins
            
        lx=room.piece.margins[0];
        rx=room.piece.size.x-(this.room.piece.margins[2]);
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
        
            // create the pieces
            
        storageCount=0;
        
        boxSize=(segmentSize*0.3f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.4f));
        
        shelfHigh=(segmentSize*0.2f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.3f));
        shelfBoxSize=(segmentSize*0.3f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.4f));
        xShelfMargin=GeneratorMain.random.nextFloat()*(segmentSize*0.125f);
        zShelfMargin=GeneratorMain.random.nextFloat()*(segmentSize*0.125f);
        
        for (z=tz;z<bz;z++) {
            for (x=lx;x<rx;x++) {
                switch (GeneratorMain.random.nextInt(3)) {
                    case 0:
                        addBoxes(x,z,boxSize,storageCount);
                        storageCount++;
                        room.setGrid(0,x,z,1);
                        break;
                    case 1:
                        addShelf(x,z,shelfHigh,shelfBoxSize,xShelfMargin,zShelfMargin,storageCount);
                        storageCount++;
                        room.setGrid(0,x,z,1);
                        break;
                }
            }
        }
    }

}
