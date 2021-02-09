package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.mesh.*;

import java.util.*;

public class MapStory
{
    public static final int PLATFORM_DIR_POS_Z=0;
    public static final int PLATFORM_DIR_NEG_Z=1;
    public static final int PLATFORM_DIR_POS_X=2;
    public static final int PLATFORM_DIR_NEG_X=3;
        
    public static final int FLAG_NONE=0;
    public static final int FLAG_STEPS=1;
    public static final int FLAG_PLATFORM=2;
    public static final int FLAG_WALL=3;
    
    private float               segmentSize;
    private String              name;
    private MeshList            meshList;
    private MapRoom             room;

    public MapStory(MeshList meshList,MapRoom room,String name,float segmentSize)
    {
        this.meshList=meshList;
        this.room=room;
        this.name=name;
        this.segmentSize=segmentSize;
    }
    
        //
        // stairs
        //
    
    public void addStairs(int x,int z,int dir,int storyIdx)
    {
        float           y,floorHigh;
        
        floorHigh=segmentSize*0.1f;
        y=room.offset.y+(storyIdx*segmentSize)+(floorHigh*storyIdx);
        
        switch (dir)
        {
            case MeshUtility.STAIR_DIR_POS_Z:
                room.setGridAllStories(x,z,FLAG_STEPS);
                room.setGridAllStories(x,(z+1),FLAG_STEPS);
                if ((z-1)>=0) room.setGridAllStories(x,(z-1),FLAG_STEPS);   // area ahead of steps
                break;
            case MeshUtility.STAIR_DIR_NEG_Z:
                room.setGridAllStories(x,z,FLAG_STEPS);
                room.setGridAllStories(x,(z-1),FLAG_STEPS);
                if ((z+1)<(room.piece.size.z-1)) room.setGridAllStories(x,(z+1),FLAG_STEPS);   // area ahead of steps
                break;
            case MeshUtility.STAIR_DIR_POS_X:
                room.setGridAllStories(x,z,FLAG_STEPS);
                room.setGridAllStories((x+1),z,FLAG_STEPS);
                if ((x-1)>=0) room.setGridAllStories((x-1),z,FLAG_STEPS);   // area ahead of steps
                break;
            case MeshUtility.STAIR_DIR_NEG_X:
                room.setGridAllStories(x,z,FLAG_STEPS);
                room.setGridAllStories((x-1),z,FLAG_STEPS);
                if ((x+1)<(room.piece.size.x-1)) room.setGridAllStories((x+1),z,FLAG_STEPS);   // area ahead of steps
                break;
        }
        
        MeshUtility.buildStairs(meshList,room,name,(room.offset.x+((float)x*segmentSize)),y,(room.offset.z+((float)z*segmentSize)),dir,1.0f,true,segmentSize);
    }
    
        //
        // second story segments
        //
       
    private boolean hasNegXWall(int storyIdx,int x,int z)
    {
        int     flag,flag2;
        
        if (x==0) return(true);

        flag=room.getGrid(storyIdx,x,z);
        flag2=room.getGrid(storyIdx,(x-1),z);
        
        if ((flag2==FLAG_NONE) || (flag2==FLAG_STEPS)) return(true);
        if (flag==flag2) return(false);           // if both the same type of wall, eliminate
        if ((flag==FLAG_PLATFORM) && (flag2==FLAG_WALL)) return(false);     // if short wall and other is tall wall, then eliminate
        return(true);
    }
    
    private boolean hasPosXWall(int storyIdx,int x,int z)
    {
        int     flag,flag2;
        
        if (x==(this.room.piece.size.x-1)) return(true);
        
        flag=room.getGrid(storyIdx,x,z);
        flag2=room.getGrid(storyIdx,(x+1),z);
        
        if ((flag2==FLAG_NONE) || (flag2==FLAG_STEPS)) return(true);
        if (flag==flag2) return(false);           // if both the same type of wall, eliminate
        if ((flag==FLAG_PLATFORM) && (flag2==FLAG_WALL)) return(false);     // if short wall and other is tall wall, then eliminate
        return(true);
    }
    
    private boolean hasNegZWall(int storyIdx,int x,int z)
    {
        int     flag,flag2;
        
        if (z==0) return(true);
        
        flag=room.getGrid(storyIdx,x,z);
        flag2=room.getGrid(storyIdx,x,(z-1));
        
        if ((flag2==FLAG_NONE) || (flag2==FLAG_STEPS)) return(true);
        if (flag==flag2) return(false);           // if both the same type of wall, eliminate
        if ((flag==FLAG_PLATFORM) && (flag2==FLAG_WALL)) return(false);     // if short wall and other is tall wall, then eliminate
        return(true);
    }
    
    private boolean hasPosZWall(int storyIdx,int x,int z)
    {
        int     flag,flag2;
        
        if (z==(this.room.piece.size.z-1)) return(true);
        
        flag=room.getGrid(storyIdx,x,z);
        flag2=room.getGrid(storyIdx,x,(z+1));
        
        if ((flag2==FLAG_NONE) || (flag2==FLAG_STEPS)) return(true);
        if (flag==flag2) return(false);           // if both the same type of wall, eliminate
        if ((flag==FLAG_PLATFORM) && (flag2==FLAG_WALL)) return(false);     // if short wall and other is tall wall, then eliminate
        return(true);
    }
    
    private void setupRandomPlatforms(int startX,int startZ,int storyIdx)
    {
        int         x,z,gx,gz,sx,sz,
                    dir,orgDir;
        boolean     wallStop;
        
        gx=startX;
        gz=startZ;
        
            // start the random wander of segments

        while (true) {

                // next random direction
                
            dir=GeneratorMain.random.nextInt(4);
            orgDir=dir;
            
                // find open direction
                
            sx=sz=0;
            wallStop=false;
            
            while (true) {

                switch (dir) {
                    case PLATFORM_DIR_POS_Z:
                        sx=gx;
                        sz=gz+1;
                        break;
                    case PLATFORM_DIR_NEG_Z:
                        sx=gx;
                        sz=gz-1;
                        break;
                    case PLATFORM_DIR_POS_X:
                        sx=gx+1;
                        sz=gz;
                        break;
                    case PLATFORM_DIR_NEG_X:
                        sx=gx-1;
                        sz=gz;
                        break;
                }

                if ((room.getGrid(storyIdx,sx,sz)!=this.FLAG_NONE) || (sx<0) || (sx>=room.piece.size.x) || (sz<0) || (sz>=room.piece.size.z)) {
                    dir++;
                    if (dir==4) dir=0;
                    if (dir==orgDir) {
                        wallStop=true;
                        break;
                    }
                }
                else {
                    break;
                }
            }
            
            if (wallStop) break;
            
                // add grid spot
                
            room.setGrid(storyIdx,sx,sz,FLAG_PLATFORM);
            
            gx=sx;
            gz=sz;
        }

            // randomly make stripes where stories
            // become solid walls instead of floating blocks
            // only do it on first story
            
        if (storyIdx==1) {
            for (x=1;x<(room.piece.size.x-1);x++) {
                if (GeneratorMain.random.nextFloat()<0.2f) {
                    for (z=1;z<(room.piece.size.z-1);z++) {
                        if (room.getGrid(storyIdx,x,z)==FLAG_PLATFORM) room.setGrid(storyIdx,x,z,FLAG_WALL);
                    }
                }
            }

            for (z=1;z<(room.piece.size.z-1);z++) {
                if (GeneratorMain.random.nextFloat()<0.2f) {
                    for (x=1;x<(room.piece.size.x-1);x++) {
                        if (room.getGrid(storyIdx,x,z)==FLAG_PLATFORM) room.setGrid(storyIdx,x,z,FLAG_WALL);
                    }
                }
            }
        }
    }
    
    private void addPlatforms(int storyIdx)
    {
        int                 x,z,trigIdx,flag;
        float               ty,by,negX,posX,negZ,posZ,floorHigh;
        boolean             skipBottom;
        ArrayList<Float>    vertexArray,normalArray;
        ArrayList<Integer>  indexArray;
        float[]             vertexes,normals,uvs;
        int[]               indexes;
        
            // setup the buffers
            
        vertexArray=new ArrayList<>();
        normalArray=new ArrayList<>();
        indexArray=new ArrayList<>();
        
            // make the segments
        
        trigIdx=0;
        floorHigh=segmentSize*0.1f;
        
        for (z=0;z!=room.piece.size.z;z++) {
            for (x=0;x!=room.piece.size.x;x++) {
                flag=room.getGrid(storyIdx,x,z);
                if ((flag!=FLAG_PLATFORM) && (flag!=FLAG_WALL)) continue;

                    // create the segments
                    
                if (flag==FLAG_PLATFORM) {
                    ty=room.offset.y+((segmentSize*(float)storyIdx)+floorHigh);
                    by=room.offset.y+(segmentSize*(float)storyIdx);
                    skipBottom=false;
                }
                else {
                    ty=room.offset.y+((segmentSize*(float)storyIdx)+floorHigh);
                    by=room.offset.y+(segmentSize*(float)(storyIdx-1));
                    skipBottom=true;
                }
                
                negX=room.offset.x+(x*segmentSize);
                posX=room.offset.x+((x+1)*segmentSize);
                negZ=room.offset.z+(z*segmentSize);
                posZ=room.offset.z+((z+1)*segmentSize);
                
                if (hasNegXWall(storyIdx,x,z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,negX,by,posZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f));
                    trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);
                }
                
                if (hasPosXWall(storyIdx,x,z)) {
                    vertexArray.addAll(Arrays.asList(posX,ty,negZ,posX,ty,posZ,posX,by,posZ,posX,by,negZ));
                    normalArray.addAll(Arrays.asList(1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f));
                    trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);
                }
                
                    // always draw the top
                    
                vertexArray.addAll(Arrays.asList(negX,ty,negZ,negX,ty,posZ,posX,ty,posZ,posX,ty,negZ));
                normalArray.addAll(Arrays.asList(0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f));
                trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);

                if (!skipBottom) {
                    vertexArray.addAll(Arrays.asList(negX,by,negZ,negX,by,posZ,posX,by,posZ,posX,by,negZ));
                    normalArray.addAll(Arrays.asList(0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f));
                    trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);
                }
                
                if (hasNegZWall(storyIdx,x,z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,negZ,posX,ty,negZ,posX,by,negZ,negX,by,negZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,-1.0f));
                    trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);
                }
                
                if (hasPosZWall(storyIdx,x,z)) {
                    vertexArray.addAll(Arrays.asList(negX,ty,posZ,posX,ty,posZ,posX,by,posZ,negX,by,posZ));
                    normalArray.addAll(Arrays.asList(0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f,0.0f,0.0f,1.0f));
                    trigIdx=MeshUtility.addQuadToIndexes(indexArray,trigIdx);
                }
            }
        }
        
        vertexes=MeshUtility.floatArrayListToFloat(vertexArray);
        normals=MeshUtility.floatArrayListToFloat(normalArray);
        indexes=MeshUtility.intArrayListToInt(indexArray);
        uvs=MeshUtility.buildUVs(vertexes,normals,(1.0f/segmentSize));

        meshList.add(new Mesh(name,"platform",vertexes,normals,uvs,indexes,false));
    }

        //
        // second story mainline
        //
        
    public void build()
    {
        int         n,x,z,dir;
        
        
        x=room.piece.size.x/2;
        z=room.piece.size.z/2;
        dir=GeneratorMain.random.nextInt(4);
        
        x=2+GeneratorMain.random.nextInt(room.piece.size.x-4);
        z=2+GeneratorMain.random.nextInt(room.piece.size.z-4);
        
        addStairs(x,z,dir,0);
        
        switch (dir)
        {
            case MeshUtility.STAIR_DIR_POS_Z:
                room.setGridAllStories(x,(z-1),FLAG_STEPS);
                z+=2;
                break;
            case MeshUtility.STAIR_DIR_NEG_Z:
                room.setGridAllStories(x,(z+-1),FLAG_STEPS);
                z-=2;
                break;
            case MeshUtility.STAIR_DIR_POS_X:
                room.setGridAllStories((x-1),z,FLAG_STEPS);
                x+=2;
                break;
            case MeshUtility.STAIR_DIR_NEG_X:
                room.setGridAllStories((x+1),z,FLAG_STEPS);
                x-=2;
                break;
        }
        
        room.setGrid(1,x,z,FLAG_WALL);

            // starting position
            
        
        
            // build the story segments
            
        //for (n=1;n<room.storyCount;n++) {
            setupRandomPlatforms(x,z,1);
            addPlatforms(1);
        //}

    }
}
