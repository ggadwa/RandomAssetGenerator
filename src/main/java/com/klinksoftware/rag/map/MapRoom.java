package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;

public class MapRoom
{
    public int                  story;
    public float                x,z,sizeX,sizeZ;
    public byte[]               wallHideArray;
    public int[]                grid,floorGrid,ceilingGrid;
    public MapPiece             piece;
    public ArrayList<MapRoom>   requiredStairs;
    
    public MapRoom(MapPiece piece)
    {
        this.piece=piece;

        this.story=0;
        
        this.x=0.0f;
        this.z=0.0f;
        this.sizeX=MapBuilder.SEGMENT_SIZE*piece.sizeX;
        this.sizeZ=MapBuilder.SEGMENT_SIZE*piece.sizeZ;
        
            // need a copy of floor grid
            
        floorGrid=piece.floorGrid.clone();
        ceilingGrid=piece.floorGrid.clone();

            // flags for staircases
            
        requiredStairs=new ArrayList<>();

            // wall hiding
            
        wallHideArray=new byte[piece.vertexes.length];
        
            // grids for blocking off floor/stories/etc
            
        grid=new int[piece.sizeX*piece.sizeZ];
    }
    
        //
        // collisions and touches with room boxes
        //
     
    public boolean collides(ArrayList<MapRoom> rooms)
    {
        int         n;
        MapRoom     checkRoom;
        
        for (n=0;n!=rooms.size();n++) {
            checkRoom=rooms.get(n);
            if (checkRoom.story!=story) continue;
            
            if (x>=(checkRoom.x+checkRoom.sizeX)) continue;
            if ((x+sizeX)<=checkRoom.x) continue;
            if (z>=(checkRoom.z+checkRoom.sizeZ)) continue;
            if ((z+sizeZ)<=checkRoom.z) continue;
            
            return(true);
        }
        
        return(false);
    }
 
    public int touches(ArrayList<MapRoom> rooms)
    {
        int         n;
        MapRoom     checkRoom;
        
        for (n=0;n!=rooms.size();n++) {
            checkRoom=rooms.get(n);
            if (checkRoom.story!=story) continue;
            
            if ((x==(checkRoom.x+checkRoom.sizeX)) || ((x+sizeX)==checkRoom.x)) {
                if (z>=(checkRoom.z+checkRoom.sizeZ)) continue;
                if ((z+sizeZ)<=checkRoom.z) continue;
                return(n);
            }
            
            if ((z==(checkRoom.z+checkRoom.sizeZ)) || ((z+sizeZ)==checkRoom.z)) {
                if (x>=(checkRoom.x+checkRoom.sizeX)) continue;
                if ((x+sizeX)<=checkRoom.x) continue;
                return(n);
            }
        }
        
        return(-1);
    }
    
        //
        // shared/touching walls
        //
        
    public boolean hasSharedWalls(MapRoom checkRoom)
    {
        int         vIdx,vIdx2,nextIdx,nextIdx2,
                    vertexCount,vertexCount2;
        float       ax,az,ax2,az2,bx,bz,bx2,bz2;
        
            // check to see if two rooms share a wall segment
        
        vertexCount=piece.vertexes.length;
        vertexCount2=checkRoom.piece.vertexes.length;
                
        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax=(piece.vertexes[vIdx][0]*MapBuilder.SEGMENT_SIZE)+x;
            az=(piece.vertexes[vIdx][1]*MapBuilder.SEGMENT_SIZE)+z;

            ax2=(piece.vertexes[nextIdx][0]*MapBuilder.SEGMENT_SIZE)+x;
            az2=(piece.vertexes[nextIdx][1]*MapBuilder.SEGMENT_SIZE)+z;

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=(checkRoom.piece.vertexes[vIdx2][0]*MapBuilder.SEGMENT_SIZE)+checkRoom.x;
                bz=(checkRoom.piece.vertexes[vIdx2][1]*MapBuilder.SEGMENT_SIZE)+checkRoom.z;

                bx2=(checkRoom.piece.vertexes[nextIdx2][0]*MapBuilder.SEGMENT_SIZE)+checkRoom.x;
                bz2=(checkRoom.piece.vertexes[nextIdx2][1]*MapBuilder.SEGMENT_SIZE)+checkRoom.z;

                if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) return(true);

                vIdx2++;
            }

            vIdx++;
        }
        
        return(false);
    }
    
    public RagBound getTouchWallRange(MapRoom checkRoom,boolean xRun)
    {
        int                 n,vIdx,vIdx2,nextIdx,nextIdx2,
                            vertexCount,vertexCount2;
        float               f,ax,az,ax2,az2,bx,bz,bx2,bz2,
                            touchMin,touchMax;
        ArrayList<Float>    touchPoints;

        touchPoints=new ArrayList<>();
        
            // find all the touching wall segements
        
        vertexCount=piece.vertexes.length;
        vertexCount2=checkRoom.piece.vertexes.length;
                
        vIdx=0;

        while (vIdx<vertexCount) {
            nextIdx=vIdx+1;
            if (nextIdx==vertexCount) nextIdx=0;

            ax=(piece.vertexes[vIdx][0]*MapBuilder.SEGMENT_SIZE)+x;
            az=(piece.vertexes[vIdx][1]*MapBuilder.SEGMENT_SIZE)+z;

            ax2=(piece.vertexes[nextIdx][0]*MapBuilder.SEGMENT_SIZE)+x;
            az2=(piece.vertexes[nextIdx][1]*MapBuilder.SEGMENT_SIZE)+z;

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=(checkRoom.piece.vertexes[vIdx2][0]*MapBuilder.SEGMENT_SIZE)+checkRoom.x;
                bz=(checkRoom.piece.vertexes[vIdx2][1]*MapBuilder.SEGMENT_SIZE)+checkRoom.z;

                bx2=(checkRoom.piece.vertexes[nextIdx2][0]*MapBuilder.SEGMENT_SIZE)+checkRoom.x;
                bz2=(checkRoom.piece.vertexes[nextIdx2][1]*MapBuilder.SEGMENT_SIZE)+checkRoom.z;

                if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) {
                    if (xRun) {
                        touchPoints.add(Math.min(piece.vertexes[vIdx][0],piece.vertexes[nextIdx][0]));   // always use the min, as stairs draw from there
                    }
                    else {
                         touchPoints.add(Math.min(piece.vertexes[vIdx][1],piece.vertexes[nextIdx][1]));
                    }
                }

                vIdx2++;
            }

            vIdx++;
        }
        
            // now convert into x or z runs
            
        if (touchPoints.isEmpty()) return(null);
            
        touchMin=touchMax=touchPoints.get(0);

        for (n=1;n<touchPoints.size();n++) {
            f=touchPoints.get(n);
            if (f<touchMin) touchMin=f;
            if (f>touchMax) touchMax=f;
        }
        
        return(new RagBound(touchMin,touchMax));
    }
    
        //
        // hiding walls
        // the vertex offset is the first vertex of the
        // the wall (ascending) to hide
        //
    
    public void hideWall(int vertexOffset)
    {
        wallHideArray[vertexOffset]=0x1;
    }
    
    public boolean isWallHidden(int vertexOffset)
    {
        return(wallHideArray[vertexOffset]==0x1);
    }
    
        //
        // grids (for marking off areas used by things)
        //
    
    public void setGrid(int x,int z,int flag)
    {
        grid[(piece.sizeX*piece.sizeZ)+(z*piece.sizeZ)+x]=flag;
    }
    
    public int getGrid(int x,int z)
    {
        return(grid[(piece.sizeX*piece.sizeZ)+(z*piece.sizeZ)+x]);
    }

}
