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

    private void addBoxes(int gx,int gz,int storageCount)
    {
        int         stackLevel,stackCount;
        float       x,y,z,boxSize,boxHalfSize;
        RagPoint    rotAngle;
        
            // box size
            
        x=(room.offset.x+(gx*segmentSize))+(segmentSize*0.5f);
        z=(room.offset.z+(gz*segmentSize))+(segmentSize*0.5f);
        
        boxSize=(segmentSize*0.3f)+(GeneratorMain.random.nextFloat()*(segmentSize*0.4f));
        boxHalfSize=boxSize*0.5f;
        
        rotAngle=new RagPoint(0.0f,0.0f,0.0f);
        
            // stacks of boxes
            
        stackCount=1+GeneratorMain.random.nextInt(3);
            
            // the stacks
            
        y=room.offset.y;
            
        for (stackLevel=0;stackLevel!=stackCount;stackLevel++) {
            rotAngle.setFromValues(0.0f,(-10.0f+(GeneratorMain.random.nextFloat()*20.0f)),0.0f);
            MeshUtility.createCubeRotated(meshList,room,(name+"_"+Integer.toString(storageCount)+"_"+Integer.toString(stackLevel)),"box",(x-boxHalfSize),(x+boxHalfSize),y,(y+boxSize),(z-boxHalfSize),(z+boxHalfSize),rotAngle,true,true,true,true,true,(stackLevel!=0),false,MeshUtility.UV_WHOLE,segmentSize);
            
                // go up one level

            y+=boxSize;
            if ((y+boxSize)>(room.offset.y+(segmentSize*room.storyCount))) break;
        }
    }
            
        //
        // storage
        //

    public void build()
    {
        int     x,z,lx,rx,tz,bz,
                storageCount;
        
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
        
        for (z=tz;z<bz;z++) {
            for (x=lx;x<rx;x++) {
                if (GeneratorMain.random.nextBoolean()) {
                    addBoxes(x,z,storageCount);
                    storageCount++;
                    
                    room.setGrid(0,x,z,1);
                }
            }
        }
    }

}
