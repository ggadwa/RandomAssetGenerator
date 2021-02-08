package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.utility.*;
import java.util.ArrayList;

public class MapRoom
{
    public static final int     MAX_STORIES=5;
    
    private float               segmentSize;
    
    public int                  storyCount;
    public byte[]               vertexHideArray;
    public int[]                grid;
    public MapPiece             piece;
    public RagPoint             offset,size;
    public ArrayList<MapRoom>   requiredStairs;
    
    public MapRoom(MapPiece piece,float segmentSize)
    {
        this.piece=piece;
        this.segmentSize=segmentSize;

        this.storyCount=this.piece.storyMinimum+GeneratorMain.random.nextInt(MAX_STORIES-this.piece.storyMinimum);
        
        this.offset=new RagPoint(0.0f,0.0f,0.0f);
        this.size=new RagPoint((segmentSize*piece.size.x),(segmentSize*this.storyCount),(segmentSize*piece.size.z));

            // flags for staircases
            
        requiredStairs=new ArrayList<>();

            // vertex hiding array, had 3 possible stories
            
        vertexHideArray=new byte[piece.vertexes.length*MAX_STORIES];
        
            // grids for blocking off floor/stories/etc
            
        grid=new int[(piece.size.x*piece.size.z)*MAX_STORIES];
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
            
            if (offset.x>=(checkRoom.offset.x+checkRoom.size.x)) continue;
            if ((offset.x+size.x)<=checkRoom.offset.x) continue;
            if (offset.z>=(checkRoom.offset.z+checkRoom.size.z)) continue;
            if ((offset.z+size.z)<=checkRoom.offset.z) continue;
            
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
            
            if ((offset.x==(checkRoom.offset.x+checkRoom.size.x)) || ((offset.x+size.x)==checkRoom.offset.x)) {
                if (offset.z>=(checkRoom.offset.z+checkRoom.size.z)) continue;
                if ((offset.z+size.z)<=checkRoom.offset.z) continue;
                return(n);
            }
            
            if ((offset.z==(checkRoom.offset.z+checkRoom.size.z)) || ((offset.z+size.z)==checkRoom.offset.z)) {
                if (offset.x>=(checkRoom.offset.x+checkRoom.size.x)) continue;
                if ((offset.x+size.x)<=checkRoom.offset.x) continue;
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

            ax=(piece.vertexes[vIdx][0]*segmentSize)+offset.x;
            az=(piece.vertexes[vIdx][1]*segmentSize)+offset.z;

            ax2=(piece.vertexes[nextIdx][0]*segmentSize)+offset.x;
            az2=(piece.vertexes[nextIdx][1]*segmentSize)+offset.z;

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=(checkRoom.piece.vertexes[vIdx2][0]*segmentSize)+checkRoom.offset.x;
                bz=(checkRoom.piece.vertexes[vIdx2][1]*segmentSize)+checkRoom.offset.z;

                bx2=(checkRoom.piece.vertexes[nextIdx2][0]*segmentSize)+checkRoom.offset.x;
                bz2=(checkRoom.piece.vertexes[nextIdx2][1]*segmentSize)+checkRoom.offset.z;

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

            ax=(piece.vertexes[vIdx][0]*segmentSize)+offset.x;
            az=(piece.vertexes[vIdx][1]*segmentSize)+offset.z;

            ax2=(piece.vertexes[nextIdx][0]*segmentSize)+offset.x;
            az2=(piece.vertexes[nextIdx][1]*segmentSize)+offset.z;

            vIdx2=0;

            while (vIdx2<vertexCount2) {
                nextIdx2=vIdx2+1;
                if (nextIdx2==vertexCount2) nextIdx2=0;

                bx=(checkRoom.piece.vertexes[vIdx2][0]*segmentSize)+checkRoom.offset.x;
                bz=(checkRoom.piece.vertexes[vIdx2][1]*segmentSize)+checkRoom.offset.z;

                bx2=(checkRoom.piece.vertexes[nextIdx2][0]*segmentSize)+checkRoom.offset.x;
                bz2=(checkRoom.piece.vertexes[nextIdx2][1]*segmentSize)+checkRoom.offset.z;

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
        // vertexes
        //
    
    public void hideVertex(int story,int vIdx)
    {
        vertexHideArray[(story*piece.vertexes.length)+vIdx]=0x1;
    }
    
    public boolean isWallHidden(int story,int vIdx)
    {
        return(vertexHideArray[(story*piece.vertexes.length)+vIdx]==0x1);
    }
    
        //
        // grids (for marking off areas used by things)
        //
    
    public void setGrid(int story,int x,int z,int flag)
    {
        grid[((piece.size.x*piece.size.z)*story)+(z*piece.size.z)+x]=flag;
    }
    
    public void setGridAllStories(int x,int z,int flag)
    {
        int     n;
        
        for (n=0;n!=(storyCount+1);n++) {
            setGrid(n,x,z,flag);
        }
    }
    
    public int getGrid(int story,int x,int z)
    {
        return(grid[((piece.size.x*piece.size.z)*story)+(z*piece.size.z)+x]);
    }

}
