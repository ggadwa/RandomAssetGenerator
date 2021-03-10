package com.klinksoftware.rag.map.outdoor;

import com.klinksoftware.rag.bitmaps.*;
import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.skeleton.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MapOutdoorBuilder
{
    private String                  basePath;
    private MeshList                meshList;
    private Skeleton                skeleton;
    private BitmapGenerator         mapBitmapList;
    
    public MapOutdoorBuilder(String basePath)
    {
        this.basePath=basePath;
    }

        //
        // build a map
        //
        
    public void build()
    {
        /*
        int                 n,k,roomCount,maxRoomCount,maxExtensionRoomCount,
                            touchIdx,failCount,placeCount,moveCount;
        float               segmentSize,storyChangePercentage,
                            roomTopY,origX,origZ,xAdd,zAdd;
        boolean             ceilings,decorations,bigRoomsOnly;
        String              mapName;
        RagPoint            centerPnt;
        MapRoom             room,connectRoom;
        ArrayList<MapRoom>  rooms;
        
            // some generator classes
        
        mapBitmapList=new BitmapGenerator(basePath);
        mapPieceList=new MapPieceList();
        
            // some settings
         
        mapName=(String)GeneratorMain.settings.get("name");
        maxRoomCount=(int)GeneratorMain.settings.get("maxRoomCount");
        maxExtensionRoomCount=(int)GeneratorMain.settings.get("maxExtensionRoomCount");
        storyChangePercentage=(float)((double)GeneratorMain.settings.get("storyChangePercentage"));
        ceilings=(boolean)GeneratorMain.settings.get("ceilings");
        decorations=(boolean)GeneratorMain.settings.get("decorations");
        bigRoomsOnly=(boolean)GeneratorMain.settings.get("bigRoomsOnly");
        segmentSize=((Double)GeneratorMain.settings.get("segmentSize")).floatValue();
        
            // map components
            
        rooms=new ArrayList<>();
        meshList=new MeshList();
        
             // first room in center of map
            
        room=new MapRoom(mapPieceList.getDefaultPiece(),segmentSize);
        room.offset.setFromValues(0.0f,0.0f,0.0f);
        rooms.add(room);
        
            // other rooms start outside of center
            // room and gravity brings them in until they connect
       
        failCount=25;
        
        while ((rooms.size()<maxRoomCount) && (failCount>0)) {
                
            room=new MapRoom(mapPieceList.getRandomPiece(bigRoomsOnly),segmentSize);
            
            placeCount=10;
            
            while (placeCount>0) {
                room.offset.x=(GeneratorMain.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE*2)-ROOM_RANDOM_LOCATION_DISTANCE)*segmentSize;
                room.offset.y=0;
                room.offset.z=(GeneratorMain.random.nextInt(ROOM_RANDOM_LOCATION_DISTANCE*2)-ROOM_RANDOM_LOCATION_DISTANCE)*segmentSize;
                if (!room.collides(rooms)) break;
                
                placeCount--;
            }
            
            if (placeCount==0) {        // could not place this anywhere, so fail this room
                failCount--;
                continue;
            }
            
                // migrate it in to center of map
  
            xAdd=-(Math.signum(room.offset.x)*segmentSize);
            zAdd=-(Math.signum(room.offset.z)*segmentSize);
            
            moveCount=ROOM_RANDOM_LOCATION_DISTANCE;
            
            while (moveCount>0) {
                origX=room.offset.x;
                origZ=room.offset.z;
                
                    // we move each chunk independently, if we can't
                    // move either x or z, then fail this room
                    
                    // if we can move, check for a touch than a shared
                    // wall, if we have one, then the room is good
                    
                room.offset.x+=xAdd;
                if (room.collides(rooms)) {
                    room.offset.x-=xAdd;
                }
                else {
                    touchIdx=room.touches(rooms);
                    if (touchIdx!=-1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            addAdditionalRoom(rooms,room,rooms.get(touchIdx),storyChangePercentage,segmentSize);
                            break;
                        }
                    }
                }
                
                room.offset.z+=zAdd;
                if (room.collides(rooms)) {
                    room.offset.z-=zAdd;
                }
                else {
                    touchIdx=room.touches(rooms);
                    if (touchIdx!=-1) {
                        if (room.hasSharedWalls(rooms.get(touchIdx))) {
                            addAdditionalRoom(rooms,room,rooms.get(touchIdx),storyChangePercentage,segmentSize);
                            break;
                        }
                    }
                }
                
                    // if we couldn't move at all, fail this room
                    
                if ((room.offset.x==origX) && (room.offset.z==origZ)) {
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
            room=new MapRoom(mapPieceList.getRandomPiece(bigRoomsOnly),segmentSize);
            
            failCount=25;
            
            while (failCount>0) {
                connectRoom=rooms.get(GeneratorMain.random.nextInt(roomCount));
                
                room.offset.setFromValues((connectRoom.offset.x-room.size.x),connectRoom.offset.y,connectRoom.offset.z);     // on left
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        addAdditionalRoom(rooms,room,connectRoom,0.0f,segmentSize);
                        break;
                    }
                }

                room.offset.setFromValues((connectRoom.offset.x+connectRoom.size.x),connectRoom.offset.y,connectRoom.offset.z);     // on right
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        addAdditionalRoom(rooms,room,connectRoom,0.0f,segmentSize);
                        break;
                    }
                }
            
                room.offset.setFromValues(connectRoom.offset.x,connectRoom.offset.y,(connectRoom.offset.z-room.size.z));     // on top
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        addAdditionalRoom(rooms,room,connectRoom,0.0f,segmentSize);
                        break;
                    }
                }
            
                room.offset.setFromValues(connectRoom.offset.x,connectRoom.offset.y,(connectRoom.offset.z+connectRoom.size.z));     // on bottom
                if (!room.collides(rooms)) {
                    if (room.hasSharedWalls(connectRoom)) {
                        addAdditionalRoom(rooms,room,connectRoom,0.0f,segmentSize);
                        break;
                    }
                }
            
                failCount--;
            }
        }

            // eliminate all combined walls
           
        removeSharedWalls(rooms,segmentSize);

            // maps always need walls, floors and ceilings
            
        mapBitmapList.generateWall();
        mapBitmapList.generateFloor();
        mapBitmapList.generateCeiling();
        
            // now create the meshes
            
        roomCount=rooms.size();
        
        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            roomTopY=room.offset.y+(room.storyCount*segmentSize);
            centerPnt=new RagPoint((room.offset.x+(room.size.x*0.5f)),(room.offset.y+((segmentSize*room.storyCount)*0.5f)),(room.offset.z+(room.size.z*0.5f)));
                
                // meshes

            meshList.add(MeshUtility.buildRoomWalls(room,centerPnt,("wall_"+Integer.toString(n)),segmentSize));
            meshList.add(MeshUtility.buildRoomFloorCeiling(room,centerPnt,("floor_"+Integer.toString(n)),"floor",room.offset.y,segmentSize));
            if (ceilings) meshList.add(MeshUtility.buildRoomFloorCeiling(room,centerPnt,("ceiling_"+Integer.toString(n)),"ceiling",roomTopY,segmentSize));
            
                // decorations

            if ((room.piece.decorate) && (decorations)) this.buildDecoration(room,n,segmentSize);
        }
        
            // any steps

        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            for (k=0;k!=room.requiredStairs.size();k++) {
                buildSteps(room,("room_"+Integer.toString(n)+"_step_"+Integer.toString(k)),room.requiredStairs.get(k),segmentSize);
            }
        }
        
            // now build the fake skeleton
            
        skeleton=meshList.rebuildMapMeshesWithSkeleton();

            // write out the model
        
        try {
            (new Export()).export(skeleton,meshList,basePath,mapName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
    }
}
