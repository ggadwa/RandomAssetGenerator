package com.klinksoftware.rag.map;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MapBuilder
{
    public static final int         ROOM_RANDOM_LOCATION_DISTANCE=100;
    public static final float       SEGMENT_SIZE=10.0f;
    public static final float       FLOOR_HEIGHT=1.0f;
    
    private MeshList                meshList;
    private Skeleton                skeleton;
    private BitmapGenerator         mapBitmapList;
    private MapPieceList            mapPieceList;

        //
        // mesh building utilities
        //
        
    private void removeSharedWalls(ArrayList<MapRoom> rooms)
    {
        int         n,k,roomCount,vIdx,vIdx2,
                    nextIdx,nextIdx2,
                    vertexCount,vertexCount2;
        float       ax,az,ax2,az2,bx,bz,bx2,bz2;
        MapRoom     room,room2;
        
            // run through ever room against every other room
            // and pull any walls that are equal as they will
            // be places the rooms connect
            
        roomCount=rooms.size();
        
        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            vertexCount=room.piece.vertexes.length;
            
            for (k=(n+1);k<roomCount;k++) {
                room2=rooms.get(k);
                vertexCount2=room2.piece.vertexes.length;
                
                vIdx=0;
                
                while (vIdx<vertexCount) {
                    nextIdx=vIdx+1;
                    if (nextIdx==vertexCount) nextIdx=0;
                                        
                    ax=(room.piece.vertexes[vIdx][0]*SEGMENT_SIZE)+room.x;
                    az=(room.piece.vertexes[vIdx][1]*SEGMENT_SIZE)+room.z;
                    
                    ax2=(room.piece.vertexes[nextIdx][0]*SEGMENT_SIZE)+room.x;
                    az2=(room.piece.vertexes[nextIdx][1]*SEGMENT_SIZE)+room.z;
                    
                    vIdx2=0;
                    
                    while (vIdx2<vertexCount2) {
                        nextIdx2=vIdx2+1;
                        if (nextIdx2==vertexCount2) nextIdx2=0;
                        
                        bx=(room2.piece.vertexes[vIdx2][0]*SEGMENT_SIZE)+room2.x;
                        bz=(room2.piece.vertexes[vIdx2][1]*SEGMENT_SIZE)+room2.z;

                        bx2=(room2.piece.vertexes[nextIdx2][0]*SEGMENT_SIZE)+room2.x;
                        bz2=(room2.piece.vertexes[nextIdx2][1]*SEGMENT_SIZE)+room2.z;
                        
                        if (((ax==bx) && (az==bz) && (ax2==bx2) && (az2==bz2)) || ((ax2==bx) && (az2==bz) && (ax==bx2) && (az==bz2))) {
                            
                                // only blank out walls that are at the same story
                                
                            if (room.story==room2.story) {
                                room.hideWall(vIdx);
                                room2.hideWall(vIdx2);
                            }
                        }
                        
                        vIdx2++;
                    }
                    
                    vIdx++;
                }
            }
        }
    }
    
        //
        // room decorations
        //
   
    private void buildDecoration(MapRoom room,int roomIdx)
    {
        /*
        int         decorationType;
        
            // some decorations can't work in some rooms
            
        while (true) {
            decorationType=GeneratorMain.random.nextInt(6);
            
                // stories only in rooms with more than one story
                
            if (decorationType==0) {
                if (room.storyCount>1) break;
                continue;
            }
            
                // equipment only in big rooms
                
            if (decorationType==3) {
                if ((room.piece.size.x==10) || (room.piece.size.z==10)) break;
                continue;
            }
            
            break;
        }
        
            // build the decoration
            
        switch (decorationType) {
            case 0:
                mapBitmapList.generatePlatform();
                (new MapStory(meshList,room,("story_"+Integer.toString(roomIdx)))).build();
                break;
            case 1:
                mapBitmapList.generatePillar();
                (new MapPillar(meshList,room,("pillar_"+Integer.toString(roomIdx)))).build();
                break;
            case 2:
                mapBitmapList.generateBox();
                mapBitmapList.generateAccessory();
                (new MapStorage(meshList,room,("storage_"+Integer.toString(roomIdx)))).build();
                break;
            case 3:
                mapBitmapList.generateComputer();
                mapBitmapList.generatePanel();
                mapBitmapList.generateMonitor();
                mapBitmapList.generatePlatform();
                mapBitmapList.generatePipe();
                mapBitmapList.generateLiquid();
                mapBitmapList.generateGlass();
                (new MapEquipment(meshList,room,("computer_"+Integer.toString(roomIdx)))).build();
                break;
            case 4:
                mapBitmapList.generatePipe();
                (new MapPipe(meshList,room,("pipe_"+Integer.toString(roomIdx)))).build();
                break;
            case 5:
                mapBitmapList.generatePlatform();
                (new MapAltar(meshList,room,("alter_"+Integer.toString(roomIdx)))).build();
                break;
        }
*/
    }

        //
        // room steps
        //
        
    private void buildSteps(MapRoom room,String name,MapRoom toRoom)
    {
        /*
        int                 x,z,noSkipX,noSkipZ,min,max;
        boolean             doAll;
        RagBound            touchRange;
        MapStory            mapStory;
        
            // force step bitmap
            
        mapBitmapList.generateStep();

            // step meshes
            
        mapStory=new MapStory(meshList,room,name);
        
        if (room.z==(toRoom.z+toRoom.size.z)) {
            touchRange=room.getTouchWallRange(toRoom,true);
            min=(int)touchRange.min;
            max=(int)touchRange.max;

            doAll=((max-min)<=2);
            noSkipX=min+GeneratorMain.random.nextInt((max+1)-min);
            
            for (x=min;x<=max;x++) {
                if ((GeneratorMain.random.nextBoolean()) || (x==noSkipX) || (doAll)) {
                    mapStory.addStairs(x,1,MeshMapUtility.STAIR_DIR_NEG_Z,0);
                }
            }
            return;
        }
        if ((room.z+room.size.z)==toRoom.z) {
            touchRange=room.getTouchWallRange(toRoom,true);
            min=(int)touchRange.min;
            max=(int)touchRange.max;
            
            doAll=((max-min)<=2);
            noSkipX=min+GeneratorMain.random.nextInt((max+1)-min);
            
            for (x=min;x<=max;x++) {
                if ((GeneratorMain.random.nextBoolean()) || (x==noSkipX) || (doAll)) {
                    mapStory.addStairs(x,(room.piece.size.z-2),MeshMapUtility.STAIR_DIR_POS_Z,0);
                }
            }
            return;
        }
        if (room.x==(toRoom.x+toRoom.size.x)) {
            touchRange=room.getTouchWallRange(toRoom,false);
            min=(int)touchRange.min;
            max=(int)touchRange.max;
            
            doAll=((max-min)<=2);
            noSkipZ=min+GeneratorMain.random.nextInt((max+1)-min);
            
            for (z=min;z<=max;z++) {
                if ((GeneratorMain.random.nextBoolean()) || (z==noSkipZ) || (doAll)) {
                    mapStory.addStairs(1,z,MeshMapUtility.STAIR_DIR_NEG_X,0);
                }
            }
            return;
        }
        if ((room.x+room.size.x)==toRoom.x) {
            touchRange=room.getTouchWallRange(toRoom,false);
            min=(int)touchRange.min;
            max=(int)touchRange.max;
            
            doAll=((max-min)<=2);
            noSkipZ=min+GeneratorMain.random.nextInt((max+1)-min);
            
            for (z=min;z<=max;z++) {
                if ((GeneratorMain.random.nextBoolean()) || (z==noSkipZ) || (doAll)) {
                    mapStory.addStairs((room.piece.size.x-2),z,MeshMapUtility.STAIR_DIR_POS_X,0);
                }
            }
            return;
        }
*/
    }

        //
        // build a single story of a room
        //
    
    private void addStory(ArrayList<MapRoom> rooms,int story)
    {
        int         n,maxRoomCount,maxExtensionRoomCount,
                    roomCount,placeCount,moveCount,failCount,
                    touchIdx;
        float       origX,origZ,xAdd,zAdd;
        MapRoom     room,connectRoom;
        
            // number of rooms on this story
            
        maxRoomCount=10+GeneratorMain.random.nextInt(15);
        maxExtensionRoomCount=GeneratorMain.random.nextInt(5);
        
             // first room in center of map
            
        room=new MapRoom(mapPieceList.getRandomPiece());
        room.x=0.0f;
        room.z=0.0f;
        room.story=story;
        rooms.add(room);
        
            // other rooms start outside of center
            // room and gravity brings them in until they connect
       
        failCount=25;
        
        while ((rooms.size()<maxRoomCount) && (failCount>0)) {
                
            room=new MapRoom(mapPieceList.getRandomPiece());
            
            placeCount=10;
            
            while (placeCount>0) {
                room.x=(GeneratorMain.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE*2)-ROOM_RANDOM_LOCATION_DISTANCE)*SEGMENT_SIZE;
                room.z=(GeneratorMain.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE*2)-ROOM_RANDOM_LOCATION_DISTANCE)*SEGMENT_SIZE;
                room.story=story;
                if (!room.collides(rooms)) break;
                
                placeCount--;
            }
            
            if (placeCount==0) {        // could not place this anywhere, so fail this room
                failCount--;
                continue;
            }
            
                // migrate it in to center of map
  
            xAdd=-(Math.signum(room.x)*SEGMENT_SIZE);
            zAdd=-(Math.signum(room.z)*SEGMENT_SIZE);
            
            moveCount=ROOM_RANDOM_LOCATION_DISTANCE;
            
            while (moveCount>0) {
                origX=room.x;
                origZ=room.z;
                
                    // we move each chunk independently, if we can't
                    // move either x or z, then fail this room
                    
                    // if we can move, check for a touch than a shared
                    // wall, if we have one, then the room is good
                    
                room.x+=xAdd;
                if (room.collides(rooms)) {
                    room.x-=xAdd;
                }
                else {
                    touchIdx=room.touches(rooms);
                    if (touchIdx!=-1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            rooms.add(room);
                            break;
                        }
                    }
                }
                
                room.z+=zAdd;
                if (room.collides(rooms)) {
                    room.z-=zAdd;
                }
                else {
                    touchIdx=room.touches(rooms);
                    if (touchIdx!=-1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            rooms.add(room);
                            break;
                        }
                    }
                }
                
                    // if we couldn't move at all, fail this room
                    
                if ((room.x==origX) && (room.z==origZ)) {
                    failCount--;
                    break;
                }
                
                moveCount--;
            }
        }
        
            // extension rooms
            // these rooms try to attach to existing rooms
            
        roomCount=rooms.size();
        
        for (n=0;n!=maxExtensionRoomCount;n++) {
            room=new MapRoom(mapPieceList.getRandomPiece());
            
            failCount=25;
            
            while (failCount>0) {
                connectRoom=rooms.get(GeneratorMain.random.nextInt(roomCount));
                
                room.x=connectRoom.x-room.sizeX;
                room.z=connectRoom.z;     // on left
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }

                room.x=connectRoom.x+connectRoom.sizeX;
                room.z=connectRoom.z;     // on right
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }
            
                room.x=connectRoom.x;
                room.z=connectRoom.z-room.sizeZ;     // on top
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }
            
                room.x=connectRoom.x;
                room.z=connectRoom.z+connectRoom.sizeZ;     // on bottom
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        rooms.add(room);
                        break;
                    }
                }
            
                failCount--;
            }
        }
    }

        //
        // build a map
        //
        
    public void build()
    {
        int                 n,k,roomCount,storyCount;
        boolean             ceilings,decorations;
        String              mapName;
        RagPoint            centerPnt;
        MapRoom             room;
        ArrayList<MapRoom>  rooms;
        
            // some generator classes
        
        mapBitmapList=new BitmapGenerator();
        mapPieceList=new MapPieceList();
        
            // some settings
         
        mapName=GeneratorMain.name;
        storyCount=1;
        ceilings=false;
        decorations=false;
        
            // map components
            
        rooms=new ArrayList<>();
        meshList=new MeshList();
        
        for (n=0;n!=storyCount;n++) {
            addStory(rooms,n);
        }

            // eliminate all combined walls
           
        removeSharedWalls(rooms);

            // maps always need walls, floors and ceilings
            
        mapBitmapList.generateWall();
        mapBitmapList.generateFloor();
        mapBitmapList.generateCeiling();
        
            // now create the meshes
            
        roomCount=rooms.size();
        
        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            centerPnt=new RagPoint((room.x+(room.sizeX*0.5f)),((room.story*(SEGMENT_SIZE+this.FLOOR_HEIGHT))+(SEGMENT_SIZE*0.5f)),(room.z+(room.sizeZ*0.5f)));
                
                // meshes

            meshList.add(MeshMapUtility.buildRoomWalls(room,centerPnt,("wall_"+Integer.toString(n))));
            meshList.add(MeshMapUtility.buildRoomFloorCeiling(room,centerPnt,("floor_"+Integer.toString(n)),"floor",true));
            if (ceilings) meshList.add(MeshMapUtility.buildRoomFloorCeiling(room,centerPnt,("ceiling_"+Integer.toString(n)),"ceiling",false));
            
                // decorations

            if ((room.piece.decorate) && (decorations)) this.buildDecoration(room,n);
        }
        
            // any steps

        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            for (k=0;k!=room.requiredStairs.size();k++) {
                buildSteps(room,("room_"+Integer.toString(n)+"_step_"+Integer.toString(k)),room.requiredStairs.get(k));
            }
        }
        
            // now build the fake skeleton
            
        skeleton=meshList.rebuildMapMeshesWithSkeleton();

            // write out the model
        
        try {
            (new Export()).export(skeleton,meshList,GeneratorMain.basePath,mapName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
