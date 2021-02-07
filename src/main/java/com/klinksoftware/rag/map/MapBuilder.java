package com.klinksoftware.rag.map;

import com.klinksoftware.rag.*;
import com.klinksoftware.rag.export.*;
import com.klinksoftware.rag.mesh.*;
import com.klinksoftware.rag.utility.*;

import java.util.*;

public class MapBuilder
{
    public static final int         ROOM_RANDOM_LOCATION_DISTANCE=100;
    
    public static float             segmentSize=10.0f;
    
    private String                  basePath;
    private MeshList                meshList;
    private MapBitmapList           mapBitmapList;
    private MapPieceList            mapPieceList;
    
    public MapBuilder(String basePath)
    {
        this.basePath=basePath;
    }

/*
    
    
    
        //
        // mesh building utilities
        //
        
    removeSharedWalls(rooms,segmentSize)
    {
        let n,k,t,y,room,room2;
        let vIdx,vIdx2,nextIdx,nextIdx2,nVertex,nVertex2;
        let ax,az,ax2,az2,bx,bz,bx2,bz2;
        let nRoom=rooms.length;
        
            // run through ever room against every other room
            // and pull any walls that are equal as they will
            // be places the rooms connect
        
        for (n=0;n!==nRoom;n++) {
            room=rooms[n];
            nVertex=room.piece.vertexes.length;
            
            for (k=(n+1);k<nRoom;k++) {
                room2=rooms[k];
                nVertex2=room2.piece.vertexes.length;
                
                vIdx=0;
                
                while (vIdx<nVertex) {
                    nextIdx=vIdx+1;
                    if (nextIdx===nVertex) nextIdx=0;
                                        
                    ax=Math.trunc(room.piece.vertexes[vIdx][0]*segmentSize)+room.offset.x
                    az=Math.trunc(room.piece.vertexes[vIdx][1]*segmentSize)+room.offset.z
                    
                    ax2=Math.trunc(room.piece.vertexes[nextIdx][0]*segmentSize)+room.offset.x
                    az2=Math.trunc(room.piece.vertexes[nextIdx][1]*segmentSize)+room.offset.z
                    
                    vIdx2=0;
                    
                    while (vIdx2<nVertex2) {
                        nextIdx2=vIdx2+1;
                        if (nextIdx2===nVertex2) nextIdx2=0;
                        
                        bx=Math.trunc(room2.piece.vertexes[vIdx2][0]*segmentSize)+room2.offset.x
                        bz=Math.trunc(room2.piece.vertexes[vIdx2][1]*segmentSize)+room2.offset.z

                        bx2=Math.trunc(room2.piece.vertexes[nextIdx2][0]*segmentSize)+room2.offset.x
                        bz2=Math.trunc(room2.piece.vertexes[nextIdx2][1]*segmentSize)+room2.offset.z
                        
                        if (((ax===bx) && (az===bz) && (ax2===bx2) && (az2===bz2)) || ((ax2===bx) && (az2===bz) && (ax===bx2) && (az===bz2))) {
                            
                                // only blank out walls that are within the
                                // bounds of the other rooms y size
                                
                            for (t=0;t!==room.storyCount;t++) {
                                y=room.offset.y+(t*segmentSize);
                                if ((y>=(room2.offset.y+(room2.storyCount*segmentSize))) || ((y+segmentSize)<=room2.offset.y)) continue;
                                
                                room.hideVertex(t,vIdx);
                            }
                            for (t=0;t!==room2.storyCount;t++) {
                                y=room2.offset.y+(t*segmentSize);
                                if ((y>=(room.offset.y+(room.storyCount*segmentSize))) || ((y+segmentSize)<=room.offset.y)) continue;
                                
                                room2.hideVertex(t,vIdx2);
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
        
    buildDecoration(room,roomIdx,genMesh,genBitmap,segmentSize)
    {
            // build the decoration
            
        switch (this.core.randomIndex(6)) {
            case 0:
                (new GenerateStoryClass(this.core,room,('story_'+roomIdx),genMesh,genBitmap.generateStep(),genBitmap.generatePlatform(),segmentSize)).build();
                break;
            case 1:
                (new GeneratePillarClass(this.core,room,('pillar_'+roomIdx),genMesh,genBitmap.generatePillar(),segmentSize)).build();
                break;
            case 2:
                (new GenerateStorageClass(this.core,room,('storage'+roomIdx),genMesh,genBitmap.generateBox(),segmentSize)).build();
                break;
            case 3:
                (new GenerateComputerClass(this.core,room,('computer_'+roomIdx),genMesh,genBitmap.generatePlatform(),genBitmap.generateComputer(),segmentSize)).build();
                break;
            case 4:
                (new GeneratePipeClass(this.core,room,('pipe_'+roomIdx),genMesh,genBitmap.generatePipe(),segmentSize)).build();
                break;
            case 5:
                (new GenerateAltarClass(this.core,room,('alter_'+roomIdx),genMesh,genBitmap.generatePlatform(),segmentSize)).build();
                break;
        }
    }
    
        //
        // room steps
        //
        
    buildSteps(core,room,name,toRoom,genMesh,stepBitmap,segmentSize)
    {
        let x,z,doAll,touchRange;
        let noSkipX,noSkipZ;
        let genStory=new GenerateStoryClass(core,room,name,genMesh,stepBitmap,null,segmentSize);
        
        if (room.offset.z===(toRoom.offset.z+toRoom.size.z)) {
            touchRange=room.getTouchWallRange(toRoom,true,segmentSize);
            doAll=(touchRange.getSize()<=2);
            noSkipX=this.core.randomInBetween(touchRange.min,(touchRange.max+1));
            
            for (x=touchRange.min;x<=touchRange.max;x++) {
                if ((this.core.randomPercentage(0.5)) || (x===noSkipX) || (doAll)) {
                    genStory.addStairs(x,0,genStory.PLATFORM_DIR_NEG_Z,0);
                }
            }
            return;
        }
        if ((room.offset.z+room.size.z)===toRoom.offset.z) {
            touchRange=room.getTouchWallRange(toRoom,true,segmentSize);
            doAll=(touchRange.getSize()<=2);
            noSkipX=this.core.randomInBetween(touchRange.min,(touchRange.max+1));
            
            for (x=touchRange.min;x<=touchRange.max;x++) {
                if ((this.core.randomPercentage(0.5)) || (x===noSkipX) || (doAll)) {
                    genStory.addStairs(x,room.piece.size.z-2,genStory.PLATFORM_DIR_POS_Z,0);
                }
            }
            return;
        }
        if (room.offset.x===(toRoom.offset.x+toRoom.size.x)) {
            touchRange=room.getTouchWallRange(toRoom,false,segmentSize);
            doAll=(touchRange.getSize()<=2);
            noSkipZ=this.core.randomInBetween(touchRange.min,(touchRange.max+1));
            
            for (z=touchRange.min;z<=touchRange.max;z++) {
                if ((this.core.randomPercentage(0.5)) || (z===noSkipZ) || (doAll)) {
                    genStory.addStairs(0,z,genStory.PLATFORM_DIR_NEG_X,0);
                }
            }
            return;
        }
        if ((room.offset.x+room.size.x)===toRoom.offset.x) {
            touchRange=room.getTouchWallRange(toRoom,false,segmentSize);
            doAll=(touchRange.getSize()<=2);
            noSkipZ=this.core.randomInBetween(touchRange.min,(touchRange.max+1));
            
            for (z=touchRange.min;z<=touchRange.max;z++) {
                if ((this.core.randomPercentage(0.5)) || (z===noSkipZ) || (doAll)) {
                    genStory.addStairs(room.piece.size.x-2,z,genStory.PLATFORM_DIR_POS_X,0);
                }
            }
            return;
        }
        
    }
    */
        //
        // add additional room
        //
    
    private void addAdditionalRoom(ArrayList<MapRoom> rooms,MapRoom room,MapRoom touchRoom)
    {
            // start at same height
            
        room.offset.y=touchRoom.offset.y;
        
            // can we change height?
            
        if ((room.offset.y==0) && (touchRoom.piece.decorate) && (touchRoom.storyCount>1)) {
            if (GeneratorMain.random.nextFloat()<0.25) {
                room.offset.y+=segmentSize;
                touchRoom.requiredStairs.add(room);
            }
        }
                            
            // add the room
                            
        rooms.add(room);
    }  

        //
        // build a map
        //
        
    public void build(String mapName,int roomCount)
    {
        int                 n,failCount,placeCount,moveCount,
                            touchIdx;
        float               roomTopY,origX,origZ,xAdd,zAdd;
        RagPoint            centerPnt;
        MapRoom             room;
        ArrayList<MapRoom>  rooms;
        
            // some generator classes
        
        mapBitmapList=new MapBitmapList(basePath);
        mapPieceList=new MapPieceList();
        
            // no rooms or meshes
            
        rooms=new ArrayList<>();
        meshList=new MeshList();
        
             // first room in center of map
            
        room=new MapRoom(mapPieceList.getDefaultPiece());
        room.offset.setFromValues(0.0f,0.0f,0.0f);
        rooms.add(room);
        
            // other rooms start outside of center
            // room and gravity brings them in until they connect
       
        failCount=25;
        
        while ((rooms.size()<roomCount) && (failCount>0)) {
                
            room=new MapRoom(mapPieceList.getRandomPiece());
            
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
                            addAdditionalRoom(rooms,room,rooms.get(touchIdx));
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
                            addAdditionalRoom(rooms,room,rooms.get(touchIdx));
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

            // eliminate all combined walls
/*            
        this.removeSharedWalls(rooms,segmentSize);
*/      

            // maps always need walls, floors, ceilings
            // and steps
            
        mapBitmapList.generateWall();
        mapBitmapList.generateFloor();
        mapBitmapList.generateCeiling();
        mapBitmapList.generateStep();
        
            // now create the meshes
            
        roomCount=rooms.size();
        
        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            roomTopY=room.offset.y+(room.storyCount*segmentSize);
            centerPnt=new RagPoint((room.offset.x+(room.size.x*0.5f)),(room.offset.y+((segmentSize*room.storyCount)*0.5f)),(room.offset.z+(room.size.z*0.5f)));
                
                // meshes

            MeshUtility.buildRoomWalls(meshList,room,centerPnt,("wall_"+Integer.toString(n)));
            //genMesh.buildRoomFloorCeiling(room,centerPnt,("floor_"+Integer.toString(n)),genBitmap.generateFloor(),room.offset.y,segmentSize);
            //genMesh.buildRoomFloorCeiling(room,centerPnt,("ceiling_"+Integer.toString(n)),genBitmap.generateCeiling(),roomTopY,segmentSize);
            
                // decorations

        //    if (room.piece.decorate) this.buildDecoration(room,n,genMesh,genBitmap,segmentSize);
            
                // room lights

        //    (new GenerateLightClass(this.core,room,('light_'+n),genMesh,genBitmap.generateStep(),segmentSize)).build();
        }
        
            // any steps
 /*           
        for (n=0;n!=roomCount;n++) {
            room=rooms.get(n);
            
            for (k=0;k!==room.requiredStairs.length;k++) {
                this.buildSteps(this.core,room,('room_'+n+'_step_'+k),room.requiredStairs[k],genMesh,genBitmap.generateStep(),segmentSize);
            }
        }
*/        


        
            // write out the model
        
        try {
            (new Export()).export(meshList,basePath,mapName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
