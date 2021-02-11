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
        
    private void addShelf(int gx,int gz,int xSize,int zSize,float shelfHigh,float shelfLegWid,float xShelfMargin,float zShelfMargin,float floorDepth,int storageCount)
    {
        int             x2,z2,stackLevel,stackCount,boxCount;
        float           x,y,z,bx,bz,
                        tableXMin,tableXMax,tableZMin,tableZMax,
                        boxSize;
        RagPoint        rotAngle;
        Mesh            shelfMesh,boxMesh,mesh2;
        
        x=room.offset.x+((float)gx*segmentSize);
        z=room.offset.z+((float)gz*segmentSize);

            // height and width

        stackCount=1+GeneratorMain.random.nextInt(3);
        
            // some preset bounds
            
        rotAngle=new RagPoint(0.0f,0.0f,0.0f);
        
        tableXMin=x+xShelfMargin;
        tableXMax=(x+((float)xSize*segmentSize))-xShelfMargin;
        tableZMin=z+zShelfMargin;
        tableZMax=(z+((float)zSize*segmentSize))-zShelfMargin;
        
        y=room.offset.y;
        
        rotAngle=new RagPoint(0.0f,0.0f,0.0f);

            // the stacked shelves
            
        shelfMesh=null;
        boxMesh=null;
        
        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {

                // the table

            mesh2=MeshUtility.createCube(room,(name+"_shelf_"+Integer.toString(storageCount)),"accessory",tableXMin,tableXMax,(y+shelfHigh),((y+shelfHigh)+floorDepth),tableZMin,tableZMax,true,true,true,true,true,true,false,MeshUtility.UV_MAP,segmentSize);
            if (shelfMesh==null) {
                shelfMesh=mesh2;
            }
            else {
                shelfMesh.combine(mesh2);
            }
            
                // legs

            mesh2=MeshUtility.createCube(room,"","accessory",tableXMin,(tableXMin+shelfLegWid),y,(y+shelfHigh),tableZMin,(tableZMin+shelfLegWid),true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize);
            shelfMesh.combine(mesh2);

            mesh2=MeshUtility.createCube(room,"","accessory",tableXMin,(tableXMin+shelfLegWid),y,(y+shelfHigh),(tableZMax-shelfLegWid),tableZMax,true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize);
            shelfMesh.combine(mesh2);

            mesh2=MeshUtility.createCube(room,"","accessory",(tableXMax-shelfLegWid),tableXMax,y,(y+shelfHigh),tableZMin,(tableZMin+shelfLegWid),true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize);
            shelfMesh.combine(mesh2);

            mesh2=MeshUtility.createCube(room,"","accessory",(tableXMax-shelfLegWid),tableXMax,y,(y+shelfHigh),(tableZMax-shelfLegWid),tableZMax,true,true,true,true,false,false,false,MeshUtility.UV_MAP,segmentSize);
            shelfMesh.combine(mesh2);
            
                // items on shelf
                
            boxCount=0;
                 
            for (z2=gz;z2<(gz+zSize);z2++) {
                for (x2=gx;x2<(gx+xSize);x2++) {
                    if (GeneratorMain.random.nextBoolean()) continue;
                    
                    boxSize=(shelfHigh*0.5f)+(GeneratorMain.random.nextFloat()*(shelfHigh*0.25f));
                    bx=(room.offset.x+((float)x2*segmentSize))+(segmentSize*0.5f);
                    bz=(room.offset.z+((float)z2*segmentSize))+(segmentSize*0.5f);

                    rotAngle.setFromValues(0.0f,(-10.0f+(GeneratorMain.random.nextFloat()*20.0f)),0.0f);
                    mesh2=MeshUtility.createCubeRotated(room,(name+"_shelf_box_"+Integer.toString(storageCount)+"_"+Integer.toString(boxCount)),"box",(bx-boxSize),(bx+boxSize),((y+shelfHigh)+floorDepth),(((y+shelfHigh)+floorDepth)+boxSize),(bz-boxSize),(bz+boxSize),rotAngle,true,true,true,true,true,true,false,MeshUtility.UV_WHOLE,segmentSize);
                    if (boxMesh==null) {
                        boxMesh=mesh2;
                    }
                    else {
                        boxMesh.combine(mesh2);
                    }
                }
                
                boxCount++;
            }
            
                // go up one level

            y+=(shelfHigh+floorDepth);
            if (y>(room.offset.y+(segmentSize*room.storyCount))) break;
        }
            
        if (shelfMesh!=null) meshList.add(shelfMesh);
        if (boxMesh!=null) meshList.add(boxMesh);
    }
            
        //
        // storage
        //

    public void build()
    {
        int     x,z,x2,z2,lx,rx,tz,bz,xSize,zSize,
                storageCount;
        float   boxSize,shelfHigh,shelfLegWid,xShelfMargin,zShelfMargin,
                floorDepth;
    
        floorDepth=segmentSize*0.1f;
        
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
        
        boxSize=(segmentSize*0.4f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.4f));
        
        shelfHigh=(segmentSize*0.35f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.35f));
        shelfLegWid=(segmentSize*0.03f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.05f));
        xShelfMargin=(segmentSize*0.025f)+GeneratorMain.random.nextFloat()*(segmentSize*0.05f);
        zShelfMargin=(segmentSize*0.025f)+GeneratorMain.random.nextFloat()*(segmentSize*0.05f);
        
        for (z=tz;z<bz;z++) {
            for (x=lx;x<rx;x++) {
                
                    // if we already filled this segment,
                    // this happens for larger tables
                    
                if (room.getGrid(0,x,z)!=0) continue;
                
                    // add item to segment
                    
                switch (GeneratorMain.random.nextInt(3)) {
                    
                        // stack of boxes
                    
                    case 0:
                        addBoxes(x,z,boxSize,storageCount);
                        storageCount++;
                        room.setGrid(0,x,z,1);
                        break;
                        
                        // shelf with possible boxes
                        
                    case 1:
                        xSize=zSize=1;
                        
                        if (GeneratorMain.random.nextBoolean()) {           // shelfs have random sizes
                            xSize=2;
                            if ((x+xSize)>=rx) {
                                xSize=1;
                            }
                            else {
                                if (room.getGrid(0,(x+1),z)!=0) xSize=1;
                            }
                        }
                        else {
                            zSize=2;
                            if ((z+zSize)>=bz) {
                                zSize=1;
                            }
                            else {
                                if (room.getGrid(0,x,(z+1))!=0) zSize=1;
                            }
                        }
                        
                        addShelf(x,z,xSize,zSize,shelfHigh,shelfLegWid,xShelfMargin,zShelfMargin,floorDepth,storageCount);
                        storageCount++;
                        
                        for (z2=z;z2<(z+zSize);z2++) {
                            for (x2=x;x2<(x+xSize);x2++) {
                                room.setGrid(0,x2,z2,1);
                            }
                        }
                        
                        break;
                }
            }
        }
    }

}
